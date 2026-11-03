package com.red5pro.media.sdp;

/**
 * All the useful User-Agents making use of SDP structures.
 *
 * @author Paul Gregoire
 */
public enum SDPUserAgent {

    /** User agent is not known. */
    undefined,
    /** Google Chrome. */
    chrome,
    /** Mozilla Firefox. */
    firefox,
    /** Generic Mozilla-based agent. */
    mozilla,
    /** Microsoft Edge. */
    edge,
    /** Opera. */
    opera,
    /** Apple Safari. */
    safari,
    /** Chrome on Android. */
    android_chrome,
    /** Firefox on Android. */
    android_firefox,
    /** Red5 Pro client. */
    red5pro,
    /** OBS Studio. */
    obs,
    /** Native application. */
    app,
    /** FFmpeg. */
    ffmpeg,
    /** GStreamer. */
    gstreamer;

}
