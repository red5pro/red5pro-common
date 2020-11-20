package com.red5pro.media;

import java.util.Arrays;

import javax.media.Buffer;

public class MediaSample implements IMediaSample {

	// type of media, audio, video etc
	protected MediaType type;

	// Critical codec information flag
	protected boolean privateData;

	// Pro sequence number
	private long sequenceNumber;

	private long timestamp;

	private long timebase;

	private long epoc;

	private String sourceName;

	// Buffered content can be modified
	private Object buffer;

	private int flags;

	// whether or not the buffer has been decoded
	private boolean decoded;

	// encoding identifier
	private String encoding;

	// fourCC code for the content
	private FourCC fourCC;

	private int trackNum;

	protected MediaSample() {

	}
	/**
	 * Creation with start time, buffer size, and media type.
	 * 
	 * @param startTime
	 * @param bufferSize
	 * @param type
	 */
	private MediaSample(long timestamp, int bufferSize, MediaType type) {
		this.timestamp = timestamp;
		this.buffer = new byte[bufferSize];
		this.type = type;
		this.privateData = false;
	}

	/**
	 * Creation with start time, buffer, and media type.
	 * 
	 * @param startTime
	 * @param buffer
	 * @param type
	 */
	private MediaSample(long timestamp, byte[] buffer, MediaType type) {
		this.timestamp = timestamp;
		this.buffer = buffer;
		this.type = type;
		this.privateData = false;
	}

	/**
	 * Creation with start time, buffer, media type, and critical data flag.
	 * 
	 * @param startTime
	 * @param buffer
	 * @param type
	 * @param critical
	 */
	private MediaSample(long timestamp, byte[] buffer, MediaType type, boolean critical) {
		this.timestamp = timestamp;
		this.buffer = buffer;
		this.type = type;
		this.privateData = critical;
	}

	/**
	 * Creation with start time, buffer, media type, critical data flag, and pro
	 * sequence number.
	 * 
	 * @param startTime
	 * @param buffer
	 * @param type
	 * @param critical
	 * @param sequenceNumber
	 */
	private MediaSample(long timestamp, byte[] buffer, MediaType type, boolean critical, long sequenceNumber) {
		this.timestamp = timestamp;
		this.buffer = buffer;
		this.type = type;
		this.privateData = critical;
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * Creation with start time, buffer, media type, critical data flag, and pro
	 * sequence number.
	 * 
	 * @param startTime
	 * @param buffer
	 * @param type
	 * @param sourceName
	 * @param critical
	 * @param sequenceNumber
	 * @param decoded
	 */
	private MediaSample(long timestamp, byte[] buffer, MediaType type, String sourceName, boolean critical,
			long sequenceNumber, boolean decoded) {
		this.timestamp = timestamp;
		this.buffer = buffer;
		this.type = type;
		this.sourceName = sourceName;
		this.privateData = critical;
		this.sequenceNumber = sequenceNumber;
		this.decoded = decoded;
	}

	public boolean isCritical() {
		return privateData;
	}

	public byte[] bufferAsBytes() {
		if (buffer instanceof short[]) {
			short[] buf = (short[]) buffer;
			byte[] array = new byte[buf.length * 2];
			for (int i = 0, o = 0; i < buf.length; i++) {
				short s = buf[i];
				array[o++] = (byte) (s & 0x00ff);
				array[o++] = (byte) ((s & 0xff00) >>> 8);
			}
			return array;
		}
		return (byte[]) buffer;
	}

	public short[] bufferAsShorts() {
		if (buffer instanceof short[]) {
			return (short[]) buffer;
		}
		byte[] buf = (byte[]) buffer;
		short[] array = new short[buf.length / 2];
		for (int i = 0, o = 0; o < array.length; o++) {
			array[o] = (short) (((buf[i++] & 0xff) | (buf[i++] & 0xff) << 8));
		}
		return array;
	}

	public int getBufferSize() {
		return bufferAsBytes().length;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Object getBuffer() {
		return buffer;
	}

	public void setBuffer(Object buffer) {
		this.buffer = null;
		this.buffer = buffer;
	}

	public boolean hasBuffer() {
		return buffer != null && getBufferSize() > 0;
	}

	public MediaType getType() {
		return type;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public boolean isAudio() {
		return type == MediaType.AUDIO;
	}

	public boolean isVideo() {
		return type == MediaType.VIDEO;
	}

	public void setKey() {
		flags |= Buffer.FLAG_KEY_FRAME;
	}

	public boolean isKeyframe() {
		return (type == MediaType.VIDEO && ((flags & Buffer.FLAG_KEY_FRAME) != 0));
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public boolean isDecoded() {
		return decoded;
	}

	public void setDecoded(boolean decoded) {
		this.decoded = decoded;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public FourCC getFourCC() {
		return fourCC;
	}

	public void setFourCC(FourCC fourCC) {
		this.fourCC = fourCC;
	}

	public void setFourCC(char a, char b, char c, char d) {
		this.fourCC = FourCC.getFourCC(a, b, c, d);;
	}

	public long getTimebase() {
		return timebase;
	}

	public void setTimebase(long timebase) {
		this.timebase = timebase;
	}

	public long getEpoc() {
		return epoc;
	}

	public void setEpoc(long epoc) {
		this.epoc = epoc;
	}

	@Override
	public boolean isComposite() {
		return false;
	}

	/**
	 * Returns an FMJ/JMF Buffer based on this MediaSample.
	 * 
	 * @deprecated
	 * @param stripRtmp
	 * @return
	 */
	public Buffer toBuffer(boolean stripRtmp) {
		Buffer buf = new Buffer();
		buf.setFlags(flags);
		if (privateData) {
			buf.setConfig();
		}
		buf.setTimeStamp(timestamp);
		buf.setMilliseconds(timestamp);
		if (isVideo()) {
			buf.setRtpTimeStamp(timestamp * 90);
		} else {
			buf.setRtpTimeStamp(timestamp * 48);
		}
		if (stripRtmp) {
			byte[] data = bufferAsBytes();
			if (isAudio()) {
				// strip the prefix byte
				buf.setData(Arrays.copyOfRange(data, 1, data.length));
				buf.setLength(data.length - 1);
			} else {
				// strip the prefix and avc type
				buf.setData(Arrays.copyOfRange(data, 2, data.length));
				buf.setLength(data.length - 2);
			}
		} else {
			buf.setData(buffer);
			buf.setLength(getBufferSize());
		}
		buf.setOffset(0);
		return buf;
	}

	/**
	 * Returns a deep-copy of this MediaSample.
	 * 
	 * @return copy
	 */
	public MediaSample deepCopy() {
		int length = getBufferSize();
		byte[] copy = new byte[length];
		System.arraycopy(bufferAsBytes(), 0, copy, 0, length);
		MediaSample ms = MediaSample.build(timestamp, copy, type, sourceName, isCritical(), getSequenceNumber(),
				decoded);
		ms.setEncoding(encoding);
		ms.setFlags(flags);
		return ms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		result = prime * result + ((encoding == null) ? 0 : encoding.hashCode());
		result = prime * result + ((fourCC == null) ? 0 : fourCC.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MediaSample other = (MediaSample) obj;
		if (type != other.type)
			return false;
		if (encoding != null && !encoding.equals(other.encoding))
			return false;
		if (fourCC != other.fourCC)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MediaSample [type=" + (isAudio() ? "audio" : "video") + ", sourceName=" + sourceName + ", encoding="
				+ encoding + ", privateData=" + privateData + ", keyframe=" + isKeyframe() + ", startTime=" + timestamp
				+ ",  sequenceNumber=" + sequenceNumber + ", decoded=" + decoded + ", buffer=" + buffer + "]";
	}

	public static MediaSample build(long startTime, byte[] buf, MediaType type) {
		return new MediaSample(startTime, buf, type);
	}

	public static MediaSample build(long startTime, byte[] buf, MediaType type, boolean critical) {
		return new MediaSample(startTime, buf, type, critical);
	}

	public static MediaSample build(long startTime, byte[] buf, MediaType type, boolean critical, long sequenceNumber) {
		return new MediaSample(startTime, buf, type, critical, sequenceNumber);
	}

	public static MediaSample build(long startTime, byte[] buf, MediaType type, String sourceName, boolean critical,
			long sequenceNumber, boolean decoded) {
		return new MediaSample(startTime, buf, type, sourceName, critical, sequenceNumber, decoded);
	}

	@Override
	public int getTrackNum() {
		// TODO Auto-generated method stub
		return trackNum;
	}

	@Override
	public void setTrackNum(int id) {
		trackNum = id;

	}

}
