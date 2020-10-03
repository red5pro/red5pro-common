package com.red5pro.interstitial.api;

import java.util.Set;

/**
 * This engine gives program directors the ability to insert alternate content
 * into a live stream such as commercials, simple pauses with filler, station
 * IDs, or emergency broadcast network messages.
 * 
 * @author Andy
 *
 */
public interface InterstitialEngine {
	/**
	 * The Engine allows program directors to customize interstitials based on the
	 * live media quality. The web application can change the URI as needed such as
	 * selecting the matching audio sample rates.
	 * 
	 * @param agent
	 */
	void setAgent(InterstitialConfiguration agent);

	/**
	 * Activates or deactivates the functionality.
	 * 
	 * @param val
	 */
	void setActive(boolean val);

	/**
	 * Returns true if engine is monitoring the live stream.
	 * 
	 * @return
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
	 * @return
	 */
	InterstitialSession getCurrentSession();

	/**
	 * Sorted set will reject adding an item if scheduled for the same time. The
	 * sorting uses 'Comparable' as implemented in InterstitialSession class.
	 * 
	 * @return
	 */
	Set<InterstitialSession> getPrograming();

	/**
	 * Use this method to handle scheduling error. Adding an interstitials scheduled
	 * at the same time as another will throw an InterstitialError.
	 */
	void addInterstitial(InterstitialSession insert) throws InterstitialException;
}
