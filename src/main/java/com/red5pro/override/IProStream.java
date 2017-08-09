/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override;

import java.io.IOException;

import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IClientStream;
import org.red5.server.api.stream.IClientBroadcastStream;
import org.red5.server.api.stream.IStream;
import org.red5.server.api.stream.StreamState;
import org.red5.server.net.rtmp.event.Notify;

/**
 * Red5 Pro server-side stream.
 * 
 * @author Paul Gregoire
 * @author Andy Shaules
 */
public interface IProStream extends IStream, IClientStream, IBroadcastStream, IClientBroadcastStream {

    /** {@inheritDoc} */
    void close();

    /** {@inheritDoc} */
    void dispatchEvent(IEvent event);

    /** {@inheritDoc} */
    Notify getMetaData();

    /** {@inheritDoc} */
    StreamState getState();

    /** {@inheritDoc} */
    long getBytesReceived();

    /** {@inheritDoc} */
    void saveAs(String name, boolean append) throws IOException;

    /**
     * Returns the recording state of the stream.
     *
     * @return true if stream is recording, false otherwise
     */
    boolean isRecording();

    /**
     * Stops any currently active recording. 
     */
    void stopRecording();

}
