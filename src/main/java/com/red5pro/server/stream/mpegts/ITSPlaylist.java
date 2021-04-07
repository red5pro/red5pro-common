package com.red5pro.server.stream.mpegts;

import java.nio.file.Path;

/**
 * Base interface for MPEG-TS / HLS playlist.
 */
public interface ITSPlaylist {

    void newSegment(boolean discontinuity);

    Path writePlaylist(String dirPath, String playList);

    void addSegmentListener(ISegmentListener listener);

    boolean removeSegmentListener(ISegmentListener listener);

    String getCodecs();

    long getIndex();

    int getSegmentLength();

}
