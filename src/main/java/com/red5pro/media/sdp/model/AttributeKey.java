package com.red5pro.media.sdp.model;

/**
 * Session description attribute field keys and flags.
 * 
 * @author Paul Gregoire
 */
public enum AttributeKey {
    bundleonly, candidate, // ICE candidate
    endofcandidates("end-of-candidates"), // end of ICE candidates marker
    group, // Grouping
    msidsemantic("msid-semantic"), rtcp, // RTCP connection information
    iceufrag("ice-ufrag"), // ICE user fragment
    icepwd("ice-pwd"), // ICE password
    iceoptions("ice-options"), // Indication of trickle support
    icelite("ice-lite"), // Indication of ice lite support
    fingerprint, // Hash and fingerprint
    setup, // DTLS configuration
    mid, // Media id
    extmap, // Format extension
    sendrecv, // Send and receive flag
    sendonly, // Send-only flag
    recvonly, // Receive-only flag
    inactive, rtcpmux("rtcp-mux"), // RTCP mux flag
    rtcpfb("rtcp-fb"), rtpmap, // RTP codec mapping
    rtcprsize("rtcp-rsize"), // RTCP reduced-size (non-compound)
    fmtp, // Format parameter
    ptime, // Packet time
    maxptime, // Maximum packet time
    metadata, // Metadata
    tsrefclk, // Source of the Network Clock
    mediaclk, // RFC-7273
    type, //
    orient, // orientation
    msid, // MST id
    ssrc, //
    ssrcgroup("ssrc-group"), control, // a=control:trackID=1 (from RFC) or a=control:streamid=0 (from ffmpeg)
    range, // a=range:npt=0.000000-
    framerate, // a=framerate:15.0
    transform, // a=transform:-1,0,0;0,-1,0;0,0,1
    tool, // a=tool:libavformat 57.83.100
    sdplang, // expect en for English is most cases
    crypto, // SRTP for Mobile SDK
    maxprate, // max packet rate?
    mimetype, // mime type
    framesize, //
    imageattr, // image attributes / dimensions
    width, height, Width, Height, // capitalized versions are for legacy support
    sctpmap, sctpport("sctp-port"), maxmessagesize("max-message-size"), // sctp / datachannel / application
    etag, charset, xgoogleflag("x-google-flag");

    // used when the "name" has a dash char in it, since a dash isnt valid for an enum
    private String nameOverride;

    AttributeKey() {
    }

    AttributeKey(String name) {
        this.nameOverride = name;
    }

    @Override
    public String toString() {
        return nameOverride == null ? name() : nameOverride;
    }

}
