package com.red5pro.media.sdp.model;

/**
 * Session description attribute field keys and flags.
 *
 * @author Paul Gregoire
 */
public enum AttributeKey {

    /** SDP attribute key bundleonly. */
    bundleonly,
    /** SDP attribute key bundleonlyDASHED. */
    bundleonlyDASHED("bundle-only"),
    /** SDP attribute key candidate. */
    candidate,
    /** SDP attribute key endofcandidates. */
    endofcandidates("end-of-candidates"),
    /** SDP attribute key group. */
    group,
    /** SDP attribute key msidsemantic. */
    msidsemantic("msid-semantic"),
    /** SDP attribute key rtcp. */
    rtcp,
    /** SDP attribute key iceufrag. */
    iceufrag("ice-ufrag"),
    /** SDP attribute key icepwd. */
    icepwd("ice-pwd"),
    /** SDP attribute key iceoptions. */
    iceoptions("ice-options"),
    /** SDP attribute key icelite. */
    icelite("ice-lite"),
    /** SDP attribute key fingerprint. */
    fingerprint,
    /** SDP attribute key setup. */
    setup,
    /** SDP attribute key mid. */
    mid,
    /** SDP attribute key extmap. */
    extmap,
    /** SDP attribute key sendrecv. */
    sendrecv,
    /** SDP attribute key sendonly. */
    sendonly,
    /** SDP attribute key recvonly. */
    recvonly,
    /** SDP attribute key inactive. */
    inactive,
    /** SDP attribute key rtcpmux. */
    rtcpmux("rtcp-mux"),
    /** SDP attribute key rtcpfb. */
    rtcpfb("rtcp-fb"),
    /** SDP attribute key rtpmap. */
    rtpmap,
    /** SDP attribute key rtcprsize. */
    rtcprsize("rtcp-rsize"),
    /** SDP attribute key fmtp. */
    fmtp,
    /** SDP attribute key ptime. */
    ptime,
    /** SDP attribute key maxptime. */
    maxptime,
    /** SDP attribute key metadata. */
    metadata,
    /** SDP attribute key tsrefclk. */
    tsrefclk,
    /** SDP attribute key mediaclk. */
    mediaclk,
    /** SDP attribute key type. */
    type,
    /** SDP attribute key orient. */
    orient,
    /** SDP attribute key msid. */
    msid,
    /** SDP attribute key ssrc. */
    ssrc,
    /** SDP attribute key ssrcgroup. */
    ssrcgroup("ssrc-group"),
    /** SDP attribute key control. */
    control,
    /** SDP attribute key range. */
    range,
    /** SDP attribute key framerate. */
    framerate,
    /** SDP attribute key transform. */
    transform,
    /** SDP attribute key tool. */
    tool,
    /** SDP attribute key sdplang. */
    sdplang,
    /** SDP attribute key crypto. */
    crypto,
    /** SDP attribute key maxprate. */
    maxprate,
    /** SDP attribute key mimetype. */
    mimetype,
    /** SDP attribute key framesize. */
    framesize,
    /** SDP attribute key imageattr. */
    imageattr,
    /** SDP attribute key width. */
    width,
    /** SDP attribute key height. */
    height,
    /** SDP attribute key Width. */
    Width,
    /** SDP attribute key Height. */
    Height,
    /** SDP attribute key sctpmap. */
    sctpmap,
    /** SDP attribute key sctpport. */
    sctpport("sctp-port"),
    /** SDP attribute key maxmessagesize. */
    maxmessagesize("max-message-size"),
    /** SDP attribute key etag. */
    etag,
    /** SDP attribute key charset. */
    charset,
    /** SDP attribute key xgoogleflag. */
    xgoogleflag("x-google-flag"),
    /** SDP attribute key extmapallowmixed. */
    extmapallowmixed,
    /** SDP attribute key extmapallowmixedDASHED. */
    extmapallowmixedDASHED("extmap-allow-mixed"),
    /** SDP attribute key rid. */
    rid,
    /** SDP attribute key simulcast. */
    simulcast,
    /** SDP attribute key rtcpxr. */
    rtcpxr("rtcp-xr"),
    /** SDP attribute key sctpinit. */
    sctpinit("sctp-init"),
    /** SDP attribute key filter. */
    filter;

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
