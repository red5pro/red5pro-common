package com.red5pro.group.webrtc;

import java.util.List;

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

	protected volatile boolean starting;

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
	public void setTrickleComplete() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isTrickleComplete() {
		// TODO Auto-generated method stub
		return false;
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
		return starting;
	}

	/**
	 * Publishers fill MediaTracks with h264 and opus packets and pass them to the
	 * compositor. Audio MediaSamples will be contained in a CompositeMediaSample
	 * with Opus alongside its decoded PCM. For video the same type of feature is
	 * allowed wherein it may contain H264 and I420 decoded bytes. <br>
	 * The MediaTrackTest unit test has an example of how to create and push events
	 * to tracks. <br>
	 * 
	 * <pre>
	 * // create media samples to hold opus and pcm
	 * MediaSample opus = MediaSample.build(now, new byte[]{3, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
	 * 		MediaType.AUDIO);
	 * opus.setFourCC(FourCC.OPUS);
	 * // if a sequence number is needed from an incoming Buffer, it can be set
	 * // in the MediaSample
	 * opus.setProSeqNum(buffer.getSequenceNumber());
	 * MediaSample pcm = MediaSample.build(now,
	 * 		new byte[]{0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, MediaType.AUDIO);
	 * pcm.setFourCC(FourCC.PCM);
	 * // create CompositeMediaSample for holding an opus frame with its decoded
	 * // pcm bytes
	 * CompositeMediaSample cms = new CompositeMediaSample();
	 * cms.add(opus);
	 * cms.add(pcm);
	 * // create the audio group event
	 * GroupEvent age = GroupEvent.build(cms, FourCC.OPUS);
	 * 
	 * // IGroupPublisher main job is to fill MediaEvents with opus packet and
	 * // decoded PCMs and then pass them to the compositor.
	 * // RTCSourceStream shall copy payloads from normal publishing pipeline
	 * // and pass to compositor.
	 * // push the event to the compositor
	 * compositor.push(age);
	 * </pre>
	 * 
	 * <br>
	 * Subscribers <br>
	 * Media Packet with the same frame in multiple formats. Find correct format and
	 * send to subscriber track id.
	 * 
	 * // track id 0-N maps to ssrc of rtcbroadcaststream transform chain. int
	 * trackId = arg0.track; // timestamp/scale over scale to find seconds as float.
	 * int timestamp = arg0.timestamp; // 1000 = milliseconds; 48000, 90000 etc. int
	 * scale = arg0.scale; // may or may not be set. subscribers have internal
	 * sequence in rtpinfo struct. short seq = arg0.sequenceNumber;
	 * 
	 * // WHich version of this frame will we send? for (FrameMagazine page :
	 * arg0.frames) { if (page.fourCC == MediaTypes.OPUS) { // We typically send
	 * "Opus frame without RTP header" to transform where rtp // header is added. if
	 * (page.container == 0) { // Opus frame without RTP header // Create Buffer
	 * Object and send to RTCBroadcastStream subscriber job. } else if
	 * (page.container == MediaTypes.RTP) { // Opus frame with RTP header } else if
	 * (page.container == MediaTypes.ActionMovieFormat) { // Opus frame in amf
	 * packet } } else if (page.fourCC == MediaTypes.VP8) { if (page.container == 0)
	 * { // VP8 frame or frame chunk without RTP header } else if (page.container ==
	 * MediaTypes.RTP) { // VP8 packet with RTP header } else if (page.container ==
	 * MediaTypes.ActionMovieFormat) { // VP8 frame in amf packet } } else if
	 * (page.fourCC == MediaTypes.H264) { if (page.container == 0) { // H264 nalu
	 * without RTP header } else if (page.container == MediaTypes.RTP) { // H264
	 * packet with RTP header } else if (page.container ==
	 * MediaTypes.ActionMovieFormat) { // H264 frame in amf packet } } }
	 * 
	 */

}
