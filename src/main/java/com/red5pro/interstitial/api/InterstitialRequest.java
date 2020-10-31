package com.red5pro.interstitial.api;

import java.util.List;

/**
 * JSON request to insert one or more interstitials, or to resume a stream.
 *
 * @author Nate Roe
 */
public class InterstitialRequest {
	/**
	 * Passed through to IInterstitialRequestHandler.
	 */
	public String user;

	/**
	 * Passed through to IInterstitialRequestHandler.
	 */
	public String digest;

	/**
	 * Optional. One or more InterstitialInserts must appear to create an
	 * interstitial.
	 */
	public List<InterstitialInsert> inserts;

	/**
	 * Optional. The context and path of the target stream to resume (switches back
	 * to stream's original A/V ending the interstitial insertion).
	 */
	public String resume;

	public String target;

	public String uri;

	public boolean loop;

	public InterstitialDurationControlType type;

	public long start;

	public long duration;

	public long id;

}
