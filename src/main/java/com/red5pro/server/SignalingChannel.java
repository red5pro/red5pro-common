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
		RAW, RTMP, WEBSOCKET, DATACHANNEL, RTP;
	};

	private final Type type;

	// XXX Paul I don't like leaving this generic, but with the various types we
	// can't use a shared interface att
	private final Object signalingChannel;

	public SignalingChannel(Type type, Object signalingChannel) {
		this.type = type;
		this.signalingChannel = signalingChannel;
	}

	public long getReadBytes() {
		switch (type) {
			case WEBSOCKET :
				return ((WebSocketConnection) signalingChannel).getReadBytes();
			case DATACHANNEL :
				return ((IDataChannelConnection) signalingChannel).getReadBytes();
			default :
				// TODO use reflection for any other type

				break;
		}
		return 0;
	}

	public long getWrittenBytes() {
		switch (type) {
			case WEBSOCKET :
				return ((WebSocketConnection) signalingChannel).getWrittenBytes();
			case DATACHANNEL :
				return ((IDataChannelConnection) signalingChannel).getWrittenBytes();
			default :
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
			case WEBSOCKET :
				((WebSocketConnection) signalingChannel).close();
				break;
			case DATACHANNEL :
				((IDataChannelConnection) signalingChannel).close();
				break;
			default :
				// TODO use reflection for any other type

				break;
		}
	}

	public Type getType() {
		return type;
	}

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
	 * @param signalChannel
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
