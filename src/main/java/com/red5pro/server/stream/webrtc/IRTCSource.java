package com.red5pro.server.stream.webrtc;

import com.red5pro.media.rtp.RTPCodecEnum;
import com.red5pro.media.sdp.SessionDescription;

/**
 * Common interface for any RTC source which may utilize the RTCStreamConfigurator.
 *
 * @author Paul Gregoire
 */
public interface IRTCSource {

    /**
     * Returns the offer sdp in SessionDescription form.
     *
     * @return offer sdp
     */
    SessionDescription getOfferSdp();

    int getAudioBitrate();

    int getVideoBitrate();

    int getAudioPayloadType();

    int getVideoPayloadType();

    int getAudioSSRC();

    void setAudioSSRC(int audioSSRC);

    void selectedAudioCodec(RTPCodecEnum selectedAudioCodec, int audioPayloadType);

    int getVideoSSRC();

    void setVideoSSRC(int videoSSRC);

    void selectedVideoCodec(RTPCodecEnum selectedVideoCodec, int videoPayloadType);

    void setProfile(String profile);

    String getProfile();

    void setAudioBitrate(int requestedAudioBitrate);

    void setVideoBitrate(int requestedVideoBitrate);

}
