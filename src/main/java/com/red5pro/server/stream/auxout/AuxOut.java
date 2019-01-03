package com.red5pro.server.stream.auxout;
/**
 * Audio out as PCM.
 * 
 * @author Andy Shaules
 *
 */
public interface AuxOut extends Runnable {
	int getRate();

	int getFormat();

	int getChannels();

	int getBitsPerSample();

	void packetReceived(WaveSamples samples);

	void stop();
}
