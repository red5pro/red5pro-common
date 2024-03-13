package com.red5pro.canvas;

import java.io.IOException;
import java.util.Map;

import org.red5.server.api.scope.IScope;
import org.red5.server.net.rtmp.event.Notify;

public interface Red5ProCanvas {
    /**
     * Set object that will fill buffers.
     *
     * @param provider
     *            FrameProvider
     */
    void setFrameProvider(FrameProvider provider);

    /**
     *
     * @return frame provider
     */
    FrameProvider getFrameProvider();

    /**
     *
     * @param bitrate
     *            video bitrate.
     */
    void setBitrate(int bitrate);

    /**
     * Sets the encoding to use for video to h264.
     *
     * @param useH264
     */
    void setUseH264(boolean useH264);

    /**
     * Sets the encoding to use for video to VP8.
     *
     * @param useVP8
     */
    void setUseVP8(boolean useVP8);

    /**
     * Start encoding engine.
     */
    void start();

    /**
     * Start output engine, option 1 local publish. Call after starting encoding
     * engine.
     *
     * @param scope
     *            to broadcast in
     * @param name
     *            name of broacdcast
     * @param record
     *            to record or not
     * @param appeand
     *            to appeand existing recording or not
     * @throws IOException
     *             if the stream cant be created
     */
    void loopBack(IScope scope, String name, boolean record, boolean appeand) throws IOException;

    /**
     * Start output engine option 2 rtmp publish to network. Call after calling
     * start.
     *
     * @param host
     *            host of rtmp server.
     * @param port
     *            port of rtmo server
     * @param path
     *            path of rtmp app
     * @param name
     *            name of publish stream
     * @param connectParams
     *            client connect params
     * @throws IOException
     *             if connection fails
     */
    void forward(String host, int port, String path, String name, Map<String, Object> connectParams) throws IOException;

    /**
     * Insert metadata to stream.
     * @param event
     */
    void insertMetadata(Notify event); 
        
    /**
     * Stop streaming process.
     */
    void stop();
}
