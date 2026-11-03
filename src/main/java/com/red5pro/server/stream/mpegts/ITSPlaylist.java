package com.red5pro.server.stream.mpegts;

import java.nio.file.Path;

/**
 * Base interface for MPEG-TS / HLS playlist.
 */
public interface ITSPlaylist {

    /**
     * Starts a new playlist segment.
     *
     * @param discontinuity
     *            true if the segment follows a discontinuity
     */
    void newSegment(boolean discontinuity);

    /**
     * Writes the playlist to disk.
     *
     * @param dirPath
     *            directory to write into
     * @param playList
     *            playlist file name
     * @return path of the written playlist
     */
    Path writePlaylist(String dirPath, String playList);

    /**
     * Registers a listener for segment events.
     *
     * @param listener
     *            listener to add
     */
    void addSegmentListener(ISegmentListener listener);

    /**
     * Unregisters a segment listener.
     *
     * @param listener
     *            listener to remove
     * @return true if the listener was removed
     */
    boolean removeSegmentListener(ISegmentListener listener);

    /**
     * Returns the codecs string for the playlist.
     *
     * @return codecs descriptor
     */
    String getCodecs();

    /**
     * Returns the index of the current segment.
     *
     * @return segment index
     */
    long getIndex();

    /**
     * Returns the target segment length.
     *
     * @return segment length
     */
    int getSegmentLength();

}
