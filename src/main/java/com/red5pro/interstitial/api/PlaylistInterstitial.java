package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.red5.server.api.IContext;
import org.red5.server.api.scope.IScope;
import org.red5.server.messaging.IConsumer;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.stream.IProviderService;
import org.red5.server.stream.ISeekableProvider;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides interstitial source from FLV files from within a given
 * scope. Single-file usage plays one FLV. Multi-file (pod) usage plays a list
 * of FLVs back-to-back as one continuous {@link InterstitialSession}, with no
 * gap between files — file-to-file transitions happen internally without
 * re-prompting the engine, so the session's PTS / duration control covers the
 * entire pod.
 *
 * <p>All files in a pod must be encoded with compatible parameters (codec,
 * profile, sample rate, resolution). Mismatched parameters may glitch on
 * transition since the main stream's audio/video format is configured once at
 * session start.
 *
 * @author Andy
 * @author Vitor
 */
public class PlaylistInterstitial extends InterstitialSession implements IConsumer {

    private static Logger log = LoggerFactory.getLogger(PlaylistInterstitial.class);

    private IScope appScope;

    private boolean firstTimestamp;

    /**
     * Per-stream anchor offsets added to a tag's intrinsic (file-relative) timestamp when stamping
     * for the dispatched timeline. Tracking V and A separately preserves the per-file V/A offset
     * across pod boundaries: the closing file's last V and last A typically end at different
     * intrinsic timestamps, so anchoring both to a single value at the boundary collapses that
     * offset and leaves an audio gap of (V_last - A_last) per advance. Across N pod files that
     * cumulative gap surfaces downstream as PTS holes / nonmonotonic-DTS reports.
     */
    private long timeStartVideo;

    private long timeStartAudio;

    private boolean hasPackets;

    /** Tag pulled from io but whose timestamp is past the current dispatchTo cutoff.
     *  Held for the next process() call so we don't overshoot the live clock. */
    private IRTMPEvent pending;

    /** Ordered list of FLV file names making up the pod. Always non-empty; single-file usage stores a list of size 1. */
    private final List<String> fileNames;

    /** Index into {@link #fileNames} of the file currently being read. Advances on EOF until end-of-pod. */
    private int currentFileIndex;

    /**
     * Drift diagnostics: most recent intrinsic (pre-anchor) body timestamps of dispatched
     * VideoData / AudioData tags from the file currently being read. Reset to {@code -1} when
     * {@link #advanceToNextPodFile} opens a new file. Drives the per-stream re-anchor at file
     * boundaries: the new file's {@link #timeStartVideo} / {@link #timeStartAudio} are derived
     * from these so each stream picks up exactly 1 ms after IT left off. {@code -1} means "no
     * tag of that type dispatched yet from this file."
     */
    private long lastVideoBodyTs = -1L;

    private long lastAudioBodyTs = -1L;

    /**
     * Single-file constructor — equivalent to a pod of size 1. Behaves exactly as the original
     * {@code PlaylistInterstitial(IScope, String, boolean, boolean)} contract for backwards compatibility.
     */
    public PlaylistInterstitial(IScope appScope, String fileName, boolean isForwardAudio, boolean isForwardVideo) {
        this(appScope, Collections.singletonList(fileName), isForwardAudio, isForwardVideo);
    }

    /**
     * Multi-file (pod) constructor. The session plays each file in {@code fileNames} in order,
     * advancing internally when one exhausts so the engine sees a single continuous interstitial.
     * Useful for SCTE-35-driven ad pods where multiple creatives need to fill one break window
     * with no inter-ad gap.
     *
     * @param appScope        application scope (used to resolve VOD providers per file)
     * @param fileNames       ordered list of file names; must be non-empty
     * @param isForwardAudio  forward this session's audio packets to the output
     * @param isForwardVideo  forward this session's video packets to the output
     * @throws IllegalArgumentException if {@code fileNames} is null or empty
     */
    public PlaylistInterstitial(IScope appScope, List<String> fileNames, boolean isForwardAudio, boolean isForwardVideo) {
        super(isForwardAudio, isForwardVideo);
        if (fileNames == null || fileNames.isEmpty()) {
            throw new IllegalArgumentException("PlaylistInterstitial requires at least one fileName");
        }
        this.appScope = appScope;
        this.fileNames = List.copyOf(fileNames);
        this.currentFileIndex = 0;
        this.fileName = this.fileNames.get(0);
    }

    @Override
    public void queue() {
    }

    @Override
    public void open() {
        log.debug("Open {}", fileName);
        if (io != null) {
            io.unsubscribe(this);
            io = null;// affirm.
        }
        // in case we get abruptly reopened.
        hasPackets = false;
        pending = null;
        IContext context = appScope.getContext();
        IProviderService providerService = (IProviderService) context.getBean(IProviderService.BEAN_NAME);
        io = providerService.getVODProviderInput(appScope, fileName);
        if (io == null) {
            log.error("FLV not found");
        } else {
            log.debug("Found FLV");
            io.subscribe(this, new HashMap<String, Object>());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(long timestamp, IRTMPEvent event, IInterstitialStream output) throws IOException {
        if (io == null) {
            throw new IOException();
        }
        if (!firstTimestamp) {
            // log.debug("setting first ts {}", timestamp);
            firstTimestamp = true;
            timeStartVideo = timestamp;
            timeStartAudio = timestamp;
        }

        if (codec == 0 && (width == 0 || height == 0)) {
            return;
        }

        // Drain any tag held over from the previous call first.
        if (pending != null) {
            long pNow = pending.getTimestamp();
            long pTimeStart = timeStartFor(pending);
            long pDispatchTo = timestamp - pTimeStart;
            if (pNow > pDispatchTo) {
                // Still past cutoff — keep holding, dispatch the live event and return.
                dispatchEvent(event, true, output);
                return;
            }
            recordLastBodyTs(pending, pNow);
            long pStamped = pNow + pTimeStart;
            warnIfOvershootsLive(pStamped, timestamp, "pending");
            pending.setTimestamp((int) pStamped);
            dispatchEvent(pending, false, output);
            pending = null;
        }

        // Outer loop: when the current pod file exhausts, advance to the next file (if any) and
        // keep dispatching tags. Single-file usage exits the outer loop after one iteration since
        // there is no next file. We will dispatch IRTMPEvent 'event' after we dispatch all the
        // FLV tags with timestamps less than 'dispatchTo'; tags that overshoot are held in
        // {@link #pending} for the next call.
        IMessage message;
        while (true) {
            while ((message = io.pullMessage()) != null) {
                hasPackets = true;
                // log.debug("insert message {}",message);
                if (!(message instanceof RTMPMessage)) {
                    continue;
                }
                IRTMPEvent body = ((RTMPMessage) message).getBody();
                if (body == null) {
                    continue;
                }
                // Packet unused if:
                if (body instanceof Notify && !includeMetaData) {
                    continue;// we dont want insert's meta
                } else if (body instanceof AudioData) {
                    if (codec == 0) {
                        continue; // live stream has no Audio
                    }
                    switch (audioCompatibility) {
                        case YES:
                            break;

                        case UNKNOWN:
                            AudioInfo info = parseAudioParams((AudioData) body);
                            audioCompatibility = info.matchesStream;
                            if (audioCompatibility == AudioCompatibility.YES) {
                                break;
                            } else if (isForwardAudio() && audioCompatibility == AudioCompatibility.NO) {
                                log.error("FLV has incompatible audio. rate: {}  Channels: {}", info.audioSampleRate, info.audioChannels);
                                throw new IOException();
                            }

                        case NO://not forwarding audio or file audio properties are unknown.
                            continue;
                    }

                } else if (body instanceof VideoData && (width == 0 || height == 0)) {
                    continue;// live stream has no video.
                }

                long now = body.getTimestamp();
                long bTimeStart = timeStartFor(body);
                long bDispatchTo = timestamp - bTimeStart;
                if (now > bDispatchTo) {
                    // Hold this tag for the next process() call instead of overshooting.
                    // Invariant: pending stores the *raw* FLV timestamp (pre-anchor);
                    // do not call body.setTimestamp(...) before this assignment.
                    pending = body;
                    dispatchEvent(event, true, output);
                    // log.debug("segment done {} {} ", now,bDispatchTo);
                    return;
                }
                recordLastBodyTs(body, now);
                long stamped = now + bTimeStart;
                warnIfOvershootsLive(stamped, timestamp, "pull");
                body.setTimestamp((int) stamped);
                // log.debug("dispatchInterstitial {} {} {}", timestamp, now, stamped);

                // this may dispatch the tag or filter it out based on if we are forwarding audio/video or not.
                dispatchEvent(body, false, output);
            }

            // Current file exhausted (pullMessage returned null). If the pod has more files,
            // advance and keep dispatching against the new file. Otherwise fall through to the
            // original end-of-stream path (loop or dispose). The cutoff is now recomputed
            // per-tag using the post-advance timeStartVideo / timeStartAudio, so no top-level
            // refresh is needed here.
            if (currentFileIndex + 1 < fileNames.size() && advanceToNextPodFile(timestamp)) {
                continue;
            }
            break;
        }

        // We got here because dispatchTo limiter has not run out,
        // and duration has not expired,
        // and no more packets to pull (and no more pod files to advance to).
        // First action is to make sure we dispatch the incoming event tag.
        dispatchEvent(event, true, output);

        // Next action is to determine if our session has expired and if we are expected to loop.
        // If our duration has not expired, next call to process will loop. If looping reset parameters.
        if (!hasPackets) {
            //If the FLV was empty of tags, toss exception.
            throw new IOException("Empty insert");
        }
        if (sessionControl.canLoop()) {
            // Loops the LAST file of a pod (or the only file in single-file usage). Pod-wide
            // looping is not supported here; callers requiring it should rebuild the session.
            log.debug("loop file");
            OOBControlMessage oobCtrlMsg = new OOBControlMessage();
            oobCtrlMsg.setTarget(ISeekableProvider.KEY);
            oobCtrlMsg.setServiceName("seek");
            Map<String, Object> paramMap = new HashMap<String, Object>(1);
            paramMap.put("position", 0);
            oobCtrlMsg.setServiceParamMap(paramMap);
            io.sendOOBControlMessage(this, oobCtrlMsg);
            if (oobCtrlMsg.getResult() instanceof Integer) {
                log.debug("rewind {}", oobCtrlMsg.getResult());
            } else {
                open();
            }
            //Reset timestamp delta.
            firstTimestamp = false;
            pending = null; // any held tag is from the pre-loop run; discard
        } else {
            log.debug("interstitial complete  {}", timestamp);
            dispose();
            this.sessionControl.resumeProgram();

        }
    }

    /**
     * Closes the current file's input and opens the next file in the pod. Re-anchors the per-file
     * timing offset so the new file's intrinsic timestamps (which start at 0) align with the
     * current main-stream timestamp. Preserves {@link #hasPackets} across the transition (a
     * mid-pod empty file does not invalidate the session — only an entirely empty pod does), and
     * resets {@link InterstitialSession#audioCompatibility} so the new file's first audio config
     * tag re-evaluates compatibility.
     *
     * @param currentTimestamp the main-stream timestamp at which the advance is happening
     * @return {@code true} if the next file opened successfully; {@code false} on I/O failure
     */
    private boolean advanceToNextPodFile(long currentTimestamp) {
        // Capture closing-file diagnostics before any state mutates. {@code lastVideoBodyTs} and
        // {@code lastAudioBodyTs} are intrinsic (pre-anchor) timestamps of the last dispatched
        // VideoData / AudioData tags from the file we're about to close; they come straight from
        // {@link IRTMPEvent#getTimestamp()} as captured in {@link #process} before
        // {@code body.setTimestamp(...)} re-anchors them. {@code -1} means "no tag of that type
        // was dispatched from this file" (rare — empty audio or empty video stream).
        int closingIndex = currentFileIndex;
        String closingFile = fileName;
        long closingTimeStartVideo = timeStartVideo;
        long closingTimeStartAudio = timeStartAudio;
        long closingLastVideoBodyTs = lastVideoBodyTs;
        long closingLastAudioBodyTs = lastAudioBodyTs;

        currentFileIndex++;
        fileName = fileNames.get(currentFileIndex);
        if (io != null) {
            io.unsubscribe(this);
            io = null;
        }
        boolean preservedHasPackets = hasPackets;
        open();
        hasPackets = preservedHasPackets;
        // The next file may be encoded with different audio params even if the pod was assembled
        // from "compatible" creatives — re-check on the first audio config tag from the new file.
        audioCompatibility = AudioCompatibility.UNKNOWN;
        if (io == null) {
            log.warn("Failed to open next pod file: {} (index {}/{})", fileName, currentFileIndex + 1, fileNames.size());
            return false;
        }
        // Per-stream re-anchor: each stream picks up 1 ms after IT left off in the closing
        // file. Tracking V and A separately preserves the per-file V/A offset across the
        // boundary so the audio timeline doesn't gap by (V_last - A_last) on every advance.
        //
        // No min-cap against currentTimestamp: a tag is only dispatched when
        // raw_ts <= currentTimestamp - timeStartFor(body), so closingLastDispatchedTs is at
        // most currentTimestamp by construction. The new anchor is therefore at most
        // currentTimestamp + 1 — a one-tick overshoot that the pending-overshoot guard absorbs
        // on the next process() call. Capping at currentTimestamp instead would force the new
        // file's first tag to share a timestamp with the closing file's last tag, producing
        // duplicate timestamps that downstream packagers reject as nonmonotonic.
        long closingLastVideoDispatchedTs = closingLastVideoBodyTs >= 0 ? closingLastVideoBodyTs + closingTimeStartVideo : Long.MIN_VALUE;
        long closingLastAudioDispatchedTs = closingLastAudioBodyTs >= 0 ? closingLastAudioBodyTs + closingTimeStartAudio : Long.MIN_VALUE;
        if (closingLastVideoBodyTs >= 0) {
            timeStartVideo = closingLastVideoDispatchedTs + 1;
        } else {
            // No video dispatched from the closing file (audio-only or all filtered out).
            // Fall back to engine clock so the new file's video picks up at "now."
            timeStartVideo = currentTimestamp;
        }
        if (closingLastAudioBodyTs >= 0) {
            timeStartAudio = closingLastAudioDispatchedTs + 1;
        } else {
            timeStartAudio = currentTimestamp;
        }
        // Per-file tracking starts fresh for the new file.
        lastVideoBodyTs = -1L;
        lastAudioBodyTs = -1L;

        // Drift trace: file boundary report. The two key signals are
        // (a) {@code avSkewMs} — how far apart the closing file's last video and audio tags ended
        //     in the file's intrinsic clock. With per-stream anchors this offset is now PRESERVED
        //     across the boundary (the new file inherits the same V/A skew), so cumulative drift
        //     does not accumulate on the dispatched timeline.
        // (b) {@code wallClockGapMs} — the gap between (closing file's last dispatched ts on
        //     the engine clock) and (currentTimestamp where the new file will start). When the
        //     engine kept up with the file in real time these match; when not, this exposes how
        //     the dispatched A/V timeline lagged or sprinted.
        // Enable the FLVInterstitial logger at DEBUG to see these:
        //   <logger name="com.red5pro.interstitial.api.FLVInterstitial" level="DEBUG"/>
        long avSkewMs = (closingLastVideoBodyTs >= 0 && closingLastAudioBodyTs >= 0) ? (closingLastAudioBodyTs - closingLastVideoBodyTs) : Long.MIN_VALUE;
        long lastVideoDispatchedTs = closingLastVideoBodyTs >= 0 ? closingLastVideoDispatchedTs : -1L;
        long lastAudioDispatchedTs = closingLastAudioBodyTs >= 0 ? closingLastAudioDispatchedTs : -1L;
        long maxLastDispatchedTs = Math.max(lastVideoDispatchedTs, lastAudioDispatchedTs);
        long wallClockGapMs = maxLastDispatchedTs >= 0 ? (currentTimestamp - maxLastDispatchedTs) : Long.MIN_VALUE;
        log.debug("Pod boundary closingIdx={} closingFile={} oldTimeStartVideo={} oldTimeStartAudio={} currentTimestamp={} newTimeStartVideo={} newTimeStartAudio={} closingLastVideoBodyTs={} closingLastAudioBodyTs={} lastVideoDispatchedTs={} lastAudioDispatchedTs={} avSkewMs={} wallClockGapMs={} -> opening file {}/{}: {}", closingIndex, closingFile, closingTimeStartVideo, closingTimeStartAudio, currentTimestamp,
                timeStartVideo, timeStartAudio, closingLastVideoBodyTs, closingLastAudioBodyTs, lastVideoDispatchedTs, lastAudioDispatchedTs, avSkewMs == Long.MIN_VALUE ? "n/a" : avSkewMs, wallClockGapMs == Long.MIN_VALUE ? "n/a" : wallClockGapMs, currentFileIndex + 1, fileNames.size(), fileName);
        return true;
    }

    /**
     * Pick the per-stream anchor to use when stamping {@code body} for the dispatched timeline.
     * Audio uses {@link #timeStartAudio}; everything else (Video, Notify) uses {@link #timeStartVideo}
     * so non-A/V bodies track the dominant clock alongside their adjacent video tags.
     */
    private long timeStartFor(IRTMPEvent body) {
        return (body instanceof AudioData) ? timeStartAudio : timeStartVideo;
    }

    /**
     * Drift diagnostics helper: records the most recent intrinsic body timestamp by type so the
     * pod-boundary log in {@link #advanceToNextPodFile} can report A/V skew at file end. {@code rawTs}
     * is the pre-anchor value pulled from the FLV reader (i.e. file-intrinsic).
     * No-op for non-A/V events (Notify/metadata) since those don't contribute to A/V alignment.
     */
    private void recordLastBodyTs(IRTMPEvent body, long rawTs) {
        if (body instanceof VideoData) {
            lastVideoBodyTs = rawTs;
        } else if (body instanceof AudioData) {
            lastAudioBodyTs = rawTs;
        }
    }

    /**
     * Drift-violation detector: logs WARN if a re-stamped FLV body's dispatched timestamp ever
     * exceeds the current live-clock timestamp passed into {@link #process}. The pending /
     * pull-cutoff guard should make this impossible — if it fires, either the cutoff math drifted
     * or a per-stream anchor was set ahead of the live clock at a pod boundary. Use this signal
     * to pinpoint downstream nonmonotonic-DTS reports back to the FLV interstitial.
     *
     * @param stampedTs    the dispatched timestamp the body is about to carry (raw + anchor)
     * @param liveTs       the engine's live-clock timestamp for this {@code process()} call
     * @param origin       short tag identifying the dispatch site ("pending" / "pull") for the log
     */
    private void warnIfOvershootsLive(long stampedTs, long liveTs, String origin) {
        if (stampedTs > liveTs) {
            log.warn("FLV interstitial overshoot ({}): stampedTs={} liveTs={} delta={}ms file={} timeStartVideo={} timeStartAudio={}", origin, stampedTs, liveTs, stampedTs - liveTs, fileName, timeStartVideo, timeStartAudio);
        }
    }

    @Override
    public void dispose() {
        log.debug("dispose");
        if (io != null) {
            io.unsubscribe(this);
            io = null;
        }
        hasPackets = false;
        fileName = null;
        pending = null;
        fireDisposedCallbackOnce();
    }

    @Override
    public void onOOBControlMessage(IMessageComponent source, IPipe pipe, OOBControlMessage oobCtrlMsg) {
    }
}
