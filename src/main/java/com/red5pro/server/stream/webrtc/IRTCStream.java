package com.red5pro.server.stream.webrtc;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.red5.server.api.scope.IScope;
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
     * Returns the local candidates.
     * 
     * @return candidates
     */
    List<String> getLocalCandidates();

    /**
     * Sets remote candidates.
     * 
     * @param mlineIndex
     * @param remoteCandidates
     */
    void setRemoteCandidates(int mlineIndex, String remoteCandidates);

    /**
     * Sets remote ICE and DTLS properties. DTLS properties will arrive in the
     * "offer" or "answer" depending upon our direction; the streaming cannot start
     * until we have these.
     * 
     * @param sdp
     */
    void setRemoteProperties(SessionDescription sdp);

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

}
