package com.red5pro.group;

import org.red5.server.api.IClient;
import org.red5.server.api.event.IEventListener;

import com.red5pro.group.expressions.ExpressionCompositor;
import com.red5pro.media.IMediaSample;

/**
 * Represents a participant of a conference / group.
 * 
 * @author Paul Gregoire
 *
 */
public interface IParticipant extends IClient, IEventListener {

	/**
	 * The key used to store the client object in a http session.
	 */
	public static final String ID = "participant";

	/**
	 * Returns the participants unique identifier.
	 */
	String getId();

	/**
	 * Whether or not this participant indicated its intent to publish.
	 */
	boolean isPublisher();

	/**
	 * Whether or not this participant indicated its intent to subscribe.
	 */
	boolean isSubscriber();

	/**
	 * Context path plus stream name.
	 * 
	 * @return context path including stream name / participant id
	 */
	String getContextPath();

	/**
	 * Returns an array of participant id's which are not to be sent to the
	 * consumer.
	 * 
	 * 
	 * @return array of participant id's to exclude
	 */
	String[] getExcludes();

	/**
	 * Frame of media data, incoming from the participant's camera or mic.
	 * 
	 * @param mediaSample
	 *            audio sample consisting of a MediaSample or a CompositeMediaSample
	 */
	void onMediaSample(IMediaSample mediaSample);

	/**
	 * Media frames to send to subscriber.
	 * 
	 * @param mediaSample
	 */
	void consumeMediaSample(IMediaSample mediaSample);
	/**
	 * 
	 * @param compositor
	 *            the group handler;
	 */
	void setCompositor(ExpressionCompositor compositor);

}
