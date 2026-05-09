package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.HashMap;
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
 * This class provides interstitial source from a single FLV file within a given scope.
 *
 * @author Andy
 */
public class FLVInterstitial extends InterstitialSession implements IConsumer {

    private static Logger log = LoggerFactory.getLogger(FLVInterstitial.class);

    private IScope appScope;

    private boolean firstTimestamp;

    private long timeStartVideo;

    private long timeStartAudio;

    private boolean hasPackets;

    /** Tag pulled from io but whose timestamp is past the current dispatchTo cutoff.
     *  Held for the next process() call so we don't overshoot the live clock. */
    private IRTMPEvent pending;

    public FLVInterstitial(IScope appScope, String fileName, boolean isForwardAudio, boolean isForwardVideo) {
        super(isForwardAudio, isForwardVideo);
        this.appScope = appScope;
        this.fileName = fileName;
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
            long pStamped = pNow + pTimeStart;
            warnIfOvershootsLive(pStamped, timestamp, "pending");
            pending.setTimestamp((int) pStamped);
            dispatchEvent(pending, false, output);
            pending = null;
        }

        IMessage message;
        while ((message = io.pullMessage()) != null) {
            hasPackets = true;
            if (!(message instanceof RTMPMessage)) {
                continue;
            }
            IRTMPEvent body = ((RTMPMessage) message).getBody();
            if (body == null) {
                continue;
            }
            if (body instanceof Notify && !includeMetaData) {
                continue;
            } else if (body instanceof AudioData) {
                if (codec == 0) {
                    continue;
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
                    case NO:
                        continue;
                }
            } else if (body instanceof VideoData && (width == 0 || height == 0)) {
                continue;
            }

            long now = body.getTimestamp();
            long bTimeStart = timeStartFor(body);
            long bDispatchTo = timestamp - bTimeStart;
            if (now > bDispatchTo) {
                pending = body;
                dispatchEvent(event, true, output);
                return;
            }
            long stamped = now + bTimeStart;
            warnIfOvershootsLive(stamped, timestamp, "pull");
            body.setTimestamp((int) stamped);
            dispatchEvent(body, false, output);
        }

        // We got here because dispatchTo limiter has not run out,
        // and duration has not expired,
        // and no more packets to pull.
        // First action is to make sure we dispatch the incoming event tag.
        dispatchEvent(event, true, output);

        // Next action is to determine if our session has expired and if we are expected to loop.
        // If our duration has not expired, next call to process will loop. If looping reset parameters.
        if (!hasPackets) {
            //If the FLV was empty of tags, toss exception.
            throw new IOException("Empty insert");
        }
        if (sessionControl.canLoop()) {
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
     * Pick the per-stream anchor to use when stamping {@code body} for the dispatched timeline.
     * Audio uses {@link #timeStartAudio}; everything else (Video, Notify) uses {@link #timeStartVideo}
     * so non-A/V bodies track the dominant clock alongside their adjacent video tags.
     */
    private long timeStartFor(IRTMPEvent body) {
        return (body instanceof AudioData) ? timeStartAudio : timeStartVideo;
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
