package com.red5pro.interstitial.api;

/**
 * This class represents a servlet request from administration for adding a list
 * of interstitials to programs.
 *
 * @author Andy
 */
public class InterstitialInsert {
    /**
     * User defined
     */
    public long id;

    /**
     * Stream path
     */
    public String target;

    /**
     * Interstitial path
     */
    public String interstitial;

    /**
     * Timing information
     */
    public InterstitialDurationControlType type;

    /**
     * Loop media at URI if duration is longer than media content.
     */
    public Boolean loop;

    /**
     * Start time, either stream or wall clock
     */
    public Long start;

    /**
     * Milliseconds length of how long the media at URI will override the live
     * content.
     */
    public Long duration;

    /**
     * If immediate is true, flushes interstitial queue, ends any current interstitial, and begins this interstitial immediately.
     */
    public boolean immediate;

    /**
     * Should audio from the interstitial be forwarded? (default: true) Otherwise
     * the base live stream will be used.
     */
    public boolean isInterstitialAudio = true;

    /**
     * Should video from the interstitial be forwarded? (default: true) Otherwise
     * the base live stream will be used.
     */
    public boolean isInterstitialVideo = true;

    @Override
    public String toString() {
        return "InterstitialRequest [id=" + id + ", target=" + target + ", interstitial=" + interstitial + ", type=" + type + ", loop=" + loop + ", start=" + start + ", duration=" + duration + ", isForwardAudio=" + isInterstitialAudio + ", isForwardVideo=" + isInterstitialVideo + "]";
    }
}
