package com.red5pro.interstitial.api;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.net.rtmp.event.VideoData.FrameType;
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

        log.trace("LiveInterstitial ctor. isForwardVideo: {}, isForwardAudio: {}", isInterstitialVideo, isInterstitialAudio);
        this.liveStream = liveStream;
        this.newStream = newStream;
        newStream.addTerminationEventListener(this);
        liveStream.addTerminationEventListener(this);
        this.timeDelta = (int) (liveStream.getCreationTime() - newStream.getCreationTime());
        // log.trace("TIMEDELTA: {} (from liveStream: {}, newStream: {})", timeDelta,
        // liveStream.getCreationTime(), newStream.getCreationTime());
    }

    @Override
    public void packetReceived(IBroadcastStream stream, IStreamPacket packet) {
        if (isDeadStream) {
            // do nothing;
            return;
        }

        if (packet instanceof VideoData) {
            if (!isVideoPrimed && isForwardVideo()) {// send codec configs if present.
                isVideoPrimed = true;
                if (newStream.getCodecInfo().getVideoCodec() != null && newStream.getCodecInfo().getVideoCodec().getDecoderConfiguration() != null) {
                    VideoData privateConfig = new VideoData(newStream.getCodecInfo().getVideoCodec().getDecoderConfiguration());
                    privateConfig.setTimestamp(packet.getTimestamp() - timeDelta);
                    liveStream.dispatchInterstitial(privateConfig);
                    // Send last key frame if present. This wont count for 'hasKeyFrame'
                    IoBuffer buff = newStream.getCodecInfo().getVideoCodec().getKeyframe();
                    if (buff != null) {
                        VideoData keyFrame = new VideoData(buff);
                        keyFrame.setTimestamp(packet.getTimestamp() - timeDelta);
                        liveStream.dispatchInterstitial(keyFrame);
                    }
                } // else forthcoming, no worries.
            }

            // check for live key frame. we need one to proceed.
            if (!hasKeyFrame && ((VideoData) packet).getFrameType() == FrameType.KEYFRAME) {
                hasKeyFrame = true;
            }

            if (!hasKeyFrame) {// can't do nothin' with new vid yet.
                return;
            }
        } else if (isForwardAudio() && !hasAudio && packet instanceof AudioData) {
            // Check for codec private data.
            if (newStream.getCodecInfo().getAudioCodec() != null && newStream.getCodecInfo().getAudioCodec().getDecoderConfiguration() != null) {
                AudioData privateConfig = new AudioData(newStream.getCodecInfo().getAudioCodec().getDecoderConfiguration());
                privateConfig.setTimestamp(packet.getTimestamp() - timeDelta);
                liveStream.dispatchInterstitial(privateConfig);
            } // else forthcoming

            hasAudio = true;
        } // audio could flow right away.

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
        if (isDeadStream) { // signal to owner.
            throw new IOException("Overide stream terminated"); // Owner will switch back, or switch to next
                                                                // interstitial.
        }

        dispatchEvent(event, true, liveStream);
    }

    @Override
    public void dispose() {
        newStream.removeStreamListener(this);
    }

    @Override
    public void streamStopped(IProStream arg0) {
        // if either stream stops, we are toast:
        // prepare to signal owner.
        isDeadStream = true;
        // halt override consumer.
        newStream.removeStreamListener(this);
    }
}
