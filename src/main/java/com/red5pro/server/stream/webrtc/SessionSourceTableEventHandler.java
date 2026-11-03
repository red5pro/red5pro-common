package com.red5pro.server.stream.webrtc;

/**
 * Receives events from a session source table.
 */
public interface SessionSourceTableEventHandler {

    /**
     * Called when a source has timed out.
     *
     * @param ssrc
     *            synchronization source identifier of the source
     */
    void sessionSourceTimeout(int ssrc);

    /**
     * Called when all input to the session has been abandoned.
     */
    void sessionInputAbandoned();

    /**
     * Checks whether the given source is valid for the session.
     *
     * @param ssrc
     *            synchronization source identifier to check
     * @return true if the source is valid
     */
    boolean isValidSource(int ssrc);

}
