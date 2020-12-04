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
	 * @param buffer
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
	 * @param decoded
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
	 * @param encoding
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
	 * @param flags
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

	String getSourceName();

	/**
	 * Sets the sequence number.
	 * 
	 * @param sequenceNumber
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
	 * @param startTime
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
	 * 
	 * @return track id
	 */
	int getTrackNum();
	/**
	 * 
	 * @param id
	 *            track id
	 * 
	 */
	void setTrackNum(int id);

	FourCC getFourCC();
}
