package com.red5pro.media.sdp;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.VideoConstants;
import com.red5pro.media.rtp.RTPCodecEnum;
import com.red5pro.media.rtp.RTPCodecFactory;
import com.red5pro.media.sdp.model.AttributeField;
import com.red5pro.media.sdp.model.AttributeKey;
import com.red5pro.media.sdp.model.BandwidthField;
import com.red5pro.media.sdp.model.ConnectionField;
import com.red5pro.media.sdp.model.MediaField;
import com.red5pro.media.sdp.model.OriginField;
import com.red5pro.media.sdp.model.SDPMediaType;
import com.red5pro.media.sdp.model.SessionField;
import com.red5pro.server.util.NetworkManager;
import com.red5pro.util.IdGenerator;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Session description protocol factory which provides encoders, decoders, and
 * utility functions for working with SDP content.
 * 
 * @author Paul Gregoire
 */
public final class SDPFactory {

    private static Logger log = LoggerFactory.getLogger(SDPFactory.class);

    private static boolean red;

    private static boolean ulpfec;

    // Full Intra Request (to indicate key frame request)
    private static boolean fir;

    // Negative ACKnowlegement
    private static boolean nack;

    // Picture Loss Indication
    private static boolean pli;

    // Bandwidth
    private static boolean googRemb = true;

    private static boolean transportCC;

    /**
     * Creates an SDP decoder that handles one line at a time.
     * 
     * @return SDPSingleLineDecoder
     */
    public static SDPSingleLineDecoder createSessionDescriptionDecoder() {
        return new SDPSingleLineDecoder();
    }

    /**
     * Instance an empty SessionDescription.
     * 
     * @return SessionDescription
     */
    public static SessionDescription createSessionDescription() {
        return new SessionDescription();
    }

    /**
     * Instance an empty SessionDescription with a given User-Agent.
     * 
     * @return SessionDescription
     */
    public static SessionDescription createSessionDescription(SDPUserAgent userAgent) {
        return new SessionDescription(userAgent);
    }

    /**
     * Instance an empty SessionDescription with a given User-Agent.
     * 
     * @param userAgent
     * @param rawSdp
     * @return SessionDescription
     */
    public static SessionDescription createSessionDescription(SDPUserAgent userAgent, String rawSdp) {
        if (log.isDebugEnabled()) {
            log.debug("Create sdp from: {} ua: {}", rawSdp, userAgent);
        }
        SessionDescription sdp = new SessionDescription(userAgent);
        parse(sdp, rawSdp);
        return sdp;
    }

    /**
     * Instance a SessionDescription from raw SDP content.
     * 
     * @param rawSdp
     * @return SessionDescription
     */
    public static SessionDescription createSessionDescription(String rawSdp) {
        if (log.isDebugEnabled()) {
            log.debug("Create sdp from: {}", rawSdp);
        }
        SessionDescription sdp = new SessionDescription();
        parse(sdp, rawSdp);
        return sdp;
    }

    /**
     * Create a SessionDescription given provided information.
     * 
     * @param userName
     * @param address
     * @return SessionDescription
     */
    public static SessionDescription createSessionDescription(String userName, String address) {
        if (log.isDebugEnabled()) {
            log.debug("Create sdp - userName: {} address: {}", new Object[] { userName, address });
        }
        SessionDescription sdp = new SessionDescription();
        // create / set media stream id
        sdp.setMsid(Long.toHexString(IdGenerator.generateId()));
        // set the origin
        OriginField origin = new OriginField(userName, IdGenerator.generateNumericStringId(13), 1L, address);
        sdp.setOrigin(origin);
        return sdp;
    }

    /**
     * Create a SessionDescription given provided information. To exclude audio or
     * video media, pass -1 for the respective port.
     * 
     * @param userName
     * @param address
     * @param audioPort
     * @param audioRTCPPort
     * @param videoPort
     * @param videoRTCPPort
     * @return SessionDescription
     */
    public static SessionDescription createSessionDescription(String userName, String address, int audioPort, int audioRTCPPort, int videoPort, int videoRTCPPort) {
        if (log.isDebugEnabled()) {
            log.debug("Create sdp - userName: {} address: {} audio port: {} video port: {}", new Object[] { userName, address, audioPort, videoPort });
        }
        SessionDescription sdp = new SessionDescription();
        // set the origin
        OriginField origin = new OriginField(userName, IdGenerator.generateNumericStringId(13), 1L, address);
        sdp.setOrigin(origin);
        // create a connection
        ConnectionField connection = new ConnectionField(address);
        // get codec factory instance
        RTPCodecFactory rtpCodecFactory = RTPCodecFactory.getInstance();
        // set audio media
        if (audioPort != -1) {
            MediaField media = new MediaField(SDPMediaType.audio, audioPort, 1);
            media.setConnection(connection);
            if (audioRTCPPort == -1) {
                media.addAttributeField(new AttributeField(AttributeKey.rtcp, String.format("%d %s %s %s", audioPort, connection.getNetworkType(), connection.getAddressType(), address)));
                media.addAttributeField(new AttributeField(AttributeKey.rtcpmux, null));
                sdp.setRtcpMux(true);
            } else {
                media.addAttributeField(new AttributeField(AttributeKey.rtcp, String.format("%d %s %s %s", audioRTCPPort, connection.getNetworkType(), connection.getAddressType(), address)));
            }
            // add rtpmap
            RTPCodecEnum[] codecs = rtpCodecFactory.getAvailableAudioCodecs();
            // add codec attributes
            Map<RTPCodecEnum, AttributeField> audioAttributes = rtpCodecFactory.getAudioAttributes();
            for (RTPCodecEnum codec : codecs) {
                media.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(codec)));
                // get attribute(s) for the current codec
                AttributeField attr = audioAttributes.get(codec);
                if (attr != null) {
                    media.addAttributeField(attr);
                }
            }
            if (!audioAttributes.isEmpty()) {
                // get attributes for ANY codec
                AttributeField attr = audioAttributes.get(RTPCodecEnum.ANY_AUDIO);
                if (attr != null) {
                    media.addAttributeField(attr);
                }
            }
            // add media to sdp
            sdp.addMediaDescription(media);
        }
        // set video media
        if (videoPort != -1) {
            MediaField media = new MediaField(SDPMediaType.video, videoPort, 1);
            media.setConnection(connection);
            if (videoRTCPPort == -1) {
                media.addAttributeField(new AttributeField(AttributeKey.rtcp, String.format("%d %s %s %s", videoPort, connection.getNetworkType(), connection.getAddressType(), address)));
                media.addAttributeField(new AttributeField(AttributeKey.rtcpmux, null));
                sdp.setRtcpMux(true);
            } else {
                media.addAttributeField(new AttributeField(AttributeKey.rtcp, String.format("%d %s %s %s", videoRTCPPort, connection.getNetworkType(), connection.getAddressType(), address)));
            }
            // add rtpmap
            RTPCodecEnum[] codecs = rtpCodecFactory.getAvailableVideoCodecs();
            // add codec attributes
            Map<RTPCodecEnum, AttributeField> videoAttributes = rtpCodecFactory.getVideoAttributes();
            for (RTPCodecEnum codec : codecs) {
                media.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(codec)));
                // get attribute(s) for the current codec
                AttributeField attr = videoAttributes.get(codec);
                if (attr != null) {
                    media.addAttributeField(attr);
                }
                /*
                 * Where rtcp-fb-pt is the payload type and rtcp-fb-val defines the type of the
                 * feedback message such as ack, nack, trr-int, and rtcp-fb-id. For example, to
                 * indicate the support of feedback of Picture Loss Indication, the sender
                 * declares the following in SDP a=rtcp-fb:100 ccm fir a=rtcp-fb:100 nack
                 * a=rtcp-fb:100 nack pli a=rtcp-fb:100 goog-remb In this document, we define a
                 * new feedback value "ccm", which indicates the support of codec control using
                 * RTCP feedback messages. The "ccm" feedback value SHOULD be used with
                 * parameters that indicate the specific codec control commands supported. In
                 * this document, we define four such parameters, namely: "fir" indicates
                 * support of the Full Intra Request (FIR). "tmmbr" indicates support of the
                 * Temporary Maximum Media Stream Bit Rate Request/Notification (TMMBR/TMMBN).
                 * It has an optional sub-parameter to indicate the session maximum packet rate
                 * (measured in packets per second) to be used. If not included, this defaults
                 * to infinity. "tstr" indicates support of the Temporal-Spatial Trade-off
                 * Request/Notification (TSTR/TSTN). "vbcm" indicates support of H.271 Video
                 * Back Channel Messages (VBCMs). It has zero or more subparameters identifying
                 * the supported H.271 "payloadType" values.
                 */
                if (fir) {
                    media.addAttributeField(new AttributeField(AttributeKey.rtcpfb, String.format("%d ccm fir", codec.payloadType)));
                }
                // TODO have to fix negotiate on the feedback records below
                if (nack) {
                    media.addAttributeField(new AttributeField(AttributeKey.rtcpfb, String.format("%d nack", codec.payloadType)));
                }
                if (pli) {
                    media.addAttributeField(new AttributeField(AttributeKey.rtcpfb, String.format("%d nack pli", codec.payloadType)));
                }
                if (googRemb) {
                    media.addAttributeField(new AttributeField(AttributeKey.rtcpfb, String.format("%d goog-remb", codec.payloadType)));
                }
                if (transportCC) {
                    media.addAttributeField(new AttributeField(AttributeKey.rtcpfb, String.format("%d transport-cc", codec.payloadType)));
                }
            }
            if (!videoAttributes.isEmpty()) {
                // get attributes for ANY codec
                AttributeField attr = videoAttributes.get(RTPCodecEnum.ANY_VIDEO);
                if (attr != null) {
                    media.addAttributeField(attr);
                }
            }
            // additional options
            if (red) {
                media.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(RTPCodecEnum.RED)));
            }
            if (ulpfec) {
                media.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(RTPCodecEnum.ULPFEC)));
            }
            // add media to sdp
            sdp.addMediaDescription(media);
        }
        return sdp;
    }

    /**
     * Create offer sdp from ORTC sender capabilities parameters.
     * 
     * @param senderCaps
     * @return sdp
     */
    public static SessionDescription senderCapabilitiesToSdp(JSONObject senderCaps) {
        // origin address 0.0.0.0 will be used since none are sent
        String address = "0.0.0.0";
        // create a mostly blank sdp
        SessionDescription sdp = createSessionDescription("edge-ortc", address);
        // add bundle etc
        sdp.setBundle(true);
        // create a connection
        ConnectionField connection = new ConnectionField(address);
        // configure ice section
        // ice:{usernameFragment:"bfpQ",password:"24nW3bmt81X27fDLdboevA62",iceLite:false}
        JSONObject ice = (JSONObject) senderCaps.get("ice");
        AttributeField ufrag = new AttributeField(AttributeKey.iceufrag, ice.getAsString("usernameFragment"));
        AttributeField pass = new AttributeField(AttributeKey.icepwd, ice.getAsString("password"));
        AttributeField trickle = new AttributeField(AttributeKey.iceoptions, "trickle");
        // if we get dtls role "auto" the server role should be returned as 'server' ie.
        // passive
        // dtls:{role:"auto",fingerprints:[{algorithm:"sha-256",value:"3F:91:9F:79:09:D8:51:15:B8:64:95:C6:5E:18:7F:74:76:47:4E:FC:D4:D8:F1:B3:67:B5:65:3B:07:9E:76:C9"}]
        JSONObject dtls = (JSONObject) senderCaps.get("dtls");
        JSONArray fingerprints = (JSONArray) dtls.get("fingerprints");
        AttributeField finger = new AttributeField(AttributeKey.fingerprint, ((JSONObject) fingerprints.get(0)).getAsString("algorithm") + ' ' + ((JSONObject) fingerprints.get(0)).getAsString("value"));
        String role = dtls.getAsString("role");
        AttributeField setup = null;
        // auto = actpass, server = passive, client = active
        if ("auto".equals(role)) {
            setup = new AttributeField(AttributeKey.setup, "actpass");
        } else if ("server".equals(role)) {
            setup = new AttributeField(AttributeKey.setup, "passive");
        } else if ("client".equals(role)) {
            setup = new AttributeField(AttributeKey.setup, "active");
        }
        // for audio we'll only keep opus
        JSONObject sendAudioCaps = (JSONObject) senderCaps.get("sendAudioCaps");
        if (sendAudioCaps != null) {
            int audioPort = 9;
            MediaField media = new MediaField(SDPMediaType.audio, audioPort, 1);
            media.setConnection(connection);
            media.addAttributeField(ufrag);
            media.addAttributeField(pass);
            media.addAttributeField(trickle);
            media.addAttributeField(finger);
            media.addAttributeField(setup);
            media.addAttributeField(new AttributeField(AttributeKey.mid, "audio"));
            media.addAttributeField(new AttributeField(AttributeKey.rtcp, String.format("%d %s %s %s", audioPort, connection.getNetworkType(), connection.getAddressType(), address)));
            media.addAttributeField(new AttributeField(AttributeKey.rtcpmux, null));
            // look for opus
            JSONArray acodecs = (JSONArray) sendAudioCaps.get("codecs");
            for (int i = 0; i < acodecs.size(); i++) {
                JSONObject acodec = (JSONObject) acodecs.get(i);
                if ("opus".equals(acodec.getAsString("name"))) {
                    // {name:"opus",kind:"audio",clockRate:48000,preferredPayloadType:102,maxptime:60,ptime:20,numChannels:2,
                    // rtcpFeedback:[{type:"x-cinfo",parameter:""},{type:"x-bwe",parameter:""},{type:"x-message",parameter:"app
                    // send:dsh recv:dsh"}],
                    // parameters:{},options:{},maxTemporalLayers:0,maxSpatialLayers:0,svcMultiStreamSupport:false}
                    int payloadType = (int) acodec.getAsNumber("preferredPayloadType");
                    // set the codec format
                    media.setFormats(new int[] { payloadType });
                    String rtpMapStr = String.format("%d opus/%d/%d", payloadType, (int) acodec.getAsNumber("clockRate"), (int) acodec.getAsNumber("numChannels"));
                    media.addAttributeField(new AttributeField(AttributeKey.rtpmap, rtpMapStr));
                    // set media attrs for opus
                    media.addAttributeField(new AttributeField(AttributeKey.fmtp, String.format("%d minptime=%d; useinbandfec=1", payloadType, (int) acodec.getAsNumber("ptime"))));
                    media.addAttributeField(new AttributeField(AttributeKey.maxptime, acodec.getAsString("maxptime")));
                    break;
                }
            }
            // add media to sdp
            sdp.addMediaDescription(media);
        }
        // for video we'll only keep the H264 type
        JSONObject sendVideoCaps = (JSONObject) senderCaps.get("sendVideoCaps");
        if (sendVideoCaps != null) {
            int videoPort = 9;
            MediaField media = new MediaField(SDPMediaType.video, videoPort, 1);
            media.setConnection(connection);
            media.addAttributeField(ufrag);
            media.addAttributeField(pass);
            media.addAttributeField(trickle);
            media.addAttributeField(finger);
            media.addAttributeField(setup);
            media.addAttributeField(new AttributeField(AttributeKey.mid, "video"));
            media.addAttributeField(new AttributeField(AttributeKey.rtcp, String.format("%d %s %s %s", videoPort, connection.getNetworkType(), connection.getAddressType(), address)));
            media.addAttributeField(new AttributeField(AttributeKey.rtcpmux, null));
            // look for h264
            JSONArray vcodecs = (JSONArray) sendVideoCaps.get("codecs");
            for (int i = 0; i < vcodecs.size(); i++) {
                JSONObject vcodec = (JSONObject) vcodecs.get(i);
                if ("H264".equals(vcodec.getAsString("name"))) {
                    // {name:"H264",kind:"video",clockRate:90000,preferredPayloadType:107,maxptime:0,ptime:0,numChannels:1,
                    // rtcpFeedback:[{type:"x-cinfo",parameter:""},{type:"x-bwe",parameter:""},{type:"nack",parameter:""},{type:"nack",parameter:"pli"},
                    // {type:"goog-remb",parameter:""}],
                    // parameters:{profile-level-id:"42C02A",packetization-mode:"1"},options:{},maxTemporalLayers:3,maxSpatialLayers:0,svcMultiStreamSupport:false}
                    int payloadType = (int) vcodec.getAsNumber("preferredPayloadType");
                    // set the codec format
                    media.setFormats(new int[] { payloadType });
                    String rtpMapStr = String.format("%d H264/%d", payloadType, (int) vcodec.getAsNumber("clockRate"));
                    media.addAttributeField(new AttributeField(AttributeKey.rtpmap, rtpMapStr));
                    // set media attrs for
                    String profile = ((JSONObject) vcodec.get("parameters")).getAsString("profile-level-id");
                    media.addAttributeField(new AttributeField(AttributeKey.fmtp, String.format("%d profile-level-id=%s;packetization-mode=1", payloadType, profile)));
                    media.addAttributeField(new AttributeField(AttributeKey.rtcpfb, payloadType + " nack pli"));
                    media.addAttributeField(new AttributeField(AttributeKey.rtcpfb, payloadType + " goog-remb"));
                }
            }
            // add media to sdp
            sdp.addMediaDescription(media);
        }
        return sdp;
    }

    /**
     * Create ORTC receiver capabilities parameters from an answer sdp.
     * 
     * @param sdp
     * @return receiver capabilities
     */
    public static String receiverCapabilitiesFromSdp(SessionDescription sdp) {
        // log.debug("Incoming sdp: {}", sdp.toString(), sdp.getMediaDescriptions().length);
        MediaField media = sdp.getMediaDescriptions()[0];
        // ice
        String ufrag = media.getAttribute(AttributeKey.iceufrag).getValue();
        String passwd = media.getAttribute(AttributeKey.icepwd).getValue();
        // dtls
        String[] algoFinger = media.getAttribute(AttributeKey.fingerprint).getValue().split(" ");
        String fingerprint = algoFinger[1];
        String role = media.getAttribute(AttributeKey.setup).getValue();
        if ("active".equals(role)) {
            role = "client";
        } else if ("passive".equals(role)) {
            role = "server";
        } else if ("actpass".equals(role)) {
            role = "auto";
        }
        // audio
        int audioPayloadType = media.getFormats()[0];
        // video
        media = sdp.getMediaDescriptions()[1];
        int videoPayloadType = media.getFormats()[0];
        String profile = null;
        String[] fmtps = media.getAttribute(AttributeKey.fmtp).getValue().split(";");
        // log.warn("FMTPS {}", Arrays.toString(fmtps));
        for (String fmtp : fmtps) {
            if (fmtp.contains("profile")) {
                // log.warn("FMTPS split {}", Arrays.toString(fmtp.split("=")));
                profile = fmtp.split("=")[1];
                break;
            }
        }
        String recvCaps = String.format("{\"ice\":{\"usernameFragment\":\"%s\",\"password\":\"%s\"},\"dtls\":{\"role\":\"%s\",\"fingerprints\":[{\"algorithm\":\"sha-256\",\"value\":\"%s\"}]},"
                + "\"ansAudioCaps\":{\"muxId\":\"audio\",\"encodings\":[{\"active\":true,\"ssrc\":1001,\"codecPayloadType\":%d,\"fec\":0,\"rtx\":0,\"priority\":1.0,\"maxBitrate\":64000,\"minQuality\":0,\"dependencyEncodingId\":\"undefined\",\"encodingId\":\"undefined\"}],\"rtcp\":{\"cname\":\"\",\"reducedSize\":false,\"ssrc\":0,\"mux\":true},"
                + "\"codecs\":[{\"name\":\"opus\",\"kind\":\"audio\",\"clockRate\":48000,\"payloadType\":102,\"preferredPayloadType\":102,\"maxptime\":60,\"ptime\":20,\"numChannels\":2,\"rtcpFeedback\":[],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false}],\"headerExtensions\":[],\"fecMechanisms\":[]},"
                + "\"ansVideoCaps\":{\"muxId\":\"video\",\"encodings\":[{\"active\":true,\"ssrc\":1002,\"codecPayloadType\":%d,\"fec\":0,\"rtx\":0,\"priority\":1.0,\"maxBitrate\":750000,\"minQuality\":0,\"framerateBias\":0.5,\"resolutionScale\":1.0,\"framerateScale\":1.0,\"dependencyEncodingId\":\"undefined\",\"encodingId\":\"undefined\"}],\"rtcp\":{\"cname\":\"\",\"reducedSize\":false,\"ssrc\":0,\"mux\":true},"
                + "\"codecs\":[{\"name\":\"H264\",\"kind\":\"video\",\"clockRate\":90000,\"payloadType\":107,\"preferredPayloadType\":107,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"nack\",\"parameter\":\"\"},{\"type\":\"nack\",\"parameter\":\"pli\"},{\"type\":\"goog-remb\",\"parameter\":\"\"}],\"parameters\":{\"profile-level-id\":\"%s\",\"packetization-mode\":\"1\"},\"options\":{},\"maxTemporalLayers\":3,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false}],\"headerExtensions\":[],\"fecMechanisms\":[]}}",
                ufrag, passwd, role, fingerprint, audioPayloadType, videoPayloadType, profile);
        return recvCaps;
    }

    /**
     * Creates and returns an origin field given a raw session string from an sdp.
     * 
     * @param origin
     * @return OriginField
     */
    public static OriginField createOriginField(String origin) {
        Matcher matcher = OriginField.PATTERN.matcher(origin);
        if (matcher.find()) {
            String userName = matcher.group(1);
            String sessionId = matcher.group(2);
            long sessionVersion = Long.valueOf(matcher.group(3));
            String address = matcher.group(4);
            // log.debug("Address: {}", address);
            OriginField field = new OriginField(userName, sessionId, sessionVersion, address);
            log.debug("Created origin: {}", field);
            return field;
        }
        return null;
    }

    /**
     * Creates and returns a session field given a raw session string from an sdp.
     * 
     * @param session
     * @return SessionField
     */
    public static SessionField createSessionField(String session) {
        Matcher matcher = SessionField.PATTERN.matcher(session);
        if (matcher.find()) {
            String value = matcher.group(1);
            SessionField field = new SessionField(value);
            log.debug("Created session: {}", field);
            return field;
        }
        return null;
    }

    /**
     * Creates and returns a connection field given a raw bandwidth string from an
     * sdp.
     * 
     * @param connection
     * @return ConnectionField
     */
    public static ConnectionField createConnectionField(String connection) {
        String[] parts = connection.split("\\s");
        if (parts.length == 3) {
            ConnectionField field = new ConnectionField(parts[2], NetworkManager.NetworkType.valueOf(parts[0]), NetworkManager.AddressType.valueOf(parts[1]));
            log.debug("Created connection: {}", field);
            return field;
        }
        return null;
    }

    /**
     * Creates and returns a bandwidth field given a raw bandwidth string from an
     * sdp.
     * 
     * @param bandwidth
     * @return BandwidthField
     */
    public static BandwidthField createBandwidthField(String bandwidth) {
        Matcher matcher = BandwidthField.PATTERN.matcher(bandwidth);
        if (matcher.find()) {
            String type = matcher.group(1);
            int value = Integer.valueOf(matcher.group(2));
            BandwidthField field = new BandwidthField(type, value);
            log.debug("Created bandwidth: {}", field);
            return field;
        }
        return null;
    }

    /**
     * Creates and returns a attribute field given a raw attribute string from an
     * sdp.
     * 
     * @param attribute
     * @return AttributeField
     */
    public static AttributeField createAttributeField(String attribute) {
        Matcher matcher = AttributeField.PATTERN.matcher(attribute);
        if (matcher.find()) {
            // strip any dashes in the key name so our enums will work
            String keyStr = matcher.group(1).replace("-", "");
            try {
                AttributeKey key = AttributeKey.valueOf(keyStr);
                String value = null;
                if (matcher.groupCount() > 1) {
                    value = matcher.group(3);
                }
                // construct the field
                AttributeField field = new AttributeField(key, value);
                log.debug("Created attribute: {}", field);
                return field;
            } catch (IllegalArgumentException iae) {
                log.info("Unsupported attribute type: {}", iae.getMessage());
            }
        }
        return null;
    }

    /**
     * Creates and returns a attribute field.
     * 
     * @param key
     * @param value
     * @return AttributeField
     */
    public static AttributeField createAttributeField(AttributeKey key, String value) {
        return new AttributeField(key, value);
    }

    /**
     * Creates and returns a attribute field.
     * 
     * @param key
     * @param payloadId
     * @param value
     * @return AttributeField
     */
    public static AttributeField createAttributeField(AttributeKey key, int payloadId, String value) {
        return new AttributeField(key, String.format("%d %s", payloadId, value));
    }

    /**
     * Creates and returns a attribute field.
     * 
     * @param key
     * @param payloadId
     * @param encoding
     * @param clockRate
     * @return AttributeField
     */
    public static AttributeField createAttributeField(AttributeKey key, int payloadId, String encoding, int clockRate) {
        return new AttributeField(key, String.format("%d %s/%d", payloadId, encoding, clockRate));
    }

    /**
     * Creates and returns a attribute field.
     * 
     * @param key
     * @param payloadId
     * @param encoding
     * @param clockRate
     * @param channels
     * @return AttributeField
     */
    public static AttributeField createAttributeField(AttributeKey key, int payloadId, String encoding, int clockRate, int channels) {
        return new AttributeField(key, String.format("%d %s/%d/%d", payloadId, encoding, clockRate, channels));
    }

    /**
     * Creates and returns a media field given a raw media string from an sdp.
     * 
     * @param media
     * @return MediaField
     */
    public static MediaField createMediaField(String media) {
        log.debug("createMediaField: {}", media);
        // have to handle the case of newer datachannel sdp entries
        // normal "chrome" style: m=application 9 DTLS/SCTP 5000
        // newer style: m=application 9 UDP/DTLS/SCTP webrtc-datachannel
        // seen from Firefox: m=application 5000 UDP/DTLS/SCTP 5000
        // IPCam metadata: m=application 0 RTP/AVP 107
        Matcher matcher = MediaField.PATTERN.matcher(media);
        if (log.isDebugEnabled()) {
            log.debug("matcher matches: {} groups: {}", matcher.matches(), matcher.groupCount());
            log.debug("type: {} port: {} protocol: {} format(s): {}", matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        if (matcher.matches()) {
            MediaField field;
            SDPMediaType type = SDPMediaType.valueOf(matcher.group(1));
            switch (type) {
                case application:
                    // get the applications protocol
                    String proto = matcher.group(3);
                    // handle webrtc datachannel
                    if (proto.contains("SCTP")) {
                        // next up are the individual codec format / payload ids
                        field = new MediaField(SDPMediaType.application, 9, proto, new int[] { 5000 });
                        // older style DC config uses port (5000) instead of webrtc-datachannel string
                        if (!media.contains("webrtc-datachannel")) {
                            // add attributes for older type request
                            field.addAttributeField(new AttributeField(AttributeKey.sctpport, "5000"));
                            field.addAttributeField(new AttributeField(AttributeKey.maxmessagesize, "262144"));
                        }
                    } else {
                        // handle non-datachannel
                        field = new MediaField(SDPMediaType.application, Integer.valueOf(matcher.group(2)), proto, new int[] { Integer.valueOf(matcher.group(4).trim()) });
                    }
                    break;
                case audio:
                case video:
                default:
                    int port = Integer.valueOf(matcher.group(2));
                    String protocol = matcher.group(3);
                    // next up are the individual codec format / payload ids
                    String[] payloadIds = matcher.group(4).trim().split("\\s");
                    log.debug("Payload ids: {}", Arrays.toString(payloadIds));
                    int[] formats = new int[payloadIds.length];
                    // add the codec format / payload ids
                    for (int p = 0; p < payloadIds.length; p++) {
                        formats[p] = Integer.valueOf(payloadIds[p]);
                    }
                    // construct the field
                    field = new MediaField(type, port, protocol, formats.length);
                    // set the formats
                    field.setFormats(formats);
                    break;
            }
            log.debug("Created media: {}", field);
            return field;
        }
        return null;
    }

    /**
     * Creates and adds an SDP track with associated fields.
     * 
     * @param sdp
     * @param type
     * @param port
     * @param protocol
     * @param payloadId
     * @return SDPTrack
     */
    public static SDPTrack createTrack(SessionDescription sdp, String type, int port, String protocol, int payloadId) {
        SDPMediaType mediaType = SDPMediaType.valueOf(type);
        // construct the field with the format(s)
        MediaField field = new MediaField(mediaType, port, protocol, new int[] { payloadId });
        // add the field to the sdp
        sdp.addMediaDescription(field);
        // create the track
        SDPTrack track = new SDPTrack(sdp, mediaType, payloadId);
        // return the constructed track
        return track;
    }

    /**
     * Parse raw sdp content into the supplied SessionDescription instance.
     * 
     * @param sdp
     * @param rawSdp
     */
    private static void parse(SessionDescription sdp, String rawSdp) {
        // holder for current media field / media description
        MediaField mediaField = null;
        // spilt on cr/crlf
        String[] parts = rawSdp.split("[\n[\r\n]]");
        for (String part : parts) {
            if (log.isDebugEnabled()) {
                log.debug("{}", part);
            }
            if (StringUtils.isEmpty(part)) {
                continue;
            }
            // spilt on equals
            String[] line = part.split("=", 2);
            if (log.isDebugEnabled()) {
                log.debug("Line - {}", Arrays.toString(line));
            }
            char entry = line[0].charAt(0);
            // trim trailing spaces to prevent parser issues
            String fieldStr = line[1].trim();
            switch (entry) {
                case 'a':
                    AttributeField attr = createAttributeField(fieldStr);
                    if (attr != null) {
                        AttributeKey key = attr.getAttribute();
                        if (mediaField == null) {
                            // check for plan-b, but make sure we're not FireFox
                            if (AttributeKey.msidsemantic.equals(key)) {
                                // ff uses unified / plan-a
                                if (sdp.isFirefox()) {
                                    log.debug("Plan-B indicated, but ignoring due to our being firefox");
                                } else {
                                    log.debug("Plan-B indicated"); // default
                                }
                            } else if (AttributeKey.group.equals(key)) {
                                log.debug("Group / bundle indicated");
                                sdp.setBundle(true);
                            } else if (AttributeKey.crypto.equals(key)) {
                                // parses and adds the crypto param to the sdp instance but doesnt add an 'a'
                                // field
                                sdp.addCrypto(attr.getValue(), false);
                            }
                            // we're at session level
                            sdp.addAttributeField(attr);
                        } else {
                            // media level attribute
                            // log.debug("Media attribute: {}", attr);
                            mediaField.addAttributeField(attr);
                            // red5 pro sdk requries special handling that webrtc does not
                            if (sdp.isRed5ProSDK()) {
                                // look up the track matching our media field
                                SDPTrack track = SDPMediaType.audio.equals(mediaField.getMediaType()) ? sdp.getAudioTrack() : sdp.getVideoTrack();
                                // if the attribute is control, create a track with the first payload id
                                if (AttributeKey.control.equals(key)) {
                                    if (track == null) {
                                        track = new SDPTrack(sdp, mediaField.getMediaType(), mediaField.getFormats()[0]);
                                    }
                                    track.setControl(attr.getValue());
                                    log.debug("Created new track for control: {}", track);
                                } else if (AttributeKey.fmtp.equals(key)) {
                                    // if the user agent is red5 pro sdk get the track and apply these incoming parameters
                                    // if the track is null, control doesnt exist in the parse yet or fmtp came before it
                                    if (track == null) {
                                        track = new SDPTrack(sdp, mediaField.getMediaType(), mediaField.getFormats()[0]);
                                    }
                                    // a=fmtp:96 packetization-mode=1;sprop-parameter-sets=Z0LAINkAoD2hAAADAAEAAAMAMA8YMkg=,aMuDyyA=;profile-level-id=42C020
                                    // a=fmtp:97 profile-level-id=1;mode=AAC-hbr;sizelength=13;indexlength=3;indexdeltalength=3;config=119056E500
                                    String[] fmtParams = attr.getValue().split("\\s|;");
                                    log.debug("Format params array: {}", Arrays.toString(fmtParams));
                                    for (String fmtParam : fmtParams) {
                                        if (StringUtils.isNotBlank(fmtParam)) {
                                            if (fmtParam.contains("=")) {
                                                // sprop-parameter-sets may and usually does have multiple `=` chars
                                                if (fmtParam.startsWith(VideoConstants.H264_SPROP_PARAMETER_SETS_FMTP)) {
                                                    track.addParameter(VideoConstants.H264_SPROP_PARAMETER_SETS_FMTP, fmtParam.substring(fmtParam.indexOf('=') + 1));
                                                } else {
                                                    String[] param = fmtParam.split("=");
                                                    track.addParameter(param[0], param[1]);
                                                }
                                            } else {
                                                track.setPayloadId(Integer.valueOf(fmtParam));
                                            }
                                        }
                                    }
                                } else if (AttributeKey.rtpmap.equals(key)) {
                                    // if the user agent is red5 pro sdk get the track and apply these incoming parameters
                                    // if the track is null there may be on control or fmtp lines
                                    if (track == null) {
                                        track = new SDPTrack(sdp, mediaField.getMediaType(), mediaField.getFormats()[0]);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 'm':
                    mediaField = createMediaField(fieldStr);
                    // log.debug("Media: {}", mediaField);
                    sdp.addMediaDescription(mediaField);
                    break;
                case 'c':
                    ConnectionField cn = createConnectionField(fieldStr);
                    if (mediaField != null) {
                        // media level attribute
                        mediaField.setConnection(cn);
                    }
                    break;
                case 'b':
                    BandwidthField bw = createBandwidthField(fieldStr);
                    if (mediaField == null) {
                        // we're at session level
                        sdp.setBandwidth(bw);
                    } else {
                        // media level attribute
                        mediaField.setBandwidth(bw);
                    }
                    break;
                case 's': // session
                    sdp.setSession(createSessionField(fieldStr));
                    break;
                case 'o': // origin
                    if (fieldStr.toLowerCase().contains("mozilla")) {
                        sdp.setUA(SDPUserAgent.mozilla);
                        sdp.setUnified(true);
                    } else if (fieldStr.contains("ortc")) {
                        sdp.setUA(SDPUserAgent.edge);
                        // XXX edge doesnt signal plan-b, but wants it
                    }
                    sdp.setOrigin(createOriginField(fieldStr));
                    break;
                case 't':
                    // time offsets
                    // create the timing field since it may not be 0 0 at some point
                    break;
                case 'v':
                    // version is always 0 for the foreseeable future
                    break;
                default:
                    log.warn("Unhandled line type: {}", entry);
                    if (log.isDebugEnabled()) {
                        log.debug("Unhandled line: {}", Arrays.toString(line));
                    }
            }
        }
    }

}
