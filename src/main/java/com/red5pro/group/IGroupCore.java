package com.red5pro.group;

import org.red5.server.api.event.IEvent;

import com.red5pro.cluster.streams.Provision;
import com.red5pro.group.expressions.ExpressionCompositor;
import com.red5pro.media.MediaTrack;

/**
 * Common interface for group implementations.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire
 *
 */
public interface IGroupCore {

	/**
	 * Returns all the audio tracks.
	 * 
	 * @return Audio tracks
	 */
	MediaTrack[] getAudioTracks();

	/**
	 * Returns all the video tracks.
	 * 
	 * @return video tracks
	 */
	MediaTrack[] getVideoTracks();

	/**
	 * Returns a track at the given index or null if one does not exist.
	 * 
	 * @param index
	 *            track index
	 * @return MediaTrack or null if indexed track doesnt exist
	 */
	MediaTrack getTrack(int index);

	/**
	 * Returns a track matching the given id or null if one does not exist.
	 * 
	 * @param id
	 *            tracks identifier
	 * @return MediaTrack or null if the track doesnt exist
	 */
	MediaTrack getTrackById(String id);

	/**
	 * Returns the compositor.
	 * 
	 * @return ExpressionCompositor
	 */
	ExpressionCompositor getExpressionCompositor();

	/**
	 * Returns a participant by their id.
	 * 
	 * @param id
	 *            of the participant
	 * @return participant matching the given id or null if not found
	 */
	IParticipant getParticipant(String id);

	/**
	 * Returns the participant count.
	 * 
	 * @return total participants
	 */
	int getParticipantCount();

	/**
	 * Set the provision.
	 * 
	 * @param provision
	 */
	void setProvision(Provision provision);

	/**
	 * Returns the provision.
	 * 
	 * @return Provision
	 */
	Provision getProvision();

	/**
	 * Overrides Scope.handleEvent() for visibility in groups.
	 * 
	 * @param event
	 * @return true if event was handled and false otherwise
	 */
	boolean handleEvent(IEvent event);

}
