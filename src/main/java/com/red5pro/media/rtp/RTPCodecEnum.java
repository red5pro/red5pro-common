package com.red5pro.media.rtp;

/**
 * Enum for RTP codecs.
 * 
 * http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xhtml
 * 
 * FEC: http://tools.ietf.org/html/rfc5109
 * 
 * @author Paul Gregoire
 */
public enum RTPCodecEnum {

    // Chrome is using id 107 for h264 pmode 1 and Firefox uses 126

    // audio
    OPUS(111, "opus", "Opus", 48000, 2), PCMU(0, "PCMU", "PCM ulaw", 8000, 1), PCMA(8, "PCMA", "PCM alaw", 8000, 1), SPEEX(97, "speex", "Speex", 16000, 2),
    // video
    H264_PMODE1(126, "H264", "h.264", 90000), H264_PMODE0(97, "H264", "h.264", 90000), VP8(100, "VP8", "VP8", 90000), VP9(101, "VP9", "VP9", 90000), AV1(103, "AV1", "AV1", 90000),
    // other
    RED(116, "red", "Redundant", 90000), ULPFEC(117, "ulpfec", "Generic Forward Error Correction", 90000),
    // special identifiers for something working with any codec or payload type
    ANY_AUDIO(-1, "ANY_AUDIO", "Any audio codec or payload", 0), ANY_VIDEO(-2, "ANY_VIDEO", "Any video codec or payload", 0),
    // no-codec as a means to turn-off audio or video
    NONE(-3, "NONE", "No codec selected", 0);

    public final int payloadType;

    public final String encodingName;

    public final String description;

    public final int clockRate;

    public final int channels;

    RTPCodecEnum(int payloadType, String encodingName, String description, int clockRate) {
        this.payloadType = payloadType;
        this.encodingName = encodingName;
        this.description = description;
        this.clockRate = clockRate;
        this.channels = 0;
    }

    RTPCodecEnum(int payloadType, String encodingName, String description, int clockRate, int channels) {
        this.payloadType = payloadType;
        this.encodingName = encodingName;
        this.description = description;
        this.clockRate = clockRate;
        this.channels = channels;
    }

    /**
     * Returns a formatted rtpmap string for the given codec.
     * 
     * @param codec
     * @return rtpmap string
     */
    public static String getRTPMapString(RTPCodecEnum codec) {
        switch (codec) {
            case ANY_AUDIO:
            case ANY_VIDEO:
            case NONE:
                // no-op
                return null;
            default:
                if (codec.channels == 0) {
                    return String.format("%d %s/%d", codec.payloadType, codec.encodingName, codec.clockRate);
                } else {
                    return String.format("%d %s/%d/%d", codec.payloadType, codec.encodingName, codec.clockRate, codec.channels);
                }
        }
    }

    public static int getPayloadType(RTPCodecEnum codec) {
        return codec.payloadType;
    }

    /**
     * Returns a codec enum based on the given encoding name.
     * 
     * @param encodingName the encoding name or identifier 
     * @return RTPCodecEnum
     */
    public static RTPCodecEnum getByEncodingName(String encodingName) {
        //System.err.printf("getByEncodingName: %s\n", encodingName);
        // specially handle h264 mode 0 or mode 1 (default)
        if ("H264M0".equals(encodingName)) {
            //System.err.println("Mode 0!!");
            return H264_PMODE0;
        } else if ("H264M1".equals(encodingName)) {
            //System.err.println("Mode 1!!");
            return H264_PMODE1;
        }
        for (RTPCodecEnum rce : RTPCodecEnum.values()) {
            if (rce.getEncodingName().equalsIgnoreCase(encodingName)) {
                //System.err.printf("Codec: %s\n", String.valueOf(rce));
                return rce;
            }
        }
        //System.err.println("No codec matched");
        return NONE;
    }

    /**
     * Returns audio payload types in precedence order.
     * 
     * @return array of payload type ids
     */
    public static int[] getAudioPayloadTypesAsArray() {
        int[] types = new int[] { OPUS.payloadType, PCMU.payloadType, PCMA.payloadType, SPEEX.payloadType };
        return types;
    }

    /**
     * Returns video payload types in precedence order.
     * 
     * @return array of payload type ids
     */
    public static int[] getVideoPayloadTypesAsArray() {
        int[] types = new int[] { H264_PMODE1.payloadType, H264_PMODE0.payloadType, VP8.payloadType, VP9.payloadType, AV1.payloadType };
        return types;
    }

    /**
     * Returns audio encoding names in precedence order.
     * 
     * @return array of encoding names
     */
    public static String[] getAudioEncodingNamesAsArray() {
        String[] encodings = new String[] { OPUS.encodingName, PCMU.encodingName, PCMA.encodingName, SPEEX.encodingName };
        return encodings;
    }

    /**
     * Returns video encoding names in precedence order.
     * 
     * @return array of encoding names
     */
    public static String[] getVideoEncodingNamesAsArray() {
        String[] encodings = new String[] { H264_PMODE1.encodingName, H264_PMODE0.encodingName, VP8.encodingName, VP9.encodingName, AV1.encodingName };
        return encodings;
    }

    public int getPayloadType() {
        return payloadType;
    }

    public String getEncodingName() {
        return encodingName;
    }

    public String getDescription() {
        return description;
    }

    public int getClockRate() {
        return clockRate;
    }

    public int getChannels() {
        return channels;
    }

}
