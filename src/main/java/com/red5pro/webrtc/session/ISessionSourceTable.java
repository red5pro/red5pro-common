package com.red5pro.webrtc.session;

/**
 * Session tracker for SSRC based entities.
 * 
 * @author Andy Shaules
 */
public interface ISessionSourceTable {

    /**
     * End session tracker lifecycle.
     */
    boolean cancel();

    /**
     * Tap SSRC entry.
     * 
     * @param ssrc
     */
    void tapEntry(int ssrc);

    /**
     * Return number of active sessions.
     * 
     * @return active session count
     */
    int getSize();

    /**
     * Returns the idle state of the owning connection.
     *  
     * @return true if the table is empty or last update is greater than the max time allowed, false otherwise
     */
    boolean isIdle();

    /**
     * Removes an SSRC entry.
     * 
     * @param ssrc
     */
    void onBye(int ssrc);

}
