package com.red5pro.interstitial.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.junit.Before;
import org.junit.Test;
import org.red5.codec.IAudioStreamCodec;
import org.red5.codec.IStreamCodecInfo;
import org.red5.codec.IVideoStreamCodec;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.VideoData;

public class LiveInterstitialTest {

    private FakeInterstitialStream liveStream;

    private FakeInterstitialStream newStream;

    private LiveInterstitial subject;

    @Before
    public void setUp() {
        liveStream = new FakeInterstitialStream("live");
        newStream = new FakeInterstitialStream("inserted");
        // Provide non-null codec info so that isVideoPrimed block does not NPE.
        // getVideoCodec() returns null, so the decoder-config branch is skipped.
        newStream.setCodecInfo(new MinimalCodecInfo());
        subject = new LiveInterstitial(liveStream, newStream, false, true);
        subject.audioCompatibility = InterstitialSession.AudioCompatibility.YES;
    }

    private static VideoData videoPacket(int ts) {
        VideoData vd = new VideoData(IoBuffer.wrap(new byte[] { 0x17, 1, 0, 0, 0 }));
        vd.setTimestamp(ts);
        return vd;
    }

    private static IRTMPEvent liveTick(int ts) {
        VideoData vd = new VideoData(IoBuffer.wrap(new byte[] { 0x17, 1, 0, 0, 0 }));
        vd.setTimestamp(ts);
        return vd;
    }

    @Test
    public void processFirst_thenPacket_anchorsToLiveTs() throws IOException {
        subject.process(2000L, liveTick(2000), liveStream);
        subject.packetReceived((IBroadcastStream) null, videoPacket(500));
        assertEquals(1, liveStream.dispatched.size());
        assertEquals(2000, liveStream.dispatched.get(0).getTimestamp());
    }

    @Test
    public void packetFirst_thenProcess_drainsBufferAtAnchor() throws IOException {
        subject.packetReceived((IBroadcastStream) null, videoPacket(500));
        subject.packetReceived((IBroadcastStream) null, videoPacket(533));
        subject.packetReceived((IBroadcastStream) null, videoPacket(566));
        assertEquals("packets must not dispatch before live anchor", 0, liveStream.dispatched.size());

        subject.process(2000L, liveTick(2000), liveStream);
        assertEquals(3, liveStream.dispatched.size());
        assertEquals(2000, liveStream.dispatched.get(0).getTimestamp());
        assertEquals(2033, liveStream.dispatched.get(1).getTimestamp());
        assertEquals(2066, liveStream.dispatched.get(2).getTimestamp());
    }

    @Test
    public void subsequentPackets_useEstablishedDelta() throws IOException {
        subject.process(2000L, liveTick(2000), liveStream);
        subject.packetReceived((IBroadcastStream) null, videoPacket(500));
        subject.packetReceived((IBroadcastStream) null, videoPacket(600));
        subject.packetReceived((IBroadcastStream) null, videoPacket(633));

        assertEquals(3, liveStream.dispatched.size());
        assertEquals(2000, liveStream.dispatched.get(0).getTimestamp());
        assertEquals(2100, liveStream.dispatched.get(1).getTimestamp());
        assertEquals(2133, liveStream.dispatched.get(2).getTimestamp());
    }

    @Test
    public void creationTime_doesNotInfluenceTimestamps() throws IOException {
        liveStream.setCreationTime(0L);
        newStream.setCreationTime(1_000_000L);
        subject = new LiveInterstitial(liveStream, newStream, false, true);
        subject.audioCompatibility = InterstitialSession.AudioCompatibility.YES;

        subject.process(2000L, liveTick(2000), liveStream);
        subject.packetReceived((IBroadcastStream) null, videoPacket(500));

        assertEquals(1, liveStream.dispatched.size());
        assertEquals("dispatched ts depends only on live anchor and packet ts, not creationTime", 2000, liveStream.dispatched.get(0).getTimestamp());
    }

    @Test
    public void disposeClearsAnchorState() throws IOException {
        subject.process(2000L, liveTick(2000), liveStream);
        subject.packetReceived((IBroadcastStream) null, videoPacket(500));
        subject.dispose();

        liveStream.dispatched.clear();
        subject.process(5000L, liveTick(5000), liveStream);
        subject.packetReceived((IBroadcastStream) null, videoPacket(800));
        assertEquals(1, liveStream.dispatched.size());
        assertEquals(5000, liveStream.dispatched.get(0).getTimestamp());
    }

    @Test
    public void isVideoPrimedUsesPostAnchorDelta() throws IOException {
        liveStream.setCreationTime(0L);
        newStream.setCreationTime(999_999L);
        subject = new LiveInterstitial(liveStream, newStream, false, true);
        subject.audioCompatibility = InterstitialSession.AudioCompatibility.YES;
        newStream.setCodecInfo(new MinimalCodecInfo());

        subject.process(2000L, liveTick(2000), liveStream);
        subject.packetReceived((IBroadcastStream) null, videoPacket(500));

        for (IRTMPEvent e : liveStream.dispatched) {
            assertTrue("dispatched ts " + e.getTimestamp() + " smells like creationTime arithmetic", e.getTimestamp() < 100_000);
        }
    }

    /** Minimal IStreamCodecInfo whose getVideoCodec() / getAudioCodec() return null. */
    private static class MinimalCodecInfo implements IStreamCodecInfo {
        @Override
        public boolean hasVideo() {
            return false;
        }

        @Override
        public boolean hasAudio() {
            return false;
        }

        @Override
        public String getVideoCodecName() {
            return null;
        }

        @Override
        public String getAudioCodecName() {
            return null;
        }

        @Override
        public IVideoStreamCodec getVideoCodec() {
            return null;
        }

        @Override
        public IAudioStreamCodec getAudioCodec() {
            return null;
        }
    }
}
