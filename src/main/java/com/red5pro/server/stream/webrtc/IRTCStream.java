package com.red5pro.server.stream.webrtc;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import com.red5pro.ice.TransportAddress;
import com.red5pro.ice.Agent;
import org.red5.server.api.scope.IScope;

import com.red5pro.io.StreamConnector;
import com.red5pro.media.MuteState;
import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.media.sdp.SessionDescription;
import com.red5pro.override.IProStream;

/**
 * Represents a stream used in the WebRTC subsystem.
 *
 * @author Paul Gregoire
 */
public interface IRTCStream {

    public static enum H264Profile {

        None, ConstrainedBaseline, Baseline, Main, High;

        public static H264Profile valueOf(int val) {
            switch (val) {
                case 1:
                    return ConstrainedBaseline;
                case 2:
                    return Baseline;
                case 3:
                    return Main;
                case 4:
                    return High;
            }
            return None;
        }

    };

    /**
     * Returns the name for this stream instance.
     *
     * @return name
     */
    String getName();

    /**
     * Get the local SDP as a SessionDescription.
     *
     * @return sdp
     */
    SessionDescription getSdp();

    /**
     * Get the local SDP as a string.
     *
     * @return sdp
     */
    String getLocalSdp();

    /**
     * Sets remote candidates.
     *
     * @param remoteCandidates
     */
    void setRemoteCandidates(List<String> remoteCandidates);

    /**
     * Initialize the streams and all the configuration steps.
     *
     * @param userAgent
     *            the user agent connected
     * @throws Exception
     */
    void init(SDPUserAgent userAgent) throws Exception;

    /**
     * Starts the stream instance.
     *
     * @return true if started and false otherwise
     */
    boolean start();

    /**
     * Stops the stream instance and cleans-up.
     */
    void stop();

    /**
     * Returns the associated pro/flash stream.
     *
     * @return ProStream
     */
    IProStream getProStream();

    /**
     * Returns the scope.
     *
     * @return scope
     */
    IScope getScope();

    /**
     * Returns whether or not we're controlling ICE.
     *
     * @return true if controlling and false otherwise
     */
    boolean isIceController();

    void setSubscriberModeStandby(boolean state);

    boolean isSubscriberModeStandby();

    /**
     * Returns the IoSession being used for media and ICE messages.
     *
     * @return IoSession or null if not connected nor established
     */
    IoSession getIoSession();

    /**
     * Whether or not the stream is starting.
     *
     * @return true if starting and false otherwise
     */
    boolean isStarting();

    /**
     * Returns the media id for at the given index in the local SDP.
     *
     * @param index
     * @return media id
     */
    String getMediaId(int index);

    /**
     * Returns an identifier for this instance.
     *
     * @return identifier which may or may not be the associated stream name
     */
    String getId();

    /**
     * Returns the muted state of the audio stream if it exists.
     *
     * @return true if muted, false if not muted, and undefined if there is no stream
     */
    MuteState isAudioMuted();

    /**
     * Mute audio.
     */
    void muteAudio();

    /**
     * Un-mute audio.
     */
    void unmuteAudio();

    /**
     * Returns the muted state of the video stream if it exists.
     *
     * @return true if muted, false if not muted, and undefined if there is no stream
     */
    MuteState isVideoMuted();

    /**
     * Mute video.
     */
    void muteVideo();

    /**
     * Un-mute video.
     */
    void unmuteVideo();

    /**
     * Returns the Agent if it exists.
     *
     * @return agent
     */
    Agent getAgent();

    /**
     * Sets the stream connector for the stream with a given remote address.
     *
     * @param streamConnector
     * @param remoteAddress
     */
    void setStreamConnector(StreamConnector streamConnector, TransportAddress remoteAddress);

    /**
     * Returns the allocated UDP port for the stream.
     *
     * @return port
     */
    int getAllocatedUdpPort();

    /**
     * Sets the allocated UDP port for the stream. If the port is 0, the port is cleared.
     *
     * @param port
     */
    void setAllocatedUdpPort(int port);

    /**
     * Returns the allocated TCP port for the stream.
     *
     * @return port
     */
    int getAllocatedTcpPort();

    /**
     * Sets the allocated TCP port for the stream. If the port is 0, the port is cleared.
     *
     * @param port
     */
    void setAllocatedTcpPort(int port);

    /**
     * Whether or not the connection for this instance originates from a remote network.
     *
     * @return true if remote and false if on the same machine or network.
     */
    boolean isRemoteEndpoint();

    /**
     * Returns whether or not session level DTLS is enabled.
     *
     * @return true if enabled, false otherwise
     */
    boolean isSessionLevelDTLS();

    /**
     * Sets whether or not session level DTLS is enabled.
     *
     * @param sessionLevelDTLS
     */
    void setSessionLevelDTLS(boolean sessionLevelDTLS);

    /**
     * Returns whether or not session level ICE is enabled.
     *
     * @return true if enabled, false otherwise
     */
    boolean isSessionLevelICE();

    /**
     * Sets whether or not session level ICE is enabled.
     *
     * @param setSessionLevelICE
     */
    void setSessionLevelICE(boolean setSessionLevelICE);

    /**
     * Returns whether or not end of candidates is enabled.
     *
     * @param setUseEndOfCandidates
     */
    void setUseEndOfCandidates(boolean setUseEndOfCandidates);

    /**
     * Returns whether or not end of candidates is enabled.
     *
     * @return true if enable, false otherwise
     */
    boolean useEndOfCandidates();

    default public void handleMuteStateEvent(MuteState audioMuteStateReq, MuteState videoMuteStateReq) {
        // default: no-op
    }
}
