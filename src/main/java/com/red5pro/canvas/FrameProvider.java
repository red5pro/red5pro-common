package com.red5pro.canvas;

public interface FrameProvider {
	/**
	 * Engine is starting.
	 * 
	 * @param startTime
	 *            utc milliseconds.
	 */
	void startCanvas(long startTime);
	/**
	 * 
	 * @param video
	 *            yu12 buffer. packed.
	 * @param time
	 *            milliseconds stream time.
	 */
	void drawVideo(byte[] video, long time);
	/**
	 * 
	 * @param audio
	 *            sample buffer, 1024 samples per frame for AAC.
	 * @param time
	 *            milliseconds stream time
	 */
	void fillSound(byte[] audio, long time);
	/**
	 * Engine is stopping.
	 */
	void stopCanvas();

}
