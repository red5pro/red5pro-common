package com.red5pro.media.sdp;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.VideoConstants;
import com.red5pro.media.sdp.model.AttributeField;
import com.red5pro.media.sdp.model.AttributeKey;
import com.red5pro.media.sdp.model.BandwidthField;
import com.red5pro.media.sdp.model.ConnectionField;
import com.red5pro.media.sdp.model.MediaField;
import com.red5pro.media.sdp.model.SDPMediaType;

/**
 * Decoder for handling an SDP one line at a time. Call doFinal() when all the lines have be added.
 * 
 * @author Paul Gregoire
 */
public class SDPSingleLineDecoder {

    private static Logger log = LoggerFactory.getLogger(SDPSingleLineDecoder.class);

    // sdp
    private SessionDescription sdp = new SessionDescription();

    // holder for current or last media field / media description
    private MediaField mediaField;

    public void readLine(String in) {
        if (log.isDebugEnabled()) {
            log.debug("{}", in);
        }
        if (StringUtils.isNotEmpty(in)) {
            // strip the crlf and spilt on equals
            String[] line = in.replaceAll("[\n|\r\n]", "").split("=", 2);
            if (log.isDebugEnabled()) {
                log.debug("Line - {}", Arrays.toString(line));
            }
            char entry = line[0].charAt(0);
            switch (entry) {
                case 'a':
                    AttributeField attr = SDPFactory.createAttributeField(line[1]);
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
                            }
                            // we're at session level
                            sdp.addAttributeField(attr);
                        } else {
                            // media level attribute
                            log.debug("Media attribute: {}", attr);
                            mediaField.addAttributeField(attr);
                            // look up the track matching our media field
                            SDPMediaType mtype = mediaField.getMediaType();
                            SDPTrack track = SDPMediaType.audio.equals(mtype) ? sdp.getAudioTrack() : (SDPMediaType.video.equals(mtype) ? sdp.getVideoTrack() : sdp.getApplicationTrack());
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
                                                if (param.length > 1) {
                                                    track.addParameter(param[0], param[1]);
                                                } else {
                                                    log.warn("{} is missing its value", param[0]);
                                                }
                                            }
                                        } else {
                                            track.setPayloadId(Integer.valueOf(fmtParam));
                                        }
                                    }
                                }
                            }

                        }
                    }
                    break;
                case 'm':
                    mediaField = SDPFactory.createMediaField(line[1]);
                    // log.debug("Media: {}", mediaField);
                    sdp.addMediaDescription(mediaField);
                    break;
                case 'c':
                    ConnectionField cn = SDPFactory.createConnectionField(line[1]);
                    if (mediaField != null) {
                        // media level attribute
                        mediaField.setConnection(cn);
                    }
                    break;
                case 'b':
                    BandwidthField bw = SDPFactory.createBandwidthField(line[1]);
                    if (mediaField == null) {
                        // we're at session level
                        sdp.setBandwidth(bw);
                    } else {
                        // media level attribute
                        mediaField.setBandwidth(bw);
                    }
                    break;
                case 's': // session
                    sdp.setSession(SDPFactory.createSessionField(line[1]));
                    break;
                case 'o': // origin
                    if (line[1].toLowerCase().contains("mozilla")) {
                        sdp.setUA(SDPUserAgent.mozilla);
                        sdp.setUnified(true);
                    } else if (line[1].contains("ortc")) {
                        sdp.setUA(SDPUserAgent.edge);
                        // XXX edge doesnt signal plan-b, but wants it
                    }
                    sdp.setOrigin(SDPFactory.createOriginField(line[1]));
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

    public SessionDescription doFinal() {
        return sdp;
    }

}
