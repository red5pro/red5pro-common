package com.red5pro.group.expressions;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.red5pro.cluster.streams.Provision;
import com.red5pro.group.GroupEvent;
import com.red5pro.group.IGroupCore;
import com.red5pro.group.IParticipant;
import com.red5pro.media.FourCC;
import com.red5pro.media.MediaTrack;
import com.red5pro.media.MediaType;

/**
 * Group focused base implementation of ExpressionCompositor.
 * 
 * @author Paul Gregoire
 *
 */
public class GroupCompositorAdapter implements ExpressionCompositor {

	protected Logger log = null;// LoggerFactory.getLogger(GroupCompositorAdapter.class);

	protected boolean isTrace;

	protected Map<String, IParticipant> participants = new ConcurrentHashMap<>();

	/**
	 * Group / conference owning this compositor.
	 */
	protected IGroupCore owner;

	/**
	 * Local reference for the groups provision.
	 */
	protected WeakReference<Provision> provisionRef = new WeakReference<>(null);

	protected int audioTrackCount = 3;

	protected int videoTrackCount = 1;
	/**
	 * Number of tracks in this group. By default we initialize with 3 audio and 1
	 * video.
	 */
	private int trackCount = audioTrackCount + videoTrackCount;

	/**
	 * Media tracks which make up the conference group.
	 */
	protected MediaTrack[] tracks = new MediaTrack[trackCount];

	private boolean hasMain;

	@Override
	public long getReferenceCount() {
		return participants.size() + (hasMain ? 1 : 0);
	}

	@Override
	public boolean hasReferenceCount() {
		return !participants.isEmpty() || hasMain;
	}

	@Override
	public IGroupCore getOwner() {
		return owner;
	}

	@Override
	public void setProvision(Provision provision) {
		provisionRef = new WeakReference<>(provision);
		// create the tracks based on the provision, increasing the length if needed
		log.debug("Deserialized: {}", provision);
		Map<String, Object> params = provision.getParameters();
		// Evaluates to the type of group this is.
		// If we have been created, group is defined.
		String groupType = params.get("group").toString();
		// default override?
		if (params.containsKey("audiotracks")) {
			audioTrackCount = (Integer) params.get("audiotracks");
		}
		if (params.containsKey("videotracks")) {
			videoTrackCount = (Integer) params.get("videotracks");
		}
		// set the track count
		this.trackCount = audioTrackCount + videoTrackCount;
		// ensure the tracks is large enough
		if (tracks.length > trackCount) {
			tracks = new MediaTrack[trackCount];
		}
		// Implementations can extend this class and override setProvision.
		// Default implementation is webrtc.
		// Formats may change based on IParticipant input.
		// The Format of the publisher input is the driving factor.
		// The IParticipant Adapter may convert data before consuming.
		for (int i = 0; i < audioTrackCount; i++) {
			tracks[i] = new MediaTrack(MediaType.AUDIO, FourCC.OPUS, String.format("audio%d", i));
		}
		for (int i = 0; i < videoTrackCount; i++) {
			tracks[i + audioTrackCount] = new MediaTrack(MediaType.VIDEO, FourCC.H264, String.format("video%d", i));
		}
		log.info("Setting group parameters type: {} Tracks A: {} V: {}", groupType, audioTrackCount, videoTrackCount);
	}

	@Override
	public Provision getProvision() {
		return provisionRef.get();
	}

	@Override
	public void push(GroupEvent event) {
		if (isTrace) {
			log.trace("Event pushed in from id: {} fourCC: {}", event.getSourceId(), event.getFourCC());
		}
	}

	@Override
	public void doExpressionEvent(Object obj) {
		if (isTrace) {
			log.trace("Do expression event");
		}
		// User defined Object.
	}

	@Override
	public MediaTrack[] getAudioTracks() {
		return (MediaTrack[]) Arrays.stream(tracks).filter(MediaTrack.isAudioTrack()).toArray(MediaTrack[]::new);
	}

	@Override
	public MediaTrack[] getVideoTracks() {
		return (MediaTrack[]) Arrays.stream(tracks).filter(MediaTrack.isVideoTrack()).toArray(MediaTrack[]::new);
	}

	@Override
	public MediaTrack getTrack(int index) {
		if (index < tracks.length) {
			return tracks[index];
		}
		return null;
	}

	@Override
	public MediaTrack getTrackById(String id) {
		for (MediaTrack track : tracks) {
			if (track.getId().equals(id)) {
				return track;
			}
		}
		return null;
	}

	@Override
	public void setTrackCount(int trackCount) {
		this.trackCount = trackCount;
	}

	@Override
	public int getTrackCount() {
		return trackCount;
	}

	@Override
	public boolean addParticipant(IParticipant participant) {
		participants.put(participant.getId(), participant);
		return true;
	}

	@Override
	public boolean removeParticipant(String id) {
		return participants.remove(id) != null;
	}

	@Override
	public IParticipant getParticipant(String id) {
		return participants.get(id);
	}

	@Override
	public int getParticipantCount() {
		return participants.size();
	}

	@Override
	public void setOwner(IGroupCore owner) {
		this.owner = owner;
	}

	public void mainProgramStart() {
		log.info("main start");
		hasMain = true;
	}

	public void mainProgramStop() {
		log.info("main stop");
		hasMain = false;
	}

    @Override
    public void stop() {       
        
    }

}
