package com.red5pro.server.stream.mpegts;

/**
 * Callback interface for segment events.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire
 *
 */
public interface ISegmentListener {

    void onNewSegment(ITSPlaylist list);

    void onWriteComplete(ITSStreamSegment segment);

}
