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

    /**
     * Returns the audio bitrate.
     *
     * @return audio bitrate
     */
    int getAudioBitrate();

    /**
     * Returns the video bitrate.
     *
     * @return video bitrate
     */
    int getVideoBitrate();

    /**
     * Returns the negotiated audio payload type.
     *
     * @return audio payload type
     */
    int getAudioPayloadType();

    /**
     * Returns the negotiated video payload type.
     *
     * @return video payload type
     */
    int getVideoPayloadType();

    /**
     * Returns the audio SSRC.
     *
     * @return audio SSRC
     */
    int getAudioSSRC();

    /**
     * Sets the audio SSRC.
     *
     * @param audioSSRC audio SSRC
     */
    void setAudioSSRC(int audioSSRC);

    /**
     * Notifies the source of the audio codec and payload type selected during negotiation.
     *
     * @param selectedAudioCodec the selected audio codec
     * @param audioPayloadType payload type for the selected codec
     */
    void selectedAudioCodec(RTPCodecEnum selectedAudioCodec, int audioPayloadType);

    /**
     * Returns the video SSRC.
     *
     * @return video SSRC
     */
    int getVideoSSRC();

    /**
     * Sets the video SSRC.
     *
     * @param videoSSRC video SSRC
     */
    void setVideoSSRC(int videoSSRC);

    /**
     * Notifies the source of the video codec and payload type selected during negotiation.
     *
     * @param selectedVideoCodec the selected video codec
     * @param videoPayloadType payload type for the selected codec
     */
    void selectedVideoCodec(RTPCodecEnum selectedVideoCodec, int videoPayloadType);

    /**
     * Sets the profile name.
     *
     * @param profile profile name
     */
    void setProfile(String profile);

    /**
     * Returns the profile name.
     *
     * @return profile name
     */
    String getProfile();

    /**
     * Sets the requested audio bitrate.
     *
     * @param requestedAudioBitrate audio bitrate
     */
    void setAudioBitrate(int requestedAudioBitrate);

    /**
     * Sets the requested video bitrate.
     *
     * @param requestedVideoBitrate video bitrate
     */
    void setVideoBitrate(int requestedVideoBitrate);

}
