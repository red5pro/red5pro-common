/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override;

import java.io.IOException;

import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IClientBroadcastStream;
import org.red5.server.net.rtmp.event.Notify;

/**
 * Red5 Pro server-side stream.
 * 
 * @author Paul Gregoire
 * @author Andy Shaules
 */
public interface IProStream extends IClientBroadcastStream {

    /** {@inheritDoc} */
    public void close();

    /** {@inheritDoc} */
    public void dispatchEvent(IEvent event);

    /** {@inheritDoc} */
    public Notify getMetaData();

    /** {@inheritDoc} */
    public void saveAs(String name, boolean append) throws IOException;

    /**
     * Returns the recording state of the stream.
     *
     * @return true if stream is recording, false otherwise
     */
    public boolean isRecording();

    /**
     * Stops any currently active recording. 
     */
    public void stopRecording();
}
