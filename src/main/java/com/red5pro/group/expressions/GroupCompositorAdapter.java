package com.red5pro.group.expressions;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
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

	@Override
	public long getReferenceCount() {
		return owner.getParticipantCount();
	}

	@Override
	public boolean hasReferenceCount() {
		return owner.getParticipantCount() > 0;
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
		Optional.ofNullable(provision.getParameters().get("group")).ifPresent(group -> {
			if ((Boolean) group) {
				Map<String, Object> params = provision.getParameters();
				audioTrackCount = (Integer) params.get("audiotracks");
				videoTrackCount = (Integer) params.get("videotracks");
				// set the track count
				this.trackCount = audioTrackCount + videoTrackCount;
				// ensure the tracks is large enough
				if (tracks.length > trackCount) {
					tracks = new MediaTrack[trackCount];
				}
				// XXX if and when the tracks are not opus and h264, we'll have to add them to
				// the provision
				for (int i = 0; i < audioTrackCount; i++) {
					tracks[i] = new MediaTrack(MediaType.AUDIO, FourCC.OPUS, String.format("audio%d", i));
				}
				for (int i = 0; i < videoTrackCount; i++) {
					tracks[i + audioTrackCount] = new MediaTrack(MediaType.VIDEO, FourCC.H264,
							String.format("video%d", i));
				}
			}
		});
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

}
