package com.red5pro.media.rtp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.sdp.model.AttributeField;
import com.red5pro.media.sdp.model.AttributeKey;

/**
 * Codec factory.
 * 
 * http://en.wikipedia.org/wiki/RTP_audio_video_profile
 */
public class RTPCodecFactory {

    protected static Logger log = LoggerFactory.getLogger(RTPCodecFactory.class);

    private static RTPCodecFactory instance = new RTPCodecFactory();

    private RTPCodecEnum[] availableAudioCodecs = { RTPCodecEnum.OPUS, RTPCodecEnum.SPEEX, RTPCodecEnum.PCMU };

    private RTPCodecEnum[] availableVideoCodecs = { RTPCodecEnum.H264_PMODE1, RTPCodecEnum.H264_PMODE0, RTPCodecEnum.VP8 };

    private Map<RTPCodecEnum, AttributeField> audioAttributes = new HashMap<>();

    private Map<RTPCodecEnum, AttributeField> videoAttributes = new HashMap<>();

    // h264 packetization mode
    // Modes: 
    // 0 - Single NAL Unit Mode
    // 1 - Non-Interleaved Mode << default for WebRTC
    // 2 - Interleaved Mode
    // All browsers *must support* mode 0 per https://tools.ietf.org/html/rfc6184#section-6.2
    // 
    // Bugs in chromium for ref:
    // https://bugs.chromium.org/p/chromium/issues/detail?id=500605
    // https://bugs.chromium.org/p/chromium/issues/detail?id=600254
    // https://bugs.chromium.org/p/chromium/issues/detail?id=591971
    //
    // a=fmtp:126 profile-level-id=42e01f;level-asymmetry-allowed=1;packetization-mode=1
    // a=fmtp:97 profile-level-id=42e01f;level-asymmetry-allowed=1

    // h264 profile
    private String profile = "42e01f";

    {
        // media attrs for opus
        audioAttributes.put(RTPCodecEnum.OPUS, new AttributeField(AttributeKey.fmtp, String.format("%d minptime=10; useinbandfec=1", RTPCodecEnum.OPUS.payloadType)));
        // -1 is for all audio media
        audioAttributes.put(RTPCodecEnum.ANY_AUDIO, new AttributeField(AttributeKey.maxptime, "60"));

        // media attrs for h264
        // details: https://tools.ietf.org/html/draft-ietf-rtcweb-video-03#section-6.2
        // per spec: WebRTC implementations MUST signal this information in-band; as a result, this parameter (sprop-parameter-sets) will not be present in SDP.
        videoAttributes.put(RTPCodecEnum.H264_PMODE0, new AttributeField(AttributeKey.fmtp, String.format("%d profile-level-id=%s;packetization-mode=0", RTPCodecEnum.H264_PMODE0.payloadType, profile)));
        videoAttributes.put(RTPCodecEnum.H264_PMODE1, new AttributeField(AttributeKey.fmtp, String.format("%d profile-level-id=%s;packetization-mode=1", RTPCodecEnum.H264_PMODE1.payloadType, profile)));
    }

    public static RTPCodecFactory getInstance() {
        return instance;
    }

    /**
     * Create a new instance of RTPCodec by codec name.
     * 
     * @return codec associated with codecName
     */
    public RTPCodec getAudioCodec(String codecName) {
        RTPCodec rtpCodec = null;
        log.debug("getAudioCodec - codecName: {}", codecName);
        for (RTPCodecEnum rc : availableAudioCodecs) {
            String encName = rc.getEncodingName();
            if (encName.equals(codecName)) {
                String deviceClassName = String.format("com.red5pro.webrtc.media.device.%s.%sDevice", encName.toLowerCase(), encName.toUpperCase());
                log.debug("Trying to instance: {}", deviceClassName);
                try {
                    rtpCodec = (RTPCodec) Class.forName(deviceClassName).newInstance();
                    break;
                } catch (Exception e) {
                    log.warn("getAudioCodec", e);
                }
            }
        }
        if (rtpCodec != null) {
            log.debug("getAudioCodec - codecId:{} codecName: {}", rtpCodec.getCodecId(), rtpCodec.getCodecName());
        }
        return rtpCodec;
    }

    /**
     * Create a new instance of RTPCodec by codec name.
     * 
     * @return codec associated with codecName
     */
    public RTPCodec getVideoCodec(String codecName) {
        RTPCodec rtpCodec = null;
        log.debug("getVideoCodec - codecName: {}", codecName);
        for (RTPCodecEnum rc : availableVideoCodecs) {
            String encName = rc.getEncodingName();
            if (encName.equals(codecName)) {
                String deviceClassName = String.format("com.red5pro.webrtc.media.device.%s.%sDevice", encName.toLowerCase(), encName.toUpperCase());
                log.debug("Trying to instance: {}", deviceClassName);
                try {
                    rtpCodec = (RTPCodec) Class.forName(deviceClassName).newInstance();
                    break;
                } catch (Exception e) {
                    log.warn("getVideoCodec", e);
                }
            }
        }
        if (rtpCodec != null) {
            log.debug("getVideoCodec - codecId:{} codecName: {}", rtpCodec.getCodecId(), rtpCodec.getCodecName());
        }
        return rtpCodec;
    }

    /**
     * Get all available audio codecs.
     * 
     * @return RTPCodecEnum array containing all audio codecs
     */
    public RTPCodecEnum[] getAvailableAudioCodecs() {
        return availableAudioCodecs;
    }

    /**
     * Get all available video codecs.
     * 
     * @return RTPCodecEnum array containing all video codecs
     */
    public RTPCodecEnum[] getAvailableVideoCodecs() {
        return availableVideoCodecs;
    }

    /**
     * Get all available codecs in precedence order.
     * 
     * @param codecsPrecedence
     *            semicolon separated payload type ids from the codecs
     * @return RTPCodecEnum array containing all codecs
     */
    public RTPCodecEnum[] getAvailableAudioCodecsWithPrecedence(String codecsPrecedence) {
        int initIndex = 0;
        int finalIndex = codecsPrecedence.indexOf(";");
        String codecId;
        RTPCodecEnum[] availableCodecs = new RTPCodecEnum[availableAudioCodecs.length];
        int codecsIndex = 0;
        log.debug("getAvailableAudioCodecsWithPrecedence - codecsPrecedence: {} finalIndex: {}", codecsPrecedence, finalIndex);
        while (initIndex < finalIndex) {
            codecId = codecsPrecedence.substring(initIndex, finalIndex);
            log.debug("getAvailableAudioCodecsWithPrecedence", "codecId = " + codecId);
            for (RTPCodecEnum rc : availableAudioCodecs) {
                if (rc.payloadType == Integer.valueOf(codecId)) {
                    log.debug("getAvailableAudioCodecsWithPrecedence - codecId: {} codecName: {}", rc.payloadType, rc.encodingName);
                    availableCodecs[codecsIndex] = rc;
                    codecsIndex++;
                    break;
                }
            }
            initIndex = finalIndex + 1;
            finalIndex = codecsPrecedence.indexOf(";", initIndex);
            if (finalIndex == -1 && initIndex <= codecsPrecedence.length()) {
                finalIndex = codecsPrecedence.length();
            }
            log.debug("getAvailableAudioCodecsWithPrecedence - codecsIndex: {} initIndex: {} finalIndex: {}", codecsIndex, initIndex, finalIndex);
        }
        return availableCodecs;
    }

    /**
     * @return Count of available audio codecs
     */
    public int getAvailableAudioCodecsCount() {
        return availableAudioCodecs.length;
    }

    /**
     * @return Count of available video codecs
     */
    public int getAvailableVideoCodecsCount() {
        return availableVideoCodecs.length;
    }

    public Map<RTPCodecEnum, AttributeField> getAudioAttributes() {
        return audioAttributes;
    }

    public void setAudioAttributes(Map<RTPCodecEnum, AttributeField> audioAttributes) {
        this.audioAttributes = audioAttributes;
    }

    public Map<RTPCodecEnum, AttributeField> getVideoAttributes() {
        return videoAttributes;
    }

    public void setVideoAttributes(Map<RTPCodecEnum, AttributeField> videoAttributes) {
        this.videoAttributes = videoAttributes;
    }

}
