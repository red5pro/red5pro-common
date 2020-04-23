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
	 * @return
	 */
	int getRate();
	/**
	 * retun 'PCM ' four-CC
	 * 
	 * @return four-cc
	 */
	int getFormat();
	/**
	 * Channels requested
	 * 
	 * @return count
	 */
	int getChannels();
	/**
	 * Bits per sample. samples received are always 16 bit at this time.
	 * 
	 * @return count.
	 */
	int getBitsPerSample();
	/**
	 * samples decoded are passed to this interface.
	 * 
	 * @param samples
	 */
	void packetReceived(WaveSamples samples);

	void stop();
}
