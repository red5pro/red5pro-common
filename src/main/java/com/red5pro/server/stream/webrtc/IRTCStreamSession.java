package com.red5pro.server.stream.webrtc;

import java.util.concurrent.Future;

import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.override.IProStream;

/**
 * Interface for RTCStreamSession to allow interoperability.
 * 
 * @author Paul Gregoire
 *
 */
public interface IRTCStreamSession {

	void start(SDPUserAgent userAgentEnum);

	void stop();

	boolean isStarted();

	void setHandler(Object rtcSessionService);

	long getCreated();

	Object getRtcStream();

	IProStream getFlashStream();

	Future<?> getCreationFuture();

	void setCreationFuture(Future<Boolean> createFuture);

	void updateFlashStream(IProStream stream);

}
