package com.red5pro.canvas;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import org.red5.server.api.scope.IScope;
import org.red5.server.net.rtmp.event.IRTMPEvent;

import com.red5pro.override.IProStream;

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

    IProStream getProstream();

    void insertMetadata(String onMetaData, Map<Object, Object> values);

    void insertMetadata(IRTMPEvent metaData);

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
     *            port of rtmp server
     * @param path
     *            path of rtmp app
     * @param name
     *            name of publish stream
     * @param connectParams
     *            client connect params
     * @param deathHandler
     *            callback invoked if forwarding dies unexpectedly after starting
     *            (may be null for default logging behavior)
     * @return handle to the forwarding operation, allowing callers to wait for
     *         startup, check status, and stop forwarding
     */
    StreamForwardingHandle forward(String host, int port, String path, String name, Map<String, Object> connectParams, Consumer<Throwable> deathHandler);

    /**
     * Stop streaming process.
     */
    void stop();
}
