package com.red5pro.media;

import org.red5.server.api.event.IEvent;

/**
 * A listener interested in MediaTrack events.
 *
 * @author Paul Gregoire
 *
 */
public class MediaTrackListener {

    /**
     * Event has emitted from the given track.
     *
     * @param track
     *            event emitter
     * @param event
     *            the event
     */
    public void onEvent(MediaTrack track, IEvent event) {
        // implementations should handle their incoming events here
    }

}
