package com.red5pro.server.stream.mpegts;

/**
 * Callback interface for segment events.
 *
 * @author Andy Shaules
 * @author Paul Gregoire
 *
 */
public interface ISegmentListener {

    /**
     * Called when a new segment is started.
     *
     * @param list the playlist the segment belongs to
     */
    void onNewSegment(ITSPlaylist list);

    /**
     * Called when a segment has been completely written.
     *
     * @param segment the completed segment
     */
    void onWriteComplete(ITSStreamSegment segment);

}
