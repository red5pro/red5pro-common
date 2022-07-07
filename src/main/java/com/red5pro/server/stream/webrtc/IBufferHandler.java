package com.red5pro.server.stream.webrtc;

import java.util.List;

import javax.media.Buffer;

/**
 * Interface for streams that will handle audio and video buffers.
 *
 * @author Paul Gregoire
 */
public interface IBufferHandler {

    /**
     * Called by an audio encoder, supplying an encoded buffer data for output.
     *
     * @param outBuf
     *            encoded and often already packetized buffer
     */
    void audioBufferReceived(Buffer outBuffer);

    /**
     * Called by an video encoder, supplying an encoded buffer data for output.
     *
     * @param outBuf
     *            encoded and often already packetized buffer
     */
    void videoBufferReceived(Buffer outBuffer);

    void setVideoGroupSize(int size);

    void setKeyGroup(List<Buffer> keyGroup);

}
