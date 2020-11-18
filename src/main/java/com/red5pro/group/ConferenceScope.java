package com.red5pro.group;

import java.beans.ConstructorProperties;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

import org.red5.server.Server;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.IServer;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.IScopeHandler;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.scope.Scope;

import com.red5pro.cluster.streams.Provision;
import com.red5pro.group.expressions.ExpressionCompositor;
import com.red5pro.media.MediaTrack;

/**
 * Represents a subscope to other scopes which represents a group of
 * participants in a conference scenario.
 * 
 * @author Paul Gregoire
 */
public class ConferenceScope extends Scope implements IGroupCore {

	/**
	 * Provision representing this conference grouping.
	 */
	protected Provision provision;

	/**
	 * Conference participants are NOT in the Scope.clients collection
	 */
	protected CopyOnWriteArraySet<IParticipant> participants = new CopyOnWriteArraySet<>();

	/**
	 * Expression compositor.
	 */
	protected ExpressionCompositor compositor;

	{
		type = ScopeType.ROOM;
	}

	/**
	 * Creates a scope (this isnt expected to be called)
	 */
	@ConstructorProperties(value = {""})
	public ConferenceScope() {
		this(null, ScopeType.ROOM, null, false);
	}

	/**
	 * Creates scope via parameters.
	 * 
	 * @param parent
	 * @param type
	 * @param name
	 * @param persistent
	 */
	public ConferenceScope(IScope parent, ScopeType type, String name, boolean persistent) {
		super(parent, type, name, persistent);
	}

	@Override
	public boolean start() {
		// TODO perform logic specific to the conference
		return super.start();
	}

	@Override
	public void stop() {
		// TODO perform logic specific to the conference
		super.stop();
	}

	@Override
	public boolean connect(IConnection conn, Object[] params) {
		log.debug("Connect - scope: {} connection: {}", this, conn);
		if (hasParent() && !parent.connect(conn, params)) {
			log.debug("Connection to parent failed");
			return false;
		}
		if (hasHandler() && !getHandler().connect(conn, this, params)) {
			log.debug("Connection to handler failed");
			return false;
		}
		if (!conn.isConnected()) {
			log.debug("Connection is not connected");
			// timeout while connecting client
			return false;
		}
		final IParticipant client = (IParticipant) conn.getClient();
		// we would not get this far if there is no handler
		if (hasHandler() && !getHandler().join(client, this)) {
			return false;
		}
		// checking the connection again? why?
		if (!conn.isConnected()) {
			// timeout while connecting client
			return false;
		}
		// add the client and event listener
		if (participants.add(client) && addEventListener(conn)) {
			log.debug("Added client");
			// increment conn stats
			connectionStats.increment();
			// get connected scope
			IScope connScope = conn.getScope();
			log.trace("Connection scope: {}", connScope);
			if (this.equals(connScope)) {
				final IServer server = getServer();
				if (server instanceof Server) {
					((Server) server).notifyConnected(conn);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void disconnect(IConnection conn) {
		log.debug("Disconnect: {}", conn);
		// call disconnect handlers in reverse order of connection. ie. roomDisconnect
		// is called before appDisconnect.
		final IClient client = conn.getClient();
		if (client == null) {
			// early bail out
			removeEventListener(conn);
			connectionStats.decrement();
			if (hasParent()) {
				parent.disconnect(conn);
			}
			return;
		}
		// remove it if it exists
		if (participants.remove(client)) {
			IScopeHandler handler = getHandler();
			if (handler != null) {
				try {
					handler.disconnect(conn, this);
				} catch (Exception e) {
					log.error("Error while executing \"disconnect\" for connection {} on handler {}. {}",
							new Object[]{conn, handler, e});
				}
				try {
					// there may be a timeout here ?
					handler.leave(client, this);
				} catch (Exception e) {
					log.error("Error while executing \"leave\" for client {} on handler {}. {}",
							new Object[]{conn, handler, e});
				}
			}
			// remove listener
			removeEventListener(conn);
			// decrement if there was a set of connections
			connectionStats.decrement();
			if (this.equals(conn.getScope())) {
				final IServer server = getServer();
				if (server instanceof Server) {
					((Server) server).notifyDisconnected(conn);
				}
			}
		}
		if (hasParent()) {
			parent.disconnect(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void dispatchEvent(IEvent event) {
		// dispatch to participants
		participants.forEach(participant -> participant.notifyEvent(event));
	}

	@Override
	public boolean handleEvent(IEvent event) {
		// TODO handle the event directed at our conference scope
		if (event instanceof GroupEvent) {
			// push the event into the compositor
			compositor.push((GroupEvent) event);
		}
		return super.handleEvent(event);
	}

	/** {@inheritDoc} */
	public int getActiveClients() {
		return participants.size();
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	@Override
	public Provision getProvision() {
		return provision;
	}

	@Override
	public void setCompositor(ExpressionCompositor compositor) {
		this.compositor = compositor;
	}

	@Override
	public ExpressionCompositor getCompositor() {
		return compositor;
	}

	@Override
	public IParticipant getParticipant(String id) {
		Optional<IParticipant> opt = participants.stream().filter(participant -> participant.getId().equals(id))
				.findFirst();
		if (opt.isPresent()) {
			return opt.get();
		}
		return null;
	}

	@Override
	public int getParticipantCount() {
		return getActiveClients();
	}

	@Override
	public MediaTrack[] getAudioTracks() {
		return compositor != null ? compositor.getAudioTracks() : null;
	}

	@Override
	public MediaTrack[] getVideoTracks() {
		return compositor != null ? compositor.getVideoTracks() : null;
	}

	@Override
	public MediaTrack getTrack(int index) {
		return compositor != null ? compositor.getTrack(index) : null;
	}

	@Override
	public MediaTrack getTrackById(String id) {
		return compositor != null ? compositor.getTrackById(id) : null;
	}

}