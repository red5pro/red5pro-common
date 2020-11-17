package com.red5pro.group.webrtc;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.session.IoSession;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.group.ConferenceParticipant;
import com.red5pro.media.IMediaSample;
import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.media.sdp.SessionDescription;
import com.red5pro.override.IProStream;
import com.red5pro.server.stream.webrtc.IRTCStream;

/**
 * A WebRTC entity which may or may not be both a publisher and subscriber in a
 * conference.
 * 
 * @author Paul Gregoire
 *
 */
public class RTCConferenceParticipant extends ConferenceParticipant implements IRTCStream {

	protected Logger log = LoggerFactory.getLogger(RTCConferenceParticipant.class);

	protected boolean isTrace = log.isTraceEnabled();

	protected boolean isDebug = log.isDebugEnabled();

	protected AtomicBoolean starting = new AtomicBoolean(false);

	protected SessionDescription offerSdp;

	protected SessionDescription answerSdp;

	protected IScope scope;

	// the published stream from this participant
	protected IProStream proStream;

	@Override
	public void init(SDPUserAgent userAgent) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAudioMediaSample(long timestamp, IMediaSample mediaSample) {
		// determine if its composite or not, normally we'd only care about opus

	}

	@Override
	public void onVideoMediaSample(long timestamp, IMediaSample mediaSample) {
		// grab the buffer and pass it on to the output implementation

	}

	@Override
	public String getName() {
		// returns the participants identifier
		return id;
	}

	public void setOffer(SessionDescription sdp) {
		this.offerSdp = sdp;
		log.debug("Offer: {}", sdp);
	}

	/**
	 * Returns the offer sdp in SessionDescription form.
	 *
	 * @return offer sdp
	 */
	@Override
	public SessionDescription getSdp() {
		return offerSdp;
	}

	/**
	 * Returns the answer sdp in SessionDescription form.
	 *
	 * @return answer sdp
	 */
	public SessionDescription getAnswerSdp() {
		return answerSdp;
	}

	@Override
	public String getLocalSdp() {
		return null;
	}

	@Override
	public List<String> getLocalCandidates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRemoteCandidates(int mlineIndex, String remoteCandidates) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemoteProperties(SessionDescription sdp) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProStream getProStream() {
		return proStream;
	}

	@Override
	public IScope getScope() {
		return scope;
	}

	@Override
	public boolean isIceController() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSubscriberModeStandby(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSubscriberModeStandby() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IoSession getIoSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStarting() {
		return starting.get();
	}

	/**
	 * Returns the media id for at the given index in the local SDP.
	 *
	 * @param index
	 * @return media id
	 */
	public String getMediaId(int index) {
		try {
			return offerSdp.getMediaDescriptions()[index].getMediaId();
		} catch (Exception e) {
			log.warn("Invalid media id index: {}", index);
		}
		return null;
	}

}
