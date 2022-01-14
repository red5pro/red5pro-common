package com.red5pro.group;

import java.beans.ConstructorProperties;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

import org.red5.server.api.event.IEvent;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.scope.Scope;

import com.red5pro.cluster.streams.Provision;
import com.red5pro.group.expressions.ExpressionCompositor;
import com.red5pro.media.MediaTrack;

/**
 * Represents a subscope to other scopes which represents a group of
 * participants in a conference scenario. To use this class as the
 * Implementation, add parameter 'core' to the provision with value
 * 'com.red5pro.group.ConferenceScope'
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

    private Long emptyTime;

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
    @ConstructorProperties(value = { "" })
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
        log.debug("Scope stop");
        if (compositor != null && !compositor.hasReferenceCount()) {
            compositor.stop();
        }
        super.stop();
    }

    /** {@inheritDoc} */
    @Override
    public void dispatchEvent(IEvent event) {
        log.debug("dispatchEvent: {}", event);
        // dispatch to participants
        participants.forEach(participant -> participant.notifyEvent(event));
        super.dispatchEvent(event);
    }

    @Override
    public boolean handleEvent(IEvent event) {
        log.debug("handleEvent: {}", event);
        // TODO handle the event directed at our conference scope
        if (event instanceof GroupEvent) {
            // push the event into the compositor
            compositor.push((GroupEvent) event);
        }
        return super.handleEvent(event);
    }

    /** {@inheritDoc} */
    @Override
    public int getActiveClients() {
        log.debug("Active clients {}", participants.size());
        return participants.size();// plus mixer program + anecdotal shared objects/etc
    }

    public int getActiveConncetions() {
        int k = super.getActiveConnections();
        log.debug("Active connections {}", k);
        return k;// plus mixer program + anecdotal shared objects/etc
    }

    @Override
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
    public boolean addParticipant(IParticipant participant) {
        boolean result = participants.add(participant);
        emptyTime = null;
        return result;
    }

    @Override
    public boolean removeParticipant(String id) {
        IParticipant participant = getParticipant(id);
        if (participant != null) {
            boolean result = participants.remove(participant);
            if (participants.size() == 0 && emptyTime == null) {
                emptyTime = System.currentTimeMillis();
            }
            return result;
        }
        return false;
    }

    @Override
    public IParticipant getParticipant(String id) {
        Optional<IParticipant> opt = participants.stream().filter(participant -> participant.getId().equals(id)).findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    public IParticipant getParticipantByPublisherId(String publisherId) {
        Optional<IParticipant> opt = participants.stream().filter(participant -> participant.getPublisherId().equals(publisherId)).findFirst();
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

    @Override
    public void onClose() {
        // no-op
    }

    @Override
    public Long getEmptyTime() {
        return emptyTime;
    }
}