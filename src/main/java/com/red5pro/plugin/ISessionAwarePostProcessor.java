package com.red5pro.plugin;

import org.red5.media.processor.IPostProcessor;

/**
 * Extends IPostProcessor interface by adding specific stream session id which is unique to each broadcast regardless of the broadcast's published name or alias.
 * Use IStream.getName. IStream is extended up to IClientBroadcastStream and ultimately IProstream.
 * @author Andy
 * <br>
 * {@link org.red5.server.api.stream.IStream#getName()}
 */
public interface ISessionAwarePostProcessor extends IPostProcessor {
    /**
     * Set unique ID for the related stream session. Use IStream.getName extended by IClientBroadcastStream.
     * This is just an id of the stream and NOT the name that is used at client side to subscribe to the stream.
     * A stream with a particular name can be published over and over. Each publish will have its own unique id/name.
     * {@link org.red5.server.api.stream.IStream#getName()}
     * @param sessionId unique id from IStream.getName()
     * @return true if implemented be the service.
     *
     */
    default boolean setStreamSessionId(String iStreamName) {
        return false;
    }

    /**
     * Get the unique id of the publish session this service is processing from IStream.getName().
     * This is just an id of the stream and NOT the name that is used at client side to subscribe to the stream.
     * {@link org.red5.server.api.stream.IStream#getName()}
     * @return id if implemented by the service or null.
     */
    default String getStreamSessionId() {
        return null;
    }
}
