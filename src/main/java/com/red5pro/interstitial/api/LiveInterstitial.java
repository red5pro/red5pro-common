package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.codec.VideoFrameType;
import org.red5.io.IoConstants;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.override.IProStream;
import com.red5pro.override.api.ProStreamTerminationEventListener;

/**
 * LiveInterstitial is an InterstitialSession for live streams. It can be used
 * to inject one stream into another.
 *
 * @author Nate Roe
 */
public class LiveInterstitial extends InterstitialSession implements IStreamListener, ProStreamTerminationEventListener {

    private static Logger log = LoggerFactory.getLogger(LiveInterstitial.class);

    private int timeDelta;

    /** True once timeDelta has been computed from real RTMP anchor samples. */
    private volatile boolean anchored;

    /** Live-stream RTMP timestamp captured at activation; -1 until set. */
    private long liveAnchorTs = -1L;

    /** Packets received from newStream before the live anchor was established. */
    private final Deque<IStreamPacket> preAnchorBuffer = new ArrayDeque<>();

    /** Maximum buffered pre-anchor packets before the session is failed. */
    private static final int MAX_PRE_ANCHOR_BUFFER = 64;

    /** Lock guarding anchor-setup state. Steady state is lock-free via volatile anchored. */
    private final Object anchorLock = new Object();

    private boolean hasKeyFrame;

    private boolean isVideoPrimed;

    private boolean isDeadStream;

    private boolean hasAudio;

    private IInterstitialStream liveStream;

    private IInterstitialStream newStream;

    /**
     * @param liveStream
     *            the live stream whose contents will change
     * @param newStream
     *            the new stream to inject into liveStream
     * @param isInterstitialVideo
     *            send video?
     * @param isInterstitialAudio
     *            send audio?
     */
    public LiveInterstitial(IInterstitialStream liveStream, IInterstitialStream newStream, boolean isInterstitialAudio, boolean isInterstitialVideo) {
        super(isInterstitialAudio, isInterstitialVideo);
        log.trace("LiveInterstitial ctor. target: {}, int'tial: {},  isForwardVideo: {}, isForwardAudio: {}", liveStream, newStream, isInterstitialVideo, isInterstitialAudio);
        this.liveStream = liveStream;
        this.newStream = newStream;
        newStream.addTerminationEventListener(this);
        liveStream.addTerminationEventListener(this);
        this.fileName = newStream.getBroadcastStreamPublishName();
        // timeDelta is computed lazily at activation from RTMP anchor samples,
        // not from wall-clock creation times — see process() and packetReceived() below.
    }

    @Override
    public void packetReceived(IBroadcastStream stream, IStreamPacket packet) {
        if (isDeadStream) {
            return;
        }

        if (!anchored) {
            synchronized (anchorLock) {
                if (!anchored) {
                    if (liveAnchorTs == -1L) {
                        if (preAnchorBuffer.size() >= MAX_PRE_ANCHOR_BUFFER) {
                            log.warn("LiveInterstitial pre-anchor buffer full ({} packets); failing session", MAX_PRE_ANCHOR_BUFFER);
                            isDeadStream = true;
                            preAnchorBuffer.clear();
                            return;
                        }
                        preAnchorBuffer.add(packet);
                        return;
                    }
                    long newAnchorTs = packet.getTimestamp();
                    timeDelta = (int) (newAnchorTs - liveAnchorTs);
                    anchored = true;
                }
            }
        }

        dispatchNewStreamPacket(packet);
    }

    /**
     * Dispatch a single packet from newStream onto liveStream, applying timeDelta.
     * Caller must ensure {@code anchored} is true before calling. Used by both the
     * steady-state packetReceived() path and the buffer-drain path in process().
     */
    private void dispatchNewStreamPacket(IStreamPacket packet) {
        final byte dataType = packet.getDataType();
        if (IoConstants.TYPE_VIDEO == dataType) {
            if (!isVideoPrimed && isForwardVideo()) {
                isVideoPrimed = true;
                if (newStream.getCodecInfo() != null && newStream.getCodecInfo().getVideoCodec() != null && newStream.getCodecInfo().getVideoCodec().getDecoderConfiguration() != null) {
                    VideoData privateConfig = new VideoData(newStream.getCodecInfo().getVideoCodec().getDecoderConfiguration());
                    privateConfig.setTimestamp(packet.getTimestamp() - timeDelta);
                    liveStream.dispatchInterstitial(privateConfig);
                    IoBuffer buff = newStream.getCodecInfo().getVideoCodec().getKeyframe();
                    if (buff != null) {
                        VideoData keyFrame = new VideoData(buff);
                        keyFrame.setTimestamp(packet.getTimestamp() - timeDelta);
                        liveStream.dispatchInterstitial(keyFrame);
                    }
                }
            }
            if (!hasKeyFrame && ((VideoData) packet).getFrameType() == VideoFrameType.KEYFRAME) {
                hasKeyFrame = true;
            }
            if (!hasKeyFrame) {
                return;
            }
        } else if (isForwardAudio() && !hasAudio && IoConstants.TYPE_AUDIO == dataType) {
            if (codec == InterstitialSession.AAC_AUDIO) {
                if (newStream.getCodecInfo() != null && newStream.getCodecInfo().getAudioCodec() != null && newStream.getCodecInfo().getAudioCodec().getDecoderConfiguration() != null) {
                    AudioData privateConfig = new AudioData(newStream.getCodecInfo().getAudioCodec().getDecoderConfiguration());
                    AudioInfo info = parseAudioParams(privateConfig);
                    audioCompatibility = info.matchesStream;
                    if (info.matchesStream == AudioCompatibility.YES) {
                        hasAudio = true;
                    }
                }
            }
        }

        if (audioCompatibility != AudioCompatibility.YES) {
            return;
        }

        try {
            IStreamPacket packetCopy = null;
            if (packet instanceof AudioData) {
                packetCopy = ((AudioData) packet).duplicate();
            } else if (packet instanceof VideoData) {
                packetCopy = ((VideoData) packet).duplicate();
            } else if (packet instanceof Notify) {
                packetCopy = ((Notify) packet).duplicate();
            }
            if (packetCopy != null) {
                ((IRTMPEvent) packetCopy).setTimestamp(packet.getTimestamp() - timeDelta);
            } else {
                packetCopy = packet;
            }
            dispatchEvent((IEvent) packetCopy, false, liveStream);
        } catch (ClassNotFoundException | IOException e) {
            log.error("", e);
        }
    }

    @Override
    public void queue() {
        // application has opportunity to edit our properties.
    }

    @Override
    public void open() throws Exception {
        if (isDeadStream) { // signal to owner.
            throw new IOException("Overide stream terminated"); // Owner will switch back, or switch to next
                                                                // interstitial.
        }
        // connect packet flow now.
        newStream.addStreamListener(this);
    }

    @Override
    public void process(long timestamp, IRTMPEvent event, IInterstitialStream output) throws IOException {
        if (isDeadStream) {
            throw new IOException("Overide stream terminated");
        } else if (audioCompatibility == AudioCompatibility.NO) {
            log.error("Incompatible audio in live interstitial");
            throw new IOException("Audio not compatible");
        }

        if (!anchored) {
            synchronized (anchorLock) {
                if (!anchored) {
                    if (liveAnchorTs == -1L) {
                        liveAnchorTs = timestamp;
                    }
                    if (!preAnchorBuffer.isEmpty()) {
                        long newAnchorTs = preAnchorBuffer.peekFirst().getTimestamp();
                        timeDelta = (int) (newAnchorTs - liveAnchorTs);
                        anchored = true;
                        IStreamPacket buffered;
                        while ((buffered = preAnchorBuffer.pollFirst()) != null) {
                            dispatchNewStreamPacket(buffered);
                        }
                    }
                }
            }
        }

        dispatchEvent(event, true, liveStream);
    }

    @Override
    public void dispose() {
        newStream.removeStreamListener(this);
        synchronized (anchorLock) {
            anchored = false;
            liveAnchorTs = -1L;
            preAnchorBuffer.clear();
            timeDelta = 0;
        }
    }

    @Override
    public void streamStopped(IProStream arg0) {
        // if either stream stops, we are toast:
        // prepare to signal owner.
        isDeadStream = true;
        // halt override consumer.
        newStream.removeStreamListener(this);
        synchronized (anchorLock) {
            preAnchorBuffer.clear();
        }
    }
}
