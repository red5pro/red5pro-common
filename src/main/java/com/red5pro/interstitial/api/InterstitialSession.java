package com.red5pro.interstitial.api;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.codec.AACAudio;
import org.red5.server.api.event.IEvent;
import org.red5.server.messaging.IMessageInput;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.VideoData;

/**
 * An InterstitialSession describes an interstitial insertion operation to the
 * InterstitialEngine.
 *
 * Implementations of InterstitialSession define the process(...) method for
 * user-defined processing, which then ultimately calls dispatchEvent(...) to
 * send events on to the target stream.
 *
 * @author Andy
 * @author Nate Roe
 */
public abstract class InterstitialSession implements Comparable<InterstitialSession> {

    public static final int AAC_AUDIO = 'A' | ('A' << 8) | ('A' << 16) | (' ' << 24);

    public static final int SPEX_AUDIO = 'S' | ('P' << 8) | ('E' << 16) | ('X' << 24);

    /**
     * Arbitrary
     */
    public long id;

    /**
     * Resource
     */
    public String fileName;

    /**
     * Packet source.
     */
    public IMessageInput io;

    /**
     * When to stop insertion.
     */
    public InterstitialDurationControl sessionControl;

    public int audioChannels;

    public int audioSampleRate;

    public int width;

    public int height;

    public AudioCompatibility audioCompatibility = AudioCompatibility.UNKNOWN;

    /**
     * Audio Codec
     */
    public int codec;

    private boolean isInterstitialAudio;

    private boolean isInterstitialVideo;

    public InterstitialSession(boolean isInterstitialAudio, boolean isInterstitialVideo) {
        this.isInterstitialAudio = isInterstitialAudio;
        this.isInterstitialVideo = isInterstitialVideo;
    }

    /**
     * Include the metada of the insert. Default is false. Metadata from inserted
     * source material will be dropped.
     */
    public boolean includeMetaData;

    public void setAudioParams(int codec, int audioChannels, int audioSampleRate) {
        this.codec = codec;
        this.audioChannels = audioChannels;
        this.audioSampleRate = audioSampleRate;
    }

    public void setVideoParams(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Called when this session is next.
     */
    public abstract void queue();

    /**
     * Called at the moment this session is activated.
     *
     * @throws Exception
     *             if an exception occurs
     */
    public abstract void open() throws Exception;

    /**
     * When presented with a timestamp, we dispatch alternate content into the
     * prostream. This may be one or none packets depending on the timestamp.
     *
     * @param timestamp
     *            stream timestamp.
     * @param event
     *            the event to process
     * @param output
     *            dispatchInterstitial method
     * @throws IOException
     *             if an IOException occurs
     */
    public abstract void process(long timestamp, IRTMPEvent event, IInterstitialStream output) throws IOException;

    /**
     * Release stream resources and end the stream lifecycle.
     */
    public abstract void dispose();

    /**
     * This assumes no nulls in our structure, in particular sessionControl.
     *
     * @param them
     *            the other InterstitialSession
     * @return Returns a negative integer, zero, or a positive integer as this
     *         object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(InterstitialSession them) {
        if (them == null) {// by the book.
            throw new NullPointerException();
        }

        long ourTime = 0;
        switch (sessionControl.getLifeCycle()) {
            case STREAM_CLOCK:
                // 0 to infinity.
                ourTime = sessionControl.getStart();
                break;
            case INDEFINITE:
                // now!
                ourTime = 0;
                break;
            case WALL_CLOCK:
                ourTime = sessionControl.getStart() - System.currentTimeMillis();
                break;
        }

        long theirTime = 0;
        switch (them.sessionControl.getLifeCycle()) {
            case STREAM_CLOCK:
                // 0 to infinity.
                theirTime = them.sessionControl.getStart();
                break;
            case INDEFINITE:
                // now!
                theirTime = 0;
                break;
            case WALL_CLOCK:
                theirTime = them.sessionControl.getStart() - System.currentTimeMillis();
                break;
        }

        int results = (int) (ourTime - theirTime);
        if (results == 0) {
            // shazbot! Attempt to avoid throwing scheduling error.
            results = (int) (id - them.id);
        }

        return results;
    }

    /**
     * @return true if we should use audio from the new stream insert
     */
    public boolean isForwardAudio() {
        return isInterstitialAudio;
    }

    /**
     * @return true if we should use video from the new stream insert
     */
    public boolean isForwardVideo() {
        return isInterstitialVideo;
    }

    /**
     * Dispatch this event to the interstitial stream, filtering for audio/video
     * packets and directing them according to the isInterstitialAudio and
     * isInterstitialVideo properties.
     *
     * @param event
     *            the event to dispatch
     * @param isOriginal
     *            is this from the original stream (or else, from the interstitial
     *            stream)?
     * @param stream
     *            the underlying stream to which the event should be dispatched, if
     *            appropriate
     */
    public void dispatchEvent(IEvent event, boolean isOriginal, IInterstitialStream stream) {
        boolean isAudio = AudioData.class.isAssignableFrom(event.getClass());
        boolean isVideo = VideoData.class.isAssignableFrom(event.getClass());

        boolean isNeither = !isVideo && !isAudio;
        boolean isReallyAudio = (isOriginal ^ isForwardAudio()) && isAudio;
        boolean isReallyVideo = (isOriginal ^ isForwardVideo()) && isVideo;

        if (isNeither || isReallyAudio || isReallyVideo) {
            stream.dispatchInterstitial(event);
        }
    }

    public AudioInfo parseAudioParams(AudioData event) {
        AudioInfo ret = new AudioInfo();
        IoBuffer audioData = event.getData();
        byte flgs = audioData.get();

        int eventCodec = (flgs & 0xF0) >> 4;

        switch (eventCodec) {
            case 10:
                ret.codec = InterstitialSession.AAC_AUDIO;
                break;
            case 11:
                ret.codec = InterstitialSession.SPEX_AUDIO;
                break;
        }

        if (ret.codec == codec) {
            if (InterstitialSession.AAC_AUDIO == ret.codec) {
                //log.info("audio {}  {}",codecId,hdr);
                byte hdr = audioData.get();
                if (hdr == 0) {
                    byte[] cfg = new byte[2];
                    audioData.get(cfg);
                    //int objectType = (cfg[0] >> 3) & 0x1F;//five bits.
                    int freqIndex = ((cfg[0] & 0x7) << 1) | ((cfg[1] >> 7) & 0x1);
                    ret.audioChannels = (cfg[1] & 0x78) >> 3;
                    ret.audioSampleRate = AACAudio.AAC_SAMPLERATES[freqIndex];

                    if (audioChannels == ret.audioChannels && audioSampleRate == ret.audioSampleRate) {
                        ret.matchesStream = AudioCompatibility.YES;
                    } else {
                        ret.matchesStream = AudioCompatibility.NO;
                    }
                }

            } else if (InterstitialSession.SPEX_AUDIO == ret.codec) {
                ret.matchesStream = AudioCompatibility.YES;
            }

        } else {
            ret.matchesStream = AudioCompatibility.NO;
        }

        audioData.rewind();

        return ret;
    }

    protected static class AudioInfo {
        public int codec;

        public int audioChannels;

        public int audioSampleRate;

        public AudioCompatibility matchesStream = AudioCompatibility.UNKNOWN;
    }

    protected static enum AudioCompatibility {
        UNKNOWN, YES, NO;
    }
}
