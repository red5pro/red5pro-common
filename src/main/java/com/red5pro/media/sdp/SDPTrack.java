package com.red5pro.media.sdp;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.sdp.model.AttributeField;
import com.red5pro.media.sdp.model.AttributeKey;
import com.red5pro.media.sdp.model.MediaField;
import com.red5pro.media.sdp.model.SDPMediaType;

/**
 * Represents an audio or video track derived from an SDP.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire
 */
public class SDPTrack {

    private final static Logger log = LoggerFactory.getLogger(SDPTrack.class);

    // source sdp
    private final SessionDescription session;

    // media type = audio or video
    private final SDPMediaType mediaType;

    private int payloadId;

    private String control;

    private int mediaChannelId = 0;

    // fmtp parameters
    private Map<String, String> parameters;

    //
    private String interleavedVal;

    public SDPTrack(SessionDescription session, SDPMediaType mediaType, int payloadId) {
        this.session = session;
        this.mediaType = mediaType;
        this.payloadId = payloadId;
        // add the track to the sdp
        session.addTrack(this);
    }

    /**
     * Returns the source SDP.
     * 
     * @return SessionDescription
     */
    public SessionDescription getSession() {
        return session;
    }

    /**
     * Sets the payload id.
     * 
     * @param payloadId
     */
    public void setPayloadId(Integer payloadId) {
        this.payloadId = payloadId;
    }

    /**
     * Returns the associated payload id for this track.
     * 
     * @return payload id
     */
    public int getPayloadId() {
        return payloadId;
    }

    /**
     * Returns whether or not this an audio track.
     * 
     * @return true if audio and false if not
     */
    public boolean isAudio() {
        return SDPMediaType.audio.equals(mediaType);
    }

    /**
     * Returns whether or not this a video track.
     * 
     * @return true if video and false if not
     */
    public boolean isVideo() {
        return SDPMediaType.video.equals(mediaType);
    }

    /**
     * Returns whether or not this an application track.
     * 
     * @return true if application and false if not
     */
    public boolean isApplication() {
        return SDPMediaType.application.equals(mediaType);
    }

    /**
     * Sets the control uri (session level) or channel id (media level).
     * 
     * @param control
     */
    public void setControl(String control) {
        log.debug("setControl: {}", control);
        this.control = control;
        AttributeField controlAttr = null;
        // aggregate control will be represented by an integer, otherwise we'll get an rtsp uri
        if (control.contains("rtsp://")) {
            controlAttr = session.getAttribute(AttributeKey.control);
            if (controlAttr == null) {
                session.addAttributeField(SDPFactory.createAttributeField(AttributeKey.control, control));
            }
        } else {
            MediaField mediaField = session.getMediaDescription(mediaType);
            controlAttr = mediaField.getAttribute(AttributeKey.control);
            if (NumberUtils.isCreatable(control)) {
                int controlChannelId = Integer.valueOf(control);
                if (controlAttr != null) {
                    String currentCtrlStr = controlAttr.getValue();
                    if (currentCtrlStr.contains("=")) {
                        // a=control:trackId=1 (seems that mixed case is possible)
                        int currentControlChannelId = Integer.valueOf(currentCtrlStr.split("=")[1]);
                        // XXX would a clash be a problem??
                        if (controlChannelId != currentControlChannelId) {
                            // do something?
                        }
                    }
                } else {
                    mediaField.addAttributeField(SDPFactory.createAttributeField(AttributeKey.control, String.format("trackID=%d", controlChannelId)));
                }
            } else if (controlAttr == null) {
                mediaField.addAttributeField(SDPFactory.createAttributeField(AttributeKey.control, control));
            }
        }
    }

    /**
     * Returns the control uri for this track if it exists.
     * 
     * @return control uri
     */
    public String getControl() {
        return control;
    }

    /**
     * Returns the control channel id for this track if it exists. Existence means
     * that aggregate control is supported.
     * 
     * @return control channel id
     */
    public int getControlChannelId() {
        if (control != null) {
            // AAC and H264 class look for "streamid"; so hopefully we catch all these variants using the first split item
            // a=control:trackId=1 (seems that mixed case is possible)
            // XXX the RFC uses this form `a=control:trackID=1`
            if (control.contains("=")) {
                return Integer.valueOf(control.split("=")[1]);
            }
        }
        return 0;
    }

    /**
     * Sets the media channel id.
     * 
     * @param mediaChannel
     */
    public void setMediaChannelId(String mediaChannel) {
        log.debug("setMediaChannelId: {}", mediaChannel);
        this.mediaChannelId = Integer.valueOf(mediaChannel);
    }

    /**
     * Sets the media channel id.
     * 
     * @param mediaChannel
     */
    public void setMediaChannelId(int mediaChannel) {
        log.info("setMediaChannelId: {}", mediaChannel);
        this.mediaChannelId = mediaChannel;
    }

    /**
     * Returns the media channel id.
     * 
     * @return media channel id
     */
    public int getMediaChannelId() {
        return mediaChannelId;
    }

    /**
     * Return clock rate as a string from the rtpmap attribute.
     * 
     * <pre>
     * a=rtpmap:97 MPEG4-GENERIC/44100/1
     * a=rtpmap:96 H264/90000
     * </pre>
     * 
     * @return clock rate
     */
    public String getClockRate() {
        String clockRate = isAudio() ? "48000" : "90000"; // default to known media type clock rates
        AttributeField codecAttr = session.getMediaDescription(mediaType).getAttribute(AttributeKey.rtpmap);
        if (codecAttr != null) {
            String[] parts = codecAttr.getValue().split("[\\s|/]");
            if (parts.length >= 2) {
                clockRate = parts[2];
            }
        }
        return clockRate;
    }

    /**
     * Return channel count as a string from the rtpmap attribute.
     * 
     * <pre>
     * a=rtpmap:97 MPEG4-GENERIC/44100/1
     * </pre>
     * 
     * @return channels
     */
    public String getChannels() {
        String channels = isAudio() ? "1" : "0";
        AttributeField codecAttr = session.getMediaDescription(mediaType).getAttribute(AttributeKey.rtpmap);
        if (codecAttr != null) {
            String[] parts = codecAttr.getValue().split("[\\s|/]");
            if (parts.length > 2) {
                channels = parts[3];
            }
        }
        return channels;
    }

    /**
     * Returns the codec / encoding for this track from the rtpmap attribute.
     * 
     * @return encoding
     */
    public String getEncoding() {
        String encoding = isAudio() ? "MPEG4-GENERIC" : "H264"; // default to known codecs for the sdk
        AttributeField codecAttr = session.getMediaDescription(mediaType).getAttribute(AttributeKey.rtpmap);
        if (codecAttr != null) {
            String[] parts = codecAttr.getValue().split("[\\s|/]");
            if (parts.length >= 1) {
                // look for '-' and grab first part if detected
                if (parts[1].contains("-")) {
                    encoding = parts[1].substring(0, parts[1].indexOf('-'));
                } else {
                    encoding = parts[1];
                }
            }
        }
        return encoding;
    }

    /**
     * Sets the rtpmap attribute.
     * 
     * @param encoding
     * @param clockRate
     */
    public void setFormat(String encoding, int clockRate) {
        setFormat(encoding, clockRate, 0);
    }

    /**
     * Sets the rtpmap attribute.
     * 
     * @param encoding
     * @param clockRate
     * @param channels
     */
    public void setFormat(String encoding, int clockRate, int channels) {
        MediaField mediaField = session.getMediaDescription(mediaType);
        AttributeField codecAttr = mediaField.getAttribute(AttributeKey.rtpmap);
        if (codecAttr == null) {
            if (isAudio()) {
                codecAttr = SDPFactory.createAttributeField(AttributeKey.rtpmap, payloadId, encoding, clockRate, channels);
            } else {
                codecAttr = SDPFactory.createAttributeField(AttributeKey.rtpmap, payloadId, encoding, clockRate);
            }
            // add to the media field
            mediaField.addAttributeField(codecAttr);
        }
    }

    /**
     * Adds a fmtp attribute.
     * 
     * @param key
     * @param value
     */
    public void addParameter(String key, String value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
        log.trace("addParameter: {} {}", key, value);
    }

    /**
     * Returns the current parameters map.
     * 
     * @return parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Consolidates any parameters into an fmtp attribute.
     */
    public void finalizeParameters() {
        log.trace("finalizeParameters: {}", parameters);
        if (parameters != null) {
            MediaField mediaField = session.getMediaDescription(mediaType);
            AttributeField fmtAttr = mediaField.getAttribute(AttributeKey.fmtp);
            if (fmtAttr == null) {
                StringBuilder paramsSb = new StringBuilder();
                parameters.forEach((key, value) -> {
                    paramsSb.append(key).append('=').append(value).append("; ");
                });
                fmtAttr = SDPFactory.createAttributeField(AttributeKey.fmtp, payloadId, paramsSb.toString().trim());
                mediaField.addAttributeField(fmtAttr);
                // set control if absent
                if (!mediaField.hasAttribute(AttributeKey.control)) {
                    setControl(mediaType.toString());
                }
            }
        }
    }

    /**
     * Set interleaved value.
     * 
     * @param interleavedVal
     */
    public void setInterleavedVal(String interleavedVal) {
        this.interleavedVal = interleavedVal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
        result = prime * result + payloadId;
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
        SDPTrack other = (SDPTrack) obj;
        if (mediaType != other.mediaType)
            return false;
        if (payloadId != other.payloadId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SDPTrack [mediaType=" + mediaType + ", payloadId=" + payloadId + ", control=" + control + ", mediaChannelId=" + mediaChannelId + ", interleavedVal=" + interleavedVal + ", parameters=" + parameters + "]";
    }

}
