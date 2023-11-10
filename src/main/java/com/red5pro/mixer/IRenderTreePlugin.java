package com.red5pro.mixer;

import java.util.List;

import com.google.gson.Gson;
import com.red5pro.mixer.nodes.RenderTree;

/**
 * Interface to the RenderTreePlugin (aka, BrewMixerPlugin, NodeMixerPlugin).
 *
 * CRUD operations for video mixer
 *
 * @author Nate Roe
 */
public interface IRenderTreePlugin {
    public static final String NAME = "RenderTreePlugin";

    /**
     * Verify the digest in the AuthorizedRequest does in fact contain the cluster password.
     *
     * @param request
     * @param action
     * @return
     */
    public boolean isAuthorized(AuthorizedRequest request, String action);

    /**
     * Get all events
     *
     * @return the eventId of every current mixer event
     */
    public List<String> getAllEvents();

    /**
     * Get the RenderTrees for a given event
     *
     * @param eventId
     * @return an ordered list of RenderTrees, one for each submix.
     */
    public List<RenderTree> getRenderTrees(String eventId) throws MixerNotFoundException;

    /**
     * Create a new mixer event with the given provisioning information.
     *
     * @param provision
     * @throws MixerException
     */
    public void create(MixerProvision provision) throws MixerException;

    /**
     * Update the RenderTrees for an existing mixer event
     *
     * @param eventId
     * @param renderTrees must supply one RenderTree per submix
     * @throws MixerNotFoundException
     * @throws MixerException
     */
    public void update(String eventId, List<RenderTree> renderTrees) throws MixerNotFoundException, MixerException;

    /**
     * Stop all mixers for the given event
     *
     * @param eventId
     * @throws MixerNotFoundException
     */
    public void stop(String eventId) throws MixerNotFoundException;

    /**
     * @return Gson with TypeAdapters to read RenderTree nodes
     */
    public Gson getGson();
}
