package com.red5pro.group;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.red5.server.api.event.IEvent;

import com.red5pro.group.expressions.ExpressionCompositor;
import com.red5pro.media.IMediaSample;
import com.red5pro.util.IdGenerator;

/**
 * Model representation for a conference participant which may or may not
 * provide data, and may or may not consume data.
 * 
 * @author Paul Gregoire
 *
 */
public abstract class ConferenceParticipant implements IParticipant {

	// unique identifier for this participant.
	private final String id = Long.toHexString(IdGenerator.generateId(24)); // 192 bits

	// publisher id / input stream name
	protected String publisherId;

	// timestamp for when this participant was created
	protected final long created = System.currentTimeMillis();

	// Publisher ids to exclude when outputting media to this participant.
	protected Set<String> excludes = new CopyOnWriteArraySet<>();

	// determination of publisher or subscriber will be false until SDP's are
	// processed
	protected boolean publisher, subscriber;

	protected ExpressionCompositor compositor;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getCreationTime() {
		return created;
	}

	@Override
	public boolean isPublisher() {
		return publisher;
	}

	@Override
	public boolean isSubscriber() {
		return subscriber;
	}

	/**
	 * Add an id for exclusion.
	 * 
	 * @param exclude
	 */
	public void addExclude(String exclude) {
		excludes.add(exclude);
	}

	/**
	 * Remove an id exclusion.
	 * 
	 * @param exclude
	 */
	public void removeExclude(String exclude) {
		excludes.remove(exclude);
	}

	@Override
	public String[] getExcludes() {
		return excludes.toArray(new String[0]);
	}
	/**
	 * Media output to send to end point.
	 */
	public abstract void consumeMediaSample(IMediaSample sample);
	/**
	 * Application and Notify events.
	 */
	public abstract void doExpressionEvent(GroupEvent event);
	/**
	 * Application data.
	 */
	@Override
	public void notifyEvent(IEvent event) {
		// Is this us?
		if (!this.equals(event.getSource())) {
			if (event instanceof GroupEvent) {
				GroupEvent ge = (GroupEvent) event;
				String eventSourceId = null;// ge.getSourceId();
				if (event.getSource() instanceof IParticipant) {
					// Notify from other channel?
					eventSourceId = ((IParticipant) event.getSource()).getPublisherId();
					if (excludes.contains(eventSourceId)) {
						return;
					}
				}

				doExpressionEvent(ge);

			} else if (!this.equals(event.getSource())) {
				GroupEvent unknown = GroupEvent.build(event.getType(), event);
				doExpressionEvent(unknown);
			}
		}
	}

	@Override
	public void setCompositor(ExpressionCompositor compositor) {
		this.compositor = compositor;
	}

	@Override
	public void setPublisherId(String publisherId) {
		publisher = publisherId != null;
		this.publisherId = publisherId;
	}

	@Override
	public String getPublisherId() {
		return publisherId;
	}
}
