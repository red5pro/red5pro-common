package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.codec.AACAudio;
import org.red5.server.api.event.IEvent;
import org.red5.server.messaging.IMessageInput;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.VideoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(InterstitialSession.class);

    /** Audio codec identifier for AAC. */
    public static final int AAC_AUDIO = 'A' | ('A' << 8) | ('A' << 16) | (' ' << 24);

    /** Audio codec identifier for Speex. */
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

    /** Number of audio channels. */
    public int audioChannels;

    /** Audio sample rate in Hz. */
    public int audioSampleRate;

    /** Video width in pixels. */
    public int width;

    /** Video height in pixels. */
    public int height;

    /** Whether the inserted audio is compatible with the live stream. */
    public AudioCompatibility audioCompatibility = AudioCompatibility.UNKNOWN;

    /** True if the insert should start immediately. */
    public boolean immediate;

    /**
     * Audio Codec
     */
    public int codec;

    private boolean isInterstitialAudio;

    private boolean isInterstitialVideo;

    /** Optional one-shot callback fired the first time {@link #dispose()} runs. {@code null} unless the caller registered one via {@link #setOnDisposed}. */
    private volatile Runnable onDisposed;

    /** Latches the {@link #onDisposed} callback so it fires exactly once even if {@code dispose()} is invoked twice. */
    private final AtomicBoolean disposedCallbackFired = new AtomicBoolean(false);

    /**
     * Creates a session.
     *
     * @param isInterstitialAudio true to forward audio from the inserted stream
     * @param isInterstitialVideo true to forward video from the inserted stream
     */
    public InterstitialSession(boolean isInterstitialAudio, boolean isInterstitialVideo) {
        this.isInterstitialAudio = isInterstitialAudio;
        this.isInterstitialVideo = isInterstitialVideo;
    }

    /**
     * Register a one-shot callback that fires the first time this session is disposed. Useful for
     * "session is done" hooks: pod completion, AdStorm impression dispatch, metrics, releasing
     * per-stream gates, etc.
     *
     * <p>The callback runs synchronously on whichever thread invokes {@code dispose()} —
     * typically the engine packet-processing thread — so keep the work light or hand off to your
     * own executor inside the {@code Runnable}.
     *
     * <p>The callback fires regardless of why dispose was triggered: PTS window expired, FLV
     * source exhausted, or the session was force-cancelled by an immediate insert. It does
     * <b>not</b> fire on FLV loop iterations (those don't dispose).
     *
     * <p>Subclass contract: implementations of {@link #dispose()} must call
     * {@link #fireDisposedCallbackOnce()} at the end of their teardown to deliver this
     * callback. Built-in subclasses ({@link FLVInterstitial}, {@link LiveInterstitial}) do this.
     *
     * @param onDisposed the callback, or {@code null} to clear an existing registration
     */
    public void setOnDisposed(Runnable onDisposed) {
        this.onDisposed = onDisposed;
    }

    /**
     * Fires the {@link #setOnDisposed registered} callback if any, exactly once. Safe to call
     * multiple times — subsequent calls are no-ops. Exceptions thrown by the callback are
     * caught and logged so they don't propagate into the engine.
     *
     * <p>Subclasses must invoke this at the end of their {@link #dispose()} implementation.
     */
    protected final void fireDisposedCallbackOnce() {
        if (!disposedCallbackFired.compareAndSet(false, true)) {
            return;
        }
        Runnable cb = this.onDisposed;
        if (cb == null) {
            return;
        }
        try {
            cb.run();
        } catch (RuntimeException e) {
            log.warn("InterstitialSession onDisposed callback threw; swallowing", e);
        }
    }

    /**
     * Include the metada of the insert. Default is false. Metadata from inserted
     * source material will be dropped.
     */
    public boolean includeMetaData;

    /**
     * Sets the audio parameters of the stream being inserted into.
     *
     * @param codec audio codec identifier, such as {@link #AAC_AUDIO}
     * @param audioChannels number of audio channels
     * @param audioSampleRate audio sample rate in Hz
     */
    public void setAudioParams(int codec, int audioChannels, int audioSampleRate) {
        this.codec = codec;
        this.audioChannels = audioChannels;
        this.audioSampleRate = audioSampleRate;
    }

    /**
     * Sets the video dimensions of the stream being inserted into.
     *
     * @param width video width in pixels
     * @param height video height in pixels
     */
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
     * Release stream resources and end the stream lifecycle. Implementations must call
     * {@link #fireDisposedCallbackOnce()} at the end of their teardown so any registered
     * {@link #setOnDisposed onDisposed} callback fires.
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
     * Indicates whether audio of the new stream is used.
     *
     * @return true if we should use audio from the new stream insert
     */
    public boolean isForwardAudio() {
        return isInterstitialAudio;
    }

    /**
     * Indicates whether video of the new stream is used.
     *
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

    /**
     * Parses the AAC decoder configuration of the given audio data and checks it against the audio parameters of this session.
     *
     * @param event audio data holding the decoder configuration
     * @return the parsed audio parameters and whether they match the stream
     */
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

    /**
     * Audio parameters parsed from the decoder configuration of an inserted stream.
     */
    protected static class AudioInfo {

        /** Creates an empty instance. */
        public AudioInfo() {
        }

        /** Audio codec identifier. */
        public int codec;

        /** Number of audio channels. */
        public int audioChannels;

        /** Audio sample rate in Hz. */
        public int audioSampleRate;

        /** Whether the parsed audio is compatible with the target stream. */
        public AudioCompatibility matchesStream = AudioCompatibility.UNKNOWN;
    }

    /**
     * Whether the audio of the inserted stream is compatible with the stream being inserted into.
     */
    protected static enum AudioCompatibility {
        /** Compatibility has not been determined. */
        UNKNOWN,
        /** Audio is compatible with the live stream. */
        YES,
        /** Audio is not compatible with the live stream. */
        NO;
    }
}
