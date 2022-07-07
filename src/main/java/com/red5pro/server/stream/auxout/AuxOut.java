package com.red5pro.server.stream.auxout;

/**
 * Supports audio out as PCM. To request raw audio samples from a live stream,
 * implement this interface and add to IProstream instance at publish start.
 *
 * @author Andy Shaules
 *
 */
public interface AuxOut extends Runnable {
    /**
     * rate requested
     *
     * @return audio sample rate
     */
    int getRate();

    /**
     * retun 'PCM ' four-CC
     *
     * @return four-cc format
     */
    int getFormat();

    /**
     * Channels requested
     *
     * @return count 1 for mono, 2 for stereo.
     */
    int getChannels();

    /**
     * Bits per sample. samples received are always 16 bit at this time.
     *
     * @return count bits in a sample.
     */
    int getBitsPerSample();

    /**
     * samples decoded are passed to this interface.
     *
     * @param samples
     *            frame of pcm
     */
    void packetReceived(WaveSamples samples);

    /**
     * Stop the canvas
     */
    void stop();
}
