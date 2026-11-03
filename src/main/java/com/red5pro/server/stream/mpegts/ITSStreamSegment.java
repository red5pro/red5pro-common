package com.red5pro.server.stream.mpegts;

/**
 * Base interface for MPEG-TS / HLS segments.
 */
public interface ITSStreamSegment {

    /**
     * Returns the segment identifier.
     *
     * @return segment id
     */
    int getId();

}
