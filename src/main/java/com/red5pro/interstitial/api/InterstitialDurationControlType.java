package com.red5pro.interstitial.api;

/**
 * The time measurement type for determining start and end.
 *
 * @see InterstitialDurationControl
 *
 * @author Andy
 * @author Nate Roe
 */
public enum InterstitialDurationControlType {
    /** Session starts 'now'. Session ends with API call */
    INDEFINITE,
    /** Session ends at streamTimeStart + duration */
    STREAM_CLOCK,
    /** Session ends with wall clock time. wallClockStart+duration */
    WALL_CLOCK
}