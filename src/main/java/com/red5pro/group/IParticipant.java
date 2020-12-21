package com.red5pro.group;

import org.red5.server.api.event.IEventListener;

import com.red5pro.group.expressions.ExpressionCompositor;
import com.red5pro.media.IMediaSample;

/**
 * Mixing bus path. Represents a discrete channel within a mixing bus. Can be a
 * client-session or a custom object.
 * 
 * @author Paul Gregoire
 * @author Andy
 */
public interface IParticipant extends IEventListener {

	/**
	 * The key used to store the client object in an attribute store.
	 */
	public static final String ID = "participant";

	/**
	 * Returns the channel unique instance identifier for java hash sets and
	 * sessions.
	 */
	String getId();

	/**
	 * Sets the mixing bus channel input stream name.
	 */
	void setPublisherId(String publisherId);

	/**
	 * Returns the mixing bus channel input stream name.
	 */
	String getPublisherId();

	/**
	 * Whether or not this channel has input capabilities.
	 */
	boolean isPublisher();

	/**
	 * Whether or not this channel has output capabilities.
	 */
	boolean isSubscriber();

	/**
	 * If the channel has input capabilities, returns context of stream.
	 * 
	 * @return context path.
	 */
	String getContextPath();

	/**
	 * Returns an array of PublisherId's which are not to be sent to the consumer.
	 * 
	 * @return array of participant id's to exclude
	 */
	String[] getExcludes();

	/**
	 * If channel has output capabilities, sends media to be consumed.
	 * 
	 * @param mediaSample
	 */
	void consumeMediaSample(IMediaSample mediaSample);
	/**
	 * 
	 * @param event
	 *            application data events.
	 */
	void doExpressionEvent(GroupEvent event);
	/**
	 * 
	 * @param compositor
	 *            the group handler;
	 */
	void setCompositor(ExpressionCompositor compositor);
	/**
	 * 
	 * @return Wall clock of time channel was created.
	 */
	long getCreationTime();
}
