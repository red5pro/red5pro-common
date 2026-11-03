package com.red5pro.canvas;

/**
 * No-op {@link FrameProvider} implementation that ignores all frame and
 * lifecycle callbacks.
 */
public class DefaultFrameProvider implements FrameProvider {
    /**
     * Builder for canvas api
     */
    public static CanvasFactory factory;

    /**
     * Creates a new no-op frame provider.
     */
    public DefaultFrameProvider() {
    }

    @Override
    public void startCanvas(long startTime) {

    }

    @Override
    public void drawVideo(byte[] video, long i) {

    }

    @Override
    public void fillSound(byte[] audio, long i) {

    }

    /**
     * Stops the canvas; this implementation does nothing.
     */
    public void stopCanvas() {

    }

}
