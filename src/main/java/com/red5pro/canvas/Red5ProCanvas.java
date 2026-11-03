package com.red5pro.canvas;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import org.red5.server.api.scope.IScope;
import org.red5.server.net.rtmp.event.IRTMPEvent;

import com.red5pro.override.IProStream;

/**
 * Canvas that encodes frames supplied by a {@link FrameProvider} and publishes
 * the result either locally or to a remote RTMP server.
 */
public interface Red5ProCanvas {
    /**
     * Set object that will fill buffers.
     *
     * @param provider
     *            FrameProvider
     */
    void setFrameProvider(FrameProvider provider);

    /**
     * Returns the provider that fills the video and audio buffers.
     *
     * @return frame provider
     */
    FrameProvider getFrameProvider();

    /**
     * Sets the target video bitrate.
     *
     * @param bitrate
     *            video bitrate.
     */
    void setBitrate(int bitrate);

    /**
     * Sets the encoding to use for video to h264.
     *
     * @param useH264
     *            true to encode video as H.264
     */
    void setUseH264(boolean useH264);

    /**
     * Sets the encoding to use for video to VP8.
     *
     * @param useVP8
     *            true to encode video as VP8
     */
    void setUseVP8(boolean useVP8);

    /**
     * Start encoding engine.
     */
    void start();

    /**
     * Returns the stream that the canvas publishes into.
     *
     * @return the pro stream
     */
    IProStream getProstream();

    /**
     * Inserts an onMetaData style message built from the given values into the
     * stream.
     *
     * @param onMetaData
     *            name of the metadata handler
     * @param values
     *            metadata key and value pairs
     */
    void insertMetadata(String onMetaData, Map<Object, Object> values);

    /**
     * Inserts a prebuilt metadata event into the stream.
     *
     * @param metaData
     *            metadata event
     */
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
