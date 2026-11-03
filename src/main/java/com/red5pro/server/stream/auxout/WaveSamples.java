package com.red5pro.server.stream.auxout;

/**
 * Holder for a chunk of raw wave (PCM) audio samples and their format.
 */
public class WaveSamples {

    /**
     * Creates an empty holder.
     */
    public WaveSamples() {
    }

    /** Timestamp of the samples. */
    public long timestamp;

    /** Number of audio channels. */
    public int channels;

    /** Sample rate in Hz. */
    public int rate;

    /** Bits per sample. */
    public int bitsPerSample;

    /** Raw sample data. */
    public byte[] samples;
}
