package com.red5pro.canvas;

public interface CanvasFactory {
	/**
	 * Create a canvas.
	 * 
	 * @param width
	 * @param height
	 * @param framerate,
	 *            use zero to disable.
	 * @param sampleRate,
	 *            use zero to disable.
	 * @param channelCount
	 * @return
	 */
	Red5ProCanvas createCanvas(int width, int height, int framerate, int sampleRate, int channelCount);
}
