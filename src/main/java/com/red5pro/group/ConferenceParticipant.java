package com.red5pro.group;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.red5.server.AttributeStore;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.scope.IScope;

import com.red5pro.media.FourCC;
import com.red5pro.media.IMediaSample;
import com.red5pro.server.ConnectionAttributeKey;
import com.red5pro.util.IdGenerator;

/**
 * Model representation for a conference participant.
 * 
 * @author Paul Gregoire
 *
 */
public class ConferenceParticipant extends AttributeStore implements IParticipant {

	// identifier for this participant
	protected final String id = Long.toHexString(IdGenerator.generateId(24)); // 192 bits

	// timestamp for when this participant was created
	protected final long created = System.currentTimeMillis();

	// conference participant will be party to a single conference scope
	protected Set<IScope> scopes = Collections.emptySet();

	// conference participant will have a single connection
	protected Set<IConnection> connections = Collections.emptySet();

	// participant ids to exclude when outputting media to this participant.
	protected Set<String> excludes = new CopyOnWriteArraySet<>();

	// determination of publisher or subscriber will be false until SDP's are
	// processed
	protected boolean publisher, subscriber;

	/**
	 * Connect this participant to the conference.
	 * 
	 * @param conferenceScope
	 * @param connection
	 */
	public void connect(IScope conferenceScope, IConnection connection) {
		scopes = Collections.singleton(conferenceScope);
		connections = Collections.singleton(connection);
		// add our id to our excludes
		addExclude(id);
	}

	/**
	 * Disconnect from the current conference.
	 */
	@Override
	public void disconnect() {
		scopes.clear();
		connections.clear();
		excludes.clear();
	}

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

	@Override
	public Collection<IScope> getScopes() {
		return scopes;
	}

	@Override
	public String getContextPath() {
		Optional<IScope> opt = scopes.stream().findFirst();
		if (opt.isPresent()) {
			return String.format("%s/%s", opt.get().getContextPath(), id);
		}
		return null;
	}

	@Override
	public Set<IConnection> getConnections() {
		return connections;
	}

	@Override
	public Set<IConnection> getConnections(IScope scope) {
		if (scope == null) {
			return getConnections();
		}
		Set<IClient> scopeClients = scope.getClients();
		if (scopeClients.contains(this)) {
			for (IClient cli : scopeClients) {
				if (this.equals(cli)) {
					return cli.getConnections();
				}
			}
		}
		return Collections.emptySet();
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

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<String> getPermissions(IConnection conn) {
		Collection<String> result = (Collection<String>) conn.getAttribute(ConnectionAttributeKey.PERMISSIONS);
		if (result == null) {
			result = Collections.emptySet();
		}
		return result;
	}

	/** {@inheritDoc} */
	public boolean hasPermission(IConnection conn, String permissionName) {
		final Collection<String> permissions = getPermissions(conn);
		return permissions.contains(permissionName);
	}

	/** {@inheritDoc} */
	public void setPermissions(IConnection conn, Collection<String> permissions) {
		if (permissions == null) {
			conn.removeAttribute(ConnectionAttributeKey.PERMISSIONS);
		} else {
			conn.setAttribute(ConnectionAttributeKey.PERMISSIONS, permissions);
		}
	}

	/** {@inheritDoc} */
	public boolean isBandwidthChecked() {
		return false;
	}

	/** {@inheritDoc} */
	public void checkBandwidth() {
		// XXX not certain we'll be doing this here
	}

	/** {@inheritDoc} */
	public Map<String, Object> checkBandwidthUp(Object[] params) {
		return Collections.emptyMap();
	}

	@Override
	public void notifyEvent(IEvent event) {
		// XXX check the incoming event to ensure its source id isn't in the exclusion
		// list
		if (event instanceof GroupEvent) {
			GroupEvent ge = (GroupEvent) event;
			// check excludes first
			String eventSourceId = ge.getSourceId();
			if (!excludes.contains(eventSourceId)) {
				// XXX the IParticipant implementation is to take it from here and use the
				// MediaSample content
				// its interested in and ignore the rest of the event.
				FourCC eventFourCC = ge.getFourCC();
				// first cut doesnt expect any packetization wrapping the event media content
				if (FourCC.isAudio(eventFourCC)) {
					onAudioMediaSample(ge.getTimestamp(), (IMediaSample) ge.getObject());
				} else if (FourCC.isVideo(eventFourCC)) {
					onVideoMediaSample(ge.getTimestamp(), (IMediaSample) ge.getObject());
				} else {
					// if the group events four cc denotes a container / packaged type, handle it
					// here
					switch (eventFourCC) {
						case AMF : // protocol
						case MPTS : // protocol
							// how did we get an protocol level event here?
							break;
						case RTMP : // transport
						case RTP : // transport
						case SRTP : // transport
						case RTSP : // transport
						case SRT : // transport
							// how did we get an transport level event here?
							break;
						case METADATA :
							// TODO route the metadata
							break;
						case JSON :
							// TODO route the json
							break;
						case JSON_RPC :
							// TODO handle the JSON RPC based on it being a notification or not
							break;
						default :
							log.warn("Unexpected identifier type: {}", eventFourCC);
							break;
					}
				}
			} else {
				log.debug("Event source is in excludes: {}, skipping it", eventSourceId);
			}
		} else {
			log.debug("Event is not group type, ignoring it");
		}
	}

	@Override
	public void onAudioMediaSample(long timestamp, IMediaSample mediaSample) {
		// XXX implementation is expected to handle samples
	}

	@Override
	public void onVideoMediaSample(long timestamp, IMediaSample mediaSample) {
		// XXX implementation is expected to handle samples
	}

}