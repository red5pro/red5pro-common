package com.red5pro.media.rtp;

/**
 * Interface for the codecs
 */
public interface RTPCodec {

	/**
	 * Represents media stream at index 0
	 */
	public final static String MEDIA_0 = "media-0";

	/**
	 * Represents media stream at index 1
	 */
	public final static String MEDIA_1 = "media-1";

	public final static String ATTRIBUTE_PTIME = "ptime";

	public final static String ATTRIBUTE_RTPMAP = "rtpmap";

	public final static String ATTRIBUTE_FMTP = "fmtp";

	public final static String ATTRIBUTE_AS = "AS";

	public void encodeInit(int defaultEncodePacketization);

	public void decodeInit(int defaultDecodePacketization);

	public String codecNegotiateAttribute(String attributeName, String localAttributeValue,
			String remoteAttributeValue);

	public int getCodecBlankPacket(byte[] buffer, int offset);

	public int getIncomingEncodedFrameSize();

	public int getIncomingDecodedFrameSize();

	public int getOutgoingEncodedFrameSize();

	public int getOutgoingDecodedFrameSize();

	public int getSampleRate();

	public String getCodecName();

	public int getCodecId();

	public int getIncomingPacketization();

	public int getOutgoingPacketization();

	public void setLocalPtime(int localPtime);

	public void setRemotePtime(int remotePtime);

	/**
	 * Get codec media attributes used for SDP negotiation.
	 * 
	 * @return String array containing codec attribute
	 */
	public String[] getCodecMediaAttributes();

}
