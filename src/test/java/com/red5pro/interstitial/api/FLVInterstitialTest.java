package com.red5pro.interstitial.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.junit.Test;
import org.red5.codec.IStreamCodecInfo;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.messaging.IMessage;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.stream.message.RTMPMessage;

import com.red5pro.override.api.ProStreamTerminationEventListener;

/**
 * Regression tests for FLVInterstitial timestamp drift.
 *
 * The bug: process() calls dispatchEvent(body, false, output) BEFORE checking
 * whether (now > dispatchTo), so the tag that triggers the exit condition is
 * dispatched with timestamp = now + timeStart, where now > dispatchTo.
 * That means the dispatched timestamp exceeds the live clock by one inter-frame
 * interval on every process() call that finds a boundary frame.
 *
 * These tests are written to FAIL against the current (buggy) implementation
 * and will pass once the fix is applied in Task 4.
 */
public class FLVInterstitialTest {

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Build a list of video keyframe RTMPMessages with timestamps
     * startMs, startMs+stepMs, startMs+2*stepMs, ...
     */
    private static List<IMessage> buildFlvFrames(int startMs, int stepMs, int count) {
        List<IMessage> out = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            // 0x17 = video keyframe, AVC; remaining bytes are minimal AVC NALU header
            VideoData vd = new VideoData(IoBuffer.wrap(new byte[] { 0x17, 1, 0, 0, 0 }));
            vd.setTimestamp(startMs + i * stepMs);
            out.add(RTMPMessage.build(vd));
        }
        return out;
    }

    /** Build a single live-tick IRTMPEvent at the given timestamp. */
    private static IRTMPEvent liveTick(int ts) {
        VideoData vd = new VideoData(IoBuffer.wrap(new byte[] { 0x17, 1, 0, 0, 0 }));
        vd.setTimestamp(ts);
        return vd;
    }

    /**
     * Build a ready-to-use FLVInterstitial with a FakeMessageInput pre-loaded with
     * the supplied frames. The session is wired with STREAM_CLOCK / duration /
     * start. Video params are set to 640x480 so frames are not filtered out.
     * No audio (codec=0 keeps AudioData filtering consistent).
     */
    private static FLVInterstitial buildSession(List<IMessage> frames, long start, long duration) {
        FLVInterstitial fi = new FLVInterstitial(null, "test.flv", false, true);
        fi.io = new FakeMessageInput(frames);
        fi.setVideoParams(640, 480);
        // codec=0 means no audio — only VideoData will be forwarded
        InterstitialDurationControl ctrl = new InterstitialDurationControl(InterstitialDurationControlType.STREAM_CLOCK, duration, start);
        fi.sessionControl = ctrl;
        return fi;
    }

    // ---------------------------------------------------------------------------
    // CapturingStream — no-op IInterstitialStream that records dispatched events
    // ---------------------------------------------------------------------------

    private static class CapturingStream implements IInterstitialStream {

        final List<IRTMPEvent> dispatched = new ArrayList<>();

        @Override
        public void dispatchInterstitial(IEvent event) {
            if (event instanceof IRTMPEvent) {
                dispatched.add((IRTMPEvent) event);
            }
        }

        @Override
        public void addTerminationEventListener(ProStreamTerminationEventListener terminationListener) {
        }

        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public IStreamCodecInfo getCodecInfo() {
            return null;
        }

        @Override
        public void addStreamListener(IStreamListener streamListener) {
        }

        @Override
        public void removeStreamListener(IStreamListener streamListener) {
        }

        @Override
        public IInterstitialEngine getInterstitialEngine() {
            return null;
        }

        @Override
        public String getBroadcastStreamPublishName() {
            return "test";
        }

        /**
         * Return the max dispatched interstitial (non-live) VideoData timestamp, or -1.
         * Live events are passed with isOriginal=true which, combined with
         * isForwardVideo=true, means they are NOT dispatched — so all VideoData in
         * this list are FLV tags.
         */
        int maxInterstitialTs() {
            int max = -1;
            for (IRTMPEvent e : dispatched) {
                if (e instanceof VideoData) {
                    max = Math.max(max, e.getTimestamp());
                }
            }
            return max;
        }
    }

    // ---------------------------------------------------------------------------
    // Test 1: single session — dispatched timestamps must not overshoot the live clock
    // ---------------------------------------------------------------------------

    /**
     * Drive 30 live ticks at 33 ms intervals (simulating 30 fps). The FLV
     * contains 150 video frames (5x over-provisioned) so the FLV never runs dry.
     * Session start = LIVE_START = 1000, so the first live tick that activates
     * the session is ts=1000. After all ticks, the maximum dispatched FLV timestamp
     * must be <= the final live tick timestamp.
     *
     * Pre-fix behaviour: process() dispatches each tag before checking
     * (now > dispatchTo), so the boundary tag (the one that triggers the return)
     * has now > dispatchTo. Its dispatched timestamp = now + timeStart > ts.
     * The test will report: "Dispatched FLV timestamp X overshoots live clock Y by 33 ms".
     */
    @Test
    public void singleSession_dispatchedTimestampsDoNotOvershootLiveClock() throws IOException {
        final int STEP = 33;
        final int TICKS = 30;
        final int LIVE_START = 1000;

        // 150 frames: more than enough to cover 30 ticks without running dry
        List<IMessage> frames = buildFlvFrames(0, STEP, TICKS * 5);

        // Session start = LIVE_START, duration = 5000 ms — comfortably covers all ticks
        FLVInterstitial fi = buildSession(frames, LIVE_START, 5000);
        CapturingStream output = new CapturingStream();

        int liveMax = LIVE_START;
        for (int i = 0; i < TICKS; i++) {
            int ts = LIVE_START + i * STEP;
            liveMax = ts;
            fi.process(ts, liveTick(ts), output);
        }

        int dispatchedMax = output.maxInterstitialTs();

        // Post-fix contract: no dispatched FLV tag should have a timestamp
        // exceeding the last live tick.
        assertTrue("Dispatched FLV timestamp " + dispatchedMax + " overshoots live clock " + liveMax + " by " + (dispatchedMax - liveMax) + " ms", dispatchedMax <= liveMax);
    }

    // ---------------------------------------------------------------------------
    // Test 2: 30 consecutive sessions — cumulative overshoot must be zero
    // ---------------------------------------------------------------------------

    /**
     * Simulate 30 back-to-back 1-second sessions (30 ticks @ 33 ms each).
     * Each session is a fresh FLVInterstitial with 60 video frames (2x, so the
     * FLV never runs dry during the session). After each session, liveTs advances
     * by one additional step to simulate a gap.
     *
     * Pre-fix: each process() call that exits via (now > dispatchTo) dispatches
     * a boundary tag that overshoots the live clock by 33 ms. That's 30 calls
     * per session × 30 sessions = 900 overshooting events, each adding 33 ms
     * of overshoot — total ~29700 ms, or at minimum 30 * 33 = 990 ms total.
     * Post-fix: total overshoot == 0.
     *
     * The FLV is 2x over-provisioned so at least the first 29 ticks per session
     * always find a boundary frame and produce an overshoot.
     */
    @Test
    public void multiSession_noCumulativeDrift() throws IOException {
        final int STEP = 33;
        final int TICKS_PER_SESSION = 30;
        final int SESSIONS = 30;
        // 2x over-provision so the FLV never runs out during a session
        final int FRAMES_PER_SESSION = TICKS_PER_SESSION * 2;

        long liveTs = 0;
        long totalOvershoot = 0;

        for (int s = 0; s < SESSIONS; s++) {
            List<IMessage> frames = buildFlvFrames(0, STEP, FRAMES_PER_SESSION);
            // Session starts at current live timestamp
            FLVInterstitial fi = buildSession(frames, liveTs, 2000);
            CapturingStream output = new CapturingStream();

            long sessionLiveStart = liveTs;
            long liveMax = liveTs;
            for (int i = 0; i < TICKS_PER_SESSION; i++) {
                long ts = sessionLiveStart + (long) i * STEP;
                liveMax = ts;
                fi.process(ts, liveTick((int) ts), output);
            }

            long dispatchedMax = output.maxInterstitialTs();
            long overshoot = Math.max(0L, dispatchedMax - liveMax);
            totalOvershoot += overshoot;

            fi.dispose();

            // Advance liveTs past the end of this session (add one step as gap)
            liveTs = liveMax + STEP;
        }

        assertEquals("Cumulative timestamp overshoot across " + SESSIONS + " sessions should be 0 ms but was " + totalOvershoot + " ms", 0L, totalOvershoot);
    }

    // ---------------------------------------------------------------------------
    // Test 3: dispose clears any pending tag; re-seeded session does not
    //         dispatch a tag with timestamp beyond the live clock at ts=0
    // ---------------------------------------------------------------------------

    /**
     * Verifies that after dispose() and re-seeding io with fresh frames, the
     * session does NOT dispatch a tag whose timestamp exceeds the live clock.
     *
     * The post-fix design will hold the boundary tag in a "pending" slot rather
     * than dispatching it immediately. After dispose() that pending slot is
     * cleared. On re-seed, only tags within the new dispatchTo window are sent.
     *
     * Pre-fix: the boundary tag is dispatched immediately, so even after dispose()
     * and re-seeding, the very first process() call dispatches frame[0] (ts=0)
     * AND frame[1] (ts=33) because frame[1] is the first frame with now > 0.
     * The dispatched max is 33 ms, which overshoots the live clock (ts=0) by 33 ms.
     *
     * The assertion checks that after a fresh start (dispose + re-seed), a single
     * process() call at ts=0 does not produce any dispatched tag with ts > 0.
     */
    @Test
    public void disposeClearsPendingTag() throws IOException {
        final int STEP = 33;

        // Phase 1: run one tick to prime the session (drives the overshoot bug)
        List<IMessage> frames1 = buildFlvFrames(0, STEP, 20);
        FLVInterstitial fi = buildSession(frames1, 0, 5000);
        CapturingStream output1 = new CapturingStream();
        fi.process(0, liveTick(0), output1);

        // Phase 2: dispose, then re-seed with a fresh FLV starting at ts=0
        fi.dispose();

        List<IMessage> frames2 = buildFlvFrames(0, STEP, 20);
        fi.io = new FakeMessageInput(frames2);
        fi.setVideoParams(640, 480);
        // Fresh sessionControl so firstTimestamp resets properly
        InterstitialDurationControl ctrl2 = new InterstitialDurationControl(InterstitialDurationControlType.STREAM_CLOCK, 5000, 0);
        fi.sessionControl = ctrl2;

        CapturingStream output2 = new CapturingStream();
        // Single process call at ts=0.
        // Post-fix contract: the boundary frame (ts=33) must NOT be dispatched
        // because 33 > dispatchTo(0). Only frame[0] at ts=0 is within budget.
        // Pre-fix: frame[0] ts=0 is dispatched, then frame[1] ts=33 is dispatched
        // (as the overshoot), so dispatchedMax = 33 > liveTs(0).
        fi.process(0, liveTick(0), output2);

        int dispatchedMax = output2.maxInterstitialTs();
        int liveTsAtCall = 0;

        // Post-fix: only tags with timestamp <= live clock (0) should be dispatched
        assertTrue("After dispose() and re-seed, dispatched tag timestamp " + dispatchedMax + " overshoots live clock " + liveTsAtCall + " by " + (dispatchedMax - liveTsAtCall) + " ms" + " — boundary tag from previous run leaked or overshoot bug present", dispatchedMax <= liveTsAtCall);
    }

    // ---------------------------------------------------------------------------
    // Test 4: last dispatched FLV timestamp must be <= last live timestamp
    // ---------------------------------------------------------------------------

    /**
     * Verifies the post-fix contract: the maximum timestamp in dispatched FLV
     * events must never exceed the maximum live clock timestamp.
     *
     * This test pins the precise contract that prevents downstream backward-step
     * padding and complements the existing overshoot tests. The boundary is reached
     * naturally as live events advance, but the dispatched FLV must stay synchronized
     * or lag, never lead ahead.
     */
    @Test
    public void lastDispatchedFlvTimestamp_neverExceedsLastLiveTimestamp() throws IOException {
        final int STEP = 33;
        final int FIRST_LIVE = 500;
        final int TICKS = 60;

        // Over-provision FLV so the boundary is always reached
        List<IMessage> frames = buildFlvFrames(0, STEP, TICKS * 3);
        FLVInterstitial fi = buildSession(frames, FIRST_LIVE, 5000);
        CapturingStream output = new CapturingStream();

        int lastLiveTs = FIRST_LIVE;
        for (int i = 0; i < TICKS; i++) {
            int ts = FIRST_LIVE + i * STEP;
            lastLiveTs = ts;
            fi.process(ts, liveTick(ts), output);
        }

        int dispatchedMax = output.maxInterstitialTs();
        assertTrue("last FLV ts " + dispatchedMax + " exceeds last live ts " + lastLiveTs, dispatchedMax <= lastLiveTs);
    }

    // ---------------------------------------------------------------------------
    // Test 5: pod file boundary preserves per-stream A/V offset
    // ---------------------------------------------------------------------------

    /**
     * Multi-file pod where the closing file ends with V_last_intrinsic > A_last_intrinsic. With a
     * single shared {@code timeStart} re-anchor, the new file's first audio tag (raw 0) was
     * stamped to {@code closingMaxDispatchedTs + 1}, which equals {@code V_last_dispatched + 1}
     * — leaving an audio gap of {@code (V_last - A_last) + 1} ms on the dispatched timeline at
     * every pod boundary. With per-stream anchors that gap collapses to a single 1 ms inter-file
     * separator and the per-file V/A skew is preserved across the boundary.
     *
     * <p>This test wires a 2-file pod where file 1 ends at intrinsic V=5000, A=4977 (V leads by
     * 23 ms). After exhausting file 1 it asserts that the first dispatched audio tag from file 2
     * lands within {@code AAC_FRAME_MS} of the closing file's last dispatched audio tag — i.e. it
     * picks up where file-1 audio left off, not 23 ms later.
     */
    @Test
    public void podBoundary_preservesPerStreamAvOffset() throws IOException {
        final int VIDEO_STEP = 33; // ~30 fps
        final int AUDIO_STEP = 23; // ~AAC frame at 44.1 kHz, rounded to int ms
        // Closing-file dispatch ends at the largest step-multiple <= the cap. With
        // VIDEO_STEP=33, AUDIO_STEP=23, and caps 5000/4977, the last frames land at:
        //   V last raw = 33 * 151 = 4983
        //   A last raw = 23 * 216 = 4968   (V leads A by 15 ms in file 1)
        final int FILE1_VIDEO_CAP = 5000;
        final int FILE1_AUDIO_CAP = 4977;
        final int FILE1_VIDEO_LAST = (FILE1_VIDEO_CAP / VIDEO_STEP) * VIDEO_STEP;
        final int FILE1_AUDIO_LAST = (FILE1_AUDIO_CAP / AUDIO_STEP) * AUDIO_STEP;
        // File 2 over-provisioned so the live-tick loop does not exhaust it (which would dispose
        // the session and make the next process() call throw on io == null).
        final int FILE2_VIDEO_CAP = 30_000;
        final int FILE2_AUDIO_CAP = 30_000;
        final int LIVE_START = 1000;
        final int LIVE_TICK_STEP = VIDEO_STEP;
        // Enough ticks to comfortably cross the file 1 → file 2 boundary and get a few file-2
        // audio tags dispatched, but not so many that file 2 runs dry.
        final int LIVE_TICK_COUNT = 250;

        final List<IMessage> file1 = buildAvFrames(0, VIDEO_STEP, FILE1_VIDEO_CAP, AUDIO_STEP, FILE1_AUDIO_CAP);
        final List<IMessage> file2 = buildAvFrames(0, VIDEO_STEP, FILE2_VIDEO_CAP, AUDIO_STEP, FILE2_AUDIO_CAP);

        Map<String, List<IMessage>> sources = new HashMap<>();
        sources.put("a.flv", file1);
        sources.put("b.flv", file2);

        FLVInterstitial fi = buildPodSession(List.of("a.flv", "b.flv"), sources, LIVE_START, 60_000);
        CapturingStream output = new CapturingStream();

        for (int i = 0; i < LIVE_TICK_COUNT; i++) {
            int ts = LIVE_START + i * LIVE_TICK_STEP;
            fi.process(ts, liveTick(ts), output);
        }

        // Identify the boundary on the dispatched audio timeline. Anything <= file 1's
        // last-audio dispatch came from file 1; the next A in dispatch order came from file 2.
        long closingFileLastAudioDispatched = -1;
        long openingFileFirstAudioDispatched = -1;
        long fileOneAudioBoundary = (long) FILE1_AUDIO_LAST + LIVE_START;
        for (IRTMPEvent e : output.dispatched) {
            if (!(e instanceof AudioData)) {
                continue;
            }
            int ts = e.getTimestamp();
            if (ts <= fileOneAudioBoundary) {
                if (ts > closingFileLastAudioDispatched) {
                    closingFileLastAudioDispatched = ts;
                }
            } else if (openingFileFirstAudioDispatched < 0 || ts < openingFileFirstAudioDispatched) {
                openingFileFirstAudioDispatched = ts;
            }
        }

        assertTrue("expected at least one audio tag dispatched from file 1; saw none", closingFileLastAudioDispatched >= 0);
        assertTrue("expected at least one audio tag dispatched from file 2; saw none", openingFileFirstAudioDispatched >= 0);

        long audioBoundaryGap = openingFileFirstAudioDispatched - closingFileLastAudioDispatched;

        // Per-stream re-anchor: file 2's first A is stamped at A_last_dispatched + 1, so the gap
        // is exactly 1 ms regardless of the closing file's V/A skew. Pre-fix (single shared
        // timeStart anchored to V) this gap would be (V_last - A_last) + 1 = 16 ms.
        assertEquals("audio timeline at pod boundary gapped by " + audioBoundaryGap + " ms (expected 1 ms) — per-stream A anchor not honored", 1L, audioBoundaryGap);

        // Sanity: closing audio is exactly where we put it — anchored at LIVE_START on first call.
        assertEquals("closing-file last A dispatched at unexpected ts", fileOneAudioBoundary, closingFileLastAudioDispatched);
    }

    // ---------------------------------------------------------------------------
    // Helpers for the multi-file boundary test
    // ---------------------------------------------------------------------------

    /**
     * Build interleaved video + audio FLV messages. Video keyframes step by {@code videoStep}
     * starting at 0 and ending at {@code videoEnd}; audio frames step by {@code audioStep} ending
     * at {@code audioEnd}. The first audio frame is an AAC sequence header with stereo / 44.1 kHz
     * config so the session's {@link InterstitialSession#parseAudioParams parseAudioParams}
     * accepts the stream as compatible.
     */
    private static List<IMessage> buildAvFrames(int startMs, int videoStep, int videoEnd, int audioStep, int audioEnd) {
        List<IMessage> out = new ArrayList<>();
        // AAC sequence header: codec=10 (AF nibble), packetType=0 (config), AudioSpecificConfig
        // for AAC-LC stereo @ 44.1 kHz: 0x12 0x10. parseAudioParams matches against
        // (channels=2, rate=44100) configured via setAudioParams below.
        AudioData cfg = new AudioData(IoBuffer.wrap(new byte[] { (byte) 0xAF, 0x00, 0x12, 0x10 }));
        cfg.setTimestamp(startMs);
        out.add(RTMPMessage.build(cfg));
        // Subsequent AAC raw frames. Body bytes don't matter — only header (0xAF 0x01) is parsed.
        // Audio frames at audioStep, 2*audioStep, ... up to and including audioEnd.
        for (int t = audioStep; t <= audioEnd; t += audioStep) {
            AudioData ad = new AudioData(IoBuffer.wrap(new byte[] { (byte) 0xAF, 0x01, 0x42 }));
            ad.setTimestamp(startMs + t);
            out.add(RTMPMessage.build(ad));
        }
        // Video keyframes at 0, videoStep, ... up to and including videoEnd.
        for (int t = 0; t <= videoEnd; t += videoStep) {
            VideoData vd = new VideoData(IoBuffer.wrap(new byte[] { 0x17, 1, 0, 0, 0 }));
            vd.setTimestamp(startMs + t);
            out.add(RTMPMessage.build(vd));
        }
        return out;
    }

    /**
     * Build a multi-file pod session. The session is wired with audio params (AAC stereo / 44.1 kHz)
     * matching {@link #buildAvFrames}, both A and V forwarded. {@code open()} is overridden to
     * resolve {@link #fileName} against {@code sources} instead of using the live VOD provider, so
     * advance-to-next-pod-file works in unit tests without an {@code IScope}.
     */
    private static FLVInterstitial buildPodSession(List<String> fileNames, Map<String, List<IMessage>> sources, long start, long duration) {
        FLVInterstitial fi = new FLVInterstitial(null, fileNames, true, true) {
            @Override
            public void open() {
                if (this.io != null) {
                    this.io.unsubscribe(this);
                    this.io = null;
                }
                List<IMessage> frames = sources.get(this.fileName);
                if (frames != null) {
                    this.io = new FakeMessageInput(frames);
                }
            }
        };
        fi.io = new FakeMessageInput(sources.get(fileNames.get(0)));
        fi.setVideoParams(640, 480);
        fi.setAudioParams(InterstitialSession.AAC_AUDIO, 2, 44100);
        InterstitialDurationControl ctrl = new InterstitialDurationControl(InterstitialDurationControlType.STREAM_CLOCK, duration, start);
        fi.sessionControl = ctrl;
        return fi;
    }
}
