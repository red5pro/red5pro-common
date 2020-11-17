package com.red5pro.server.stream.webrtc;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.red5.server.api.scope.IScope;
import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.media.sdp.SessionDescription;
import com.red5pro.override.IProStream;

/**
 * Represents a stream used in the WebRTC subsystem.
 * 
 * @author Paul Gregoire
 */
public interface IRTCStream {

	/**
	 * Returns the name for this stream instance.
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * Get the local SDP as a SessionDescription.
	 * 
	 * @return sdp
	 */
	SessionDescription getSdp();

	/**
	 * Get the local SDP as a string.
	 * 
	 * @return sdp
	 */
	String getLocalSdp();

	/**
	 * Returns the local candidates.
	 * 
	 * @return candidates
	 */
	List<String> getLocalCandidates();

	/**
	 * Sets remote candidates.
	 * 
	 * @param mlineIndex
	 * @param remoteCandidates
	 */
	void setRemoteCandidates(int mlineIndex, String remoteCandidates);

	/**
	 * Sets remote ICE and DTLS properties. DTLS properties will arrive in the
	 * "offer" or "answer" depending upon our direction; the streaming cannot start
	 * until we have these.
	 * 
	 * @param sdp
	 */
	void setRemoteProperties(SessionDescription sdp);

	/**
	 * Initialize the streams and all the configuration steps.
	 * 
	 * @param userAgent
	 *            the user agent connected
	 * @throws Exception
	 */
	void init(SDPUserAgent userAgent) throws Exception;

	/**
	 * Starts the stream instance.
	 * 
	 * @return true if started and false otherwise
	 */
	boolean start();

	/**
	 * Stops the stream instance and cleans-up.
	 */
	void stop();

	/**
	 * Returns the associated pro/flash stream.
	 * 
	 * @return ProStream
	 */
	IProStream getProStream();

	/**
	 * Returns the scope.
	 * 
	 * @return scope
	 */
	IScope getScope();

	/**
	 * Set tickle state to complete.
	 */
	void setTrickleComplete();

	/**
	 * Returns whether or not trickle is complete.
	 * 
	 * @return true if complete and false otherwise
	 */
	boolean isTrickleComplete();

	/**
	 * Returns whether or not we're controlling ICE.
	 * 
	 * @return true if controlling and false otherwise
	 */
	boolean isIceController();

	void setSubscriberModeStandby(boolean state);

	boolean isSubscriberModeStandby();

	/**
	 * Returns the IoSession being used for media and ICE messages.
	 * 
	 * @return IoSession or null if not connected nor established
	 */
	IoSession getIoSession();

	/**
	 * Whether or not the stream is starting.
	 * 
	 * @return true if starting and false otherwise
	 */
	boolean isStarting();

}
