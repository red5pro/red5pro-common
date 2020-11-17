package com.red5pro.group.webrtc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger log = LoggerFactory.getLogger(RTCConferenceSession.class);

	// creation timestamp
	private final long created = System.currentTimeMillis();

	/**
	 * Whether or not the session has started.
	 */
	protected volatile boolean started;

	/**
	 * The backing stream.
	 */
	private IProStream proStream;

	/**
	 * The webrtc based stream.
	 */
	private IRTCStream rtcStream;

	public RTCConferenceSession(IRTCStream rtcStream, IProStream proStream) {
		log.debug("RTCConferenceSession - rtc stream: {} pro stream: {}", rtcStream, proStream);
		// set the rtc stream
		this.rtcStream = rtcStream;
		// source pro stream
		this.proStream = proStream;
	}

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
		return started;
	}

	@Override
	public void setHandler(Object rtcSessionService) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getCreated() {
		return created;
	}

	@Override
	public IRTCStream getRtcStream() {
		return rtcStream;
	}

	@Override
	public IProStream getProStream() {
		return proStream;
	}

	@Override
	public void updateProStream(IProStream stream) {
		// XXX do nothing here
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (created ^ (created >>> 32));
        result = prime * result + ((proStream == null) ? 0 : proStream.getPublishedName().hashCode());
        result = prime * result + ((rtcStream == null) ? 0 : rtcStream.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RTCConferenceSession other = (RTCConferenceSession) obj;
        if (created != other.created) {
            return false;
        }
        IRTCStream otherRtcStream = other.getRtcStream();
        if (!rtcStream.getName().equals(otherRtcStream.getName())) {
            return false;
        }
        if (proStream == null) {
            if (other.proStream != null) {
                return false;
            }
        } else if (!proStream.equals(other.proStream)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RTCConferenceSession [created=" + created + ", rtcStream=" + rtcStream + ", proStream=" + proStream + "]";
    }

}
