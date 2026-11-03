package com.red5pro.server;

import org.red5.net.websocket.WebSocketConnection;

import com.red5pro.server.datachannel.IDataChannelConnection;

/**
 * Container class for signaling channel implementations.
 *
 * @author Paul Gregoire
 *
 */
public class SignalingChannel {

    /**
     * Implementation / transport type.
     */
    public static enum Type {
        /** Unassigned or unrecognized channel. */
        RAW,
        /** RTMP connection. */
        RTMP,
        /** WebSocket connection. */
        WEBSOCKET,
        /** WebRTC data channel. */
        DATACHANNEL,
        /** RTP transport. */
        RTP;
    };

    private final Type type;

    // XXX Paul I don't like leaving this generic, but with the various types we
    // can't use a shared interface att
    private final Object signalingChannel;

    /**
     * Creates a signaling channel wrapper.
     *
     * @param type the channel implementation type
     * @param signalingChannel the underlying channel object
     */
    public SignalingChannel(Type type, Object signalingChannel) {
        this.type = type;
        this.signalingChannel = signalingChannel;
    }

    /**
     * Returns the number of bytes read from the underlying channel; 0 for types that do not report it.
     *
     * @return bytes read
     */
    public long getReadBytes() {
        switch (type) {
            case WEBSOCKET:
                return ((WebSocketConnection) signalingChannel).getReadBytes();
            case DATACHANNEL:
                return ((IDataChannelConnection) signalingChannel).getReadBytes();
            default:
                // TODO use reflection for any other type

                break;
        }
        return 0;
    }

    /**
     * Returns the number of bytes written to the underlying channel; 0 for types that do not report it.
     *
     * @return bytes written
     */
    public long getWrittenBytes() {
        switch (type) {
            case WEBSOCKET:
                return ((WebSocketConnection) signalingChannel).getWrittenBytes();
            case DATACHANNEL:
                return ((IDataChannelConnection) signalingChannel).getWrittenBytes();
            default:
                // TODO use reflection for any other type

                break;
        }
        return 0;
    }

    /**
     * Close the underlying implementation.
     */
    public void close() {
        switch (type) {
            case WEBSOCKET:
                ((WebSocketConnection) signalingChannel).close();
                break;
            case DATACHANNEL:
                ((IDataChannelConnection) signalingChannel).close();
                break;
            default:
                // TODO use reflection for any other type

                break;
        }
    }

    /**
     * Returns the channel implementation type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the underlying channel object.
     *
     * @return the channel, cast to the appropriate type for Type
     */
    public Object getSignalingChannel() {
        return signalingChannel;
    }

    /**
     * Returns the signal channel as a WebSocket if it is of WebSocket type and null
     * if not.
     *
     * @return WebSocketConnection or null if incorrect type
     */
    public WebSocketConnection getWebSocketSignalingChannel() {
        return type == Type.WEBSOCKET ? (WebSocketConnection) signalingChannel : null;
    }

    /**
     * Returns the signal channel as a DataChannel if it is of DataChannel type and
     * null if not.
     *
     * @return IDataChannelConnection or null if incorrect type
     */
    public IDataChannelConnection getDataChannelSignalingChannel() {
        return type == Type.DATACHANNEL ? (IDataChannelConnection) signalingChannel : null;
    }

    /**
     * Builder for ease of use.
     *
     * @param signalChannel the underlying channel object; its type is inferred from its class
     * @return SignalingChannel
     */
    public final static SignalingChannel build(Object signalChannel) {
        // determine the type if we can
        Type type = Type.RAW; // default / unassigned
        if (signalChannel instanceof WebSocketConnection) {
            type = Type.WEBSOCKET;
        } else if (signalChannel instanceof IDataChannelConnection) {
            type = Type.DATACHANNEL;
        }
        SignalingChannel signalingChannel = new SignalingChannel(type, signalChannel);
        return signalingChannel;
    }

}
