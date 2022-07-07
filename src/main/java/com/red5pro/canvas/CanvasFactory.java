package com.red5pro.canvas;

public interface CanvasFactory {
    /**
     * Create a canvas.
     *
     * @param width
     *            width of video canvas
     * @param height
     *            height of video canvas
     * @param framerate,
     *            use zero to disable.
     * @param sampleRate,
     *            use zero to disable.
     * @param channelCount
     *            audio channel count, 1 or 2
     * @return the canvas instance
     */
    Red5ProCanvas createCanvas(int width, int height, int framerate, int sampleRate, int channelCount);
}
