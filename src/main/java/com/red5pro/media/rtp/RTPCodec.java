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

    /** SDP attribute name for packetization time */
    public final static String ATTRIBUTE_PTIME = "ptime";

    /** SDP attribute name for RTP payload mapping */
    public final static String ATTRIBUTE_RTPMAP = "rtpmap";

    /** SDP attribute name for format parameters */
    public final static String ATTRIBUTE_FMTP = "fmtp";

    /** SDP attribute name for application-specific bandwidth */
    public final static String ATTRIBUTE_AS = "AS";

    /**
     * Initializes the encoder side of the codec.
     *
     * @param defaultEncodePacketization
     *            packetization to use when none has been negotiated
     */
    public void encodeInit(int defaultEncodePacketization);

    /**
     * Initializes the decoder side of the codec.
     *
     * @param defaultDecodePacketization
     *            packetization to use when none has been negotiated
     */
    public void decodeInit(int defaultDecodePacketization);

    /**
     * Negotiates the value of an SDP attribute between local and remote sides.
     *
     * @param attributeName
     *            name of the attribute
     * @param localAttributeValue
     *            value offered locally
     * @param remoteAttributeValue
     *            value offered by the remote side
     * @return the negotiated value
     */
    public String codecNegotiateAttribute(String attributeName, String localAttributeValue, String remoteAttributeValue);

    /**
     * Writes a blank (silence) packet for this codec into the buffer.
     *
     * @param buffer
     *            destination buffer
     * @param offset
     *            position in the buffer at which to start writing
     * @return number of bytes written
     */
    public int getCodecBlankPacket(byte[] buffer, int offset);

    /**
     * Returns the size of an incoming encoded frame.
     *
     * @return frame size in bytes
     */
    public int getIncomingEncodedFrameSize();

    /**
     * Returns the size of an incoming frame once decoded.
     *
     * @return frame size in bytes
     */
    public int getIncomingDecodedFrameSize();

    /**
     * Returns the size of an outgoing encoded frame.
     *
     * @return frame size in bytes
     */
    public int getOutgoingEncodedFrameSize();

    /**
     * Returns the size of an outgoing frame before encoding.
     *
     * @return frame size in bytes
     */
    public int getOutgoingDecodedFrameSize();

    /**
     * Returns the sample rate of the codec.
     *
     * @return sample rate in Hz
     */
    public int getSampleRate();

    /**
     * Returns the codec name as used in SDP.
     *
     * @return codec name
     */
    public String getCodecName();

    /**
     * Returns the codec identifier (RTP payload type).
     *
     * @return codec id
     */
    public int getCodecId();

    /**
     * Returns the packetization of incoming media.
     *
     * @return incoming packetization
     */
    public int getIncomingPacketization();

    /**
     * Returns the packetization of outgoing media.
     *
     * @return outgoing packetization
     */
    public int getOutgoingPacketization();

    /**
     * Sets the local packetization time.
     *
     * @param localPtime
     *            local ptime value
     */
    public void setLocalPtime(int localPtime);

    /**
     * Sets the remote packetization time.
     *
     * @param remotePtime
     *            remote ptime value
     */
    public void setRemotePtime(int remotePtime);

    /**
     * Get codec media attributes used for SDP negotiation.
     *
     * @return String array containing codec attribute
     */
    public String[] getCodecMediaAttributes();

}
