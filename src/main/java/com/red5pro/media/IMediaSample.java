package com.red5pro.media;

import javax.media.Buffer;

/**
 * Common interface for MediaSample implementations.
 *
 * @author Paul Gregoire
 *
 */
public interface IMediaSample {

    /**
     * Returns whether or not the sample is of composite type.
     *
     * @return true if composite and false otherwise
     */
    boolean isComposite();

    /**
     * Returns whether or not this sample contains critical / private data.
     *
     * @return true if critical and false otherwise
     */
    boolean isCritical();

    /**
     * Sets the internal buffer.
     *
     * @param buffer sample payload
     */
    void setBuffer(Object buffer);

    /**
     * Returns the internal buffer.
     *
     * @return buffer
     */
    Object getBuffer();

    /**
     * Returns whether or not the sample has a buffer which has data within it.
     *
     * @return true if the buffer exists and has data
     */
    boolean hasBuffer();

    /**
     * Returns the buffer size.
     *
     * @return size of the buffer
     */
    int getBufferSize();

    /**
     * Sets the decoded state.
     *
     * @param decoded whether the payload is decoded
     */
    void setDecoded(boolean decoded);

    /**
     * Returns the decoded state.
     *
     * @return true if decoded and false if encoded
     */
    boolean isDecoded();

    /**
     * Sets the encoding identifier.
     *
     * @param encoding encoding identifier
     */
    void setEncoding(String encoding);

    /**
     * Returns the encoding identifier.
     *
     * @return encoding
     */
    String getEncoding();

    /**
     * Sets the flags for this sample.
     *
     * @param flags sample flags
     */
    void setFlags(int flags);

    /**
     * Returns the flags.
     *
     * @return flags
     */
    int getFlags();

    /**
     * Returns whether or not this is a key frame.
     *
     * @return true if key frame and false if not
     */
    boolean isKeyframe();

    /** Returns the source name, if available.
     * @return source name, or null if unavailable
     */
    String getSourceName();

    /**
     * Sets the sequence number.
     *
     * @param sequenceNumber sample sequence number
     */
    void setSequenceNumber(long sequenceNumber);

    /**
     * Returns the sequence number.
     *
     * @return sequence number
     */
    long getSequenceNumber();

    /**
     * Sets the start time; in most cases this will only be called if resampled.
     *
     * @param startTime sample timestamp
     */
    void setTimestamp(long startTime);

    /**
     * Returns the start time for the sample.
     *
     * @return start time
     */
    long getTimestamp();

    /**
     * Returns an FMJ/JMF Buffer based on this MediaSample.
     *
     * @return Buffer
     */
    Buffer toBuffer();

    /**
     * Returns an FMJ/JMF Buffer based on this MediaSample.
     *
     * @param stripRtmp
     *            whether or not to strip AMF bytes
     * @return Buffer
     */
    Buffer toBuffer(boolean stripRtmp);

    /**
     * Returns the buffer as a byte array.
     *
     * @return byte[]
     */
    byte[] bufferAsBytes();

    /**
     * Returns the buffer as a short array.
     *
     * @return short[]
     */
    short[] bufferAsShorts();

    /**
     * Returns the track identifier.
     *
     * @return track identifier
     */
    int getTrackNum();

    /** Sets the track identifier.
     *
     * @param id
     *            track id
     *
     */
    void setTrackNum(int id);

    /**
     * Returns the media FourCC when known.
     *
     * @return media FourCC, or zero if unknown
     */
    int getFourCC();

    /**
     * Returns the container format when available.
     *
     * @return container format, or null if unavailable
     */
    FourCC getContainer();

}
