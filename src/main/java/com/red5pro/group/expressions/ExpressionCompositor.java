package com.red5pro.group.expressions;

import com.red5pro.cluster.streams.Provision;
import com.red5pro.group.GroupEvent;
import com.red5pro.group.IGroupCore;
import com.red5pro.group.IParticipant;
import com.red5pro.media.MediaTrack;

public interface ExpressionCompositor {

	/**
	 * Returns the owner of this; expected to be set in constructor or builder.
	 * 
	 * @return IGroup
	 */
	IGroupCore getOwner();

	void setOwner(IGroupCore owner);

	/**
	 * Sets the local provision reference.
	 * 
	 * @param provision
	 */
	void setProvision(Provision provision);

	/**
	 * Returns the local provision reference, primary instance is in the owner.
	 * 
	 * @return Provision or null if not set
	 */
	Provision getProvision();

	/**
	 * Returns whether or not participants exist.
	 * 
	 * @return true if there are participants and false otherwise
	 */
	boolean hasReferenceCount();

	/**
	 * Returns the number of participants in the group.
	 * 
	 * @return participant count
	 */
	long getReferenceCount();

	/**
	 * Sets the track count.
	 * 
	 * @param trackCount
	 */
	void setTrackCount(int trackCount);

	/**
	 * Returns the track count.
	 * 
	 * @return number of MediaTracks
	 */
	int getTrackCount();

	/**
	 * Returns an array containing existing audio MediaTrack instances.
	 * 
	 * @return MediaTrack array of audio type or empty array if not found
	 */
	MediaTrack[] getAudioTracks();

	/**
	 * Returns an array containing existing video MediaTrack instances.
	 * 
	 * @return MediaTrack array of video type or empty array if not found
	 */
	MediaTrack[] getVideoTracks();

	/**
	 * Returns a track by its index in the MediaTrack array.
	 * 
	 * @param index
	 *            track index
	 * @return MediaTrack or null if indexed track doesnt exist
	 */
	MediaTrack getTrack(int index);

	/**
	 * Returns a track with a matching id.
	 * 
	 * @param id
	 *            tracks identifier
	 * @return MediaTrack or null if the track doesnt exist
	 */
	MediaTrack getTrackById(String id);

	/**
	 * Push a group event into the compositor.
	 * 
	 * @param event
	 */
	void push(GroupEvent event);
	/**
	 * End user api. Pass private context into composition.
	 * 
	 * @param user
	 *            defined event.
	 */
	void doExpressionEvent(Object event);

	/**
	 * 
	 * @param participant
	 * @return
	 */
	public boolean addParticipant(IParticipant participant);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeParticipant(String id);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public IParticipant getParticipant(String id);

	/**
	 * 
	 * @return
	 */
	public int getParticipantCount();
	/**
	 * 
	 */
	public void stop();
}
