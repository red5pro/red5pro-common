package com.red5pro.group.webrtc;

import java.util.concurrent.Future;

import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.override.IProStream;
import com.red5pro.server.stream.webrtc.IRTCStream;
import com.red5pro.server.stream.webrtc.IRTCStreamSession;

/**
 * A session to contain and establish handling for a conference participant.
 * 
 * @author Paul Gregoire
 *
 */
public class RTCConferenceSession implements IRTCStreamSession {

	@Override
	public void start(SDPUserAgent userAgentEnum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHandler(Object rtcSessionService) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getCreated() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IRTCStream getRtcStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProStream getFlashStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> getCreationFuture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreationFuture(Future<Boolean> createFuture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFlashStream(IProStream stream) {
		// TODO Auto-generated method stub

	}

}
