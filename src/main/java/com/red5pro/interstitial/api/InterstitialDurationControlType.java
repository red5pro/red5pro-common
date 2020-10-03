package com.red5pro.interstitial.api;

/**
 * The time measurement type for determining start and end.
 * 
 * @see InterstitialDurationControl
 */
public enum InterstitialDurationControlType {
	/** Session ends at streamTimeStart + duration */
	StreamClock,
	/** Session starts 'now'. Session ends with API call */
	Indefinite,
	/** Session ends with wall clock time. wallClockStart+duration */
	WallClock
}