package com.red5pro.group;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
     * Provision parameter that defines the class handler alias of the Compositor.
     * Group handlers are registered by alias. See ICompositorRegistry
     */
    public static final String PARAMS_GROUP_TYPE = "group";

    /**
     * Provision parameter that defines the class implementation to use as the
     * IGroupCore handler. Supports IScope implementations. Core handlers are
     * defined by full java clazz path. See ICompositorRegistry.
     */
    public static final String PARAMS_CORE_IMPL = "core";

    /**
     * Provision Parameter to set Mixing bus options.
     */
    public static final String PARAMS_VIDEO_TRACKS = "videotracks";

    /**
     * Provision Parameter to set Mixing bus options.
     */
    public static final String PARAMS_AUDIO_TRACKS = "audiotracks";

    /**
     * End user handle to server core API implementation.
     */
    public static List<ICompositorRegistry> registry = new CopyOnWriteArrayList<>();

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
     * Sets the compositor.
     *
     * @param compositor
     */
    void setCompositor(ExpressionCompositor compositor);

    /**
     * Returns the compositor.
     *
     * @return ExpressionCompositor
     */
    ExpressionCompositor getCompositor();

    /**
     * Adds a participant.
     *
     * @param participant
     * @return true if added and false if compositor is stopped or participant is
     *         already added.
     */
    boolean addParticipant(IParticipant participant);

    /**
     * Removes a participant by its id.
     *
     * @param id
     *            participant id
     * @return true if removed or false if not
     */
    boolean removeParticipant(String id);

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
