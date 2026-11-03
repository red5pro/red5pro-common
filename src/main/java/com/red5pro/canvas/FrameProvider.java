package com.red5pro.canvas;

/**
 * Supplies the canvas engine with lifecycle notifications and with video and audio frames to render and mix.
 */
public interface FrameProvider {
    /**
     * Engine is starting.
     *
     * @param startTime
     *            utc milliseconds.
     */
    void startCanvas(long startTime);

    /**
     * Draws a video frame.
     *
     * @param video
     *            yu12 buffer. packed.
     * @param time
     *            milliseconds stream time.
     */
    void drawVideo(byte[] video, long time);

    /**
     * Supplies audio samples for a frame.
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
