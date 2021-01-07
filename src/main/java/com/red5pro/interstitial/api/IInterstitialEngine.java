package com.red5pro.interstitial.api;

import java.util.Set;

/**
 * This engine gives program directors the ability to insert alternate content
 * into a live stream such as commercials, simple pauses with filler, station
 * IDs, or emergency broadcast network messages.
 *
 * @author Andy
 */
public interface IInterstitialEngine {
    /**
     * The Engine allows program directors to customize interstitials based on the
     * live media quality. The web application can change the URI as needed such as
     * selecting the matching audio sample rates.
     *
     * @param config
     *            the IInterstitialConfiguration
     */
    void setAgent(IInterstitialConfiguration config);

    /**
     * Activates or deactivates the functionality.
     *
     * @param isActive
     *            true if active
     */
    void setActive(boolean isActive);

    /**
     * Returns true if engine is monitoring the live stream.
     *
     * @return true if active
     */
    boolean isActive();

    /**
     * Tells the engine the current interstitial has an error. Engine will skip and
     * check the next in line for activation.
     */
    void skipError();

    /**
     * Call to restart program from an 'Indefinite' interstitial or override
     * normally scheduled resume times.
     */
    void resumeProgram();

    /**
     * Returns current running session or null.
     *
     * @return the current InterstitialSession
     */
    InterstitialSession getCurrentSession();

    /**
     * Sorted set will reject adding an item if scheduled for the same time. The
     * sorting uses 'Comparable' as implemented in InterstitialSession class.
     *
     * @return all of the scheduled InterstitialSession
     */
    Set<InterstitialSession> getPrograming();

    /**
     * Use this method to handle scheduling error. Adding an interstitials scheduled
     * at the same time as another will throw an InterstitialError.
     *
     * @param insert
     *            the insert to add
     * @throws InterstitialException
     *             if an exception occurs
     */
    void addInterstitial(InterstitialSession insert) throws InterstitialException;
}
