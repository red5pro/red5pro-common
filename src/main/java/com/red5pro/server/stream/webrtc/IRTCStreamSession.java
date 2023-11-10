package com.red5pro.server.stream.webrtc;

import java.util.concurrent.Future;

import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.override.IProStream;

/**
 * Interface for RTCStreamSession to allow interoperability.
 *
 * @author Paul Gregoire
 *
 */
public interface IRTCStreamSession {

    /**
     * Start the session.
     */
    void start();

    /**
     * Stop the session.
     */
    void stop();

    /**
     * Returns true if the session is started.
     *
     * @return started
     */
    boolean isStarted();

    /**
     * Returns creation time for this session.
     *
     * @return created
     */
    long getCreated();

    IRTCStream getRtcStream();

    IProStream getProStream();

    void updateProStream(IProStream stream);

    Future<?> getCreationFuture();

    void setCreationFuture(Future<Boolean> createFuture);

    /**
     * Sets the User-Agent from the WebSocket connection.
     *
     * @see <a href="https://www.whatismybrowser.com/developers/tools/user-agent-parser/browse">UA Browser List</a>
     *
     * @param userAgent User-Agent from the WebSocket
     */
    void setUserAgent(String userAgent);

    /**
     * Returns the user-agent string.
     *
     * @return userAgent
     */
    default String getUserAgent() {
        return SDPUserAgent.undefined.name();
    }

    /**
     * Returns the user-agent enum.
     *
     * @return userAgentEnum
     */
    default SDPUserAgent getUserAgentEnum() {
        return SDPUserAgent.undefined;
    }

    /**
     * Returns the user-agents version string.
     *
     * @return userAgentVersion
     */
    default String getUAVersion() {
        return "0";
    }

    /**
     * Returns true if the User-Agent is Android.
     *
     * @return true if Android and false otherwise
     */
    boolean isAndroid();

    /**
     * Returns true if the User-Agent is iOS.
     *
     * @return true if iOS and false otherwise
     */
    boolean isIOS();

    /**
     * Returns true if the User-Agent is Windows.
     *
     * @return true if Windows and false otherwise
     */
    boolean isWin();

    /**
     * Returns true if the User-Agent is Linux.
     *
     * @return true if Linux and false otherwise
     */
    boolean isLinux();

    /**
     * Returns true if the User-Agent is Mac.
     *
     * @return true if Mac and false otherwise
     */
    boolean isMac();

    /**
     * Returns true if the User-Agent is Chrome.
     *
     * @see <a href="https://developer.chrome.com/multidevice/user-agent">UA Reference</a>
     *
     * @return true if Chrome and false otherwise
     */
    boolean isChrome();

    /**
     * Returns whether or not its a webview.
     *
     * @see https://developer.chrome.com/multidevice/user-agent
     *
     * @return true if WebView and false otherwise
     */
    boolean isWebView();

    /**
     * Returns true if the User-Agent is Firefox. (ex. Mozilla/5.0 (X11; Ubuntu;
     * Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0)
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Gecko_user_agent_string_reference">UA Reference</a>
     *
     * @return true if Firefox and false otherwise
     */
    boolean isFirefox();

    boolean isEdge();

    // http://useragentstring.com/pages/useragentstring.php?name=Safari
    boolean isSafari();

    boolean isPostman();

    /**
     * Opera (ex. Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like
     * Gecko) Chrome/58.0.3029.81 Safari/537.36 OPR/45.0.2552.812)
     *
     * @return true if Opera and false otherwise
     */
    boolean isOpera();

    /**
     * Jetty WebSocket client.
     *
     * @return true if Jetty WebSocket client and false otherwise
     */
    boolean isJettyWS();

    /**
     * Check header to determine if this is a proxied connection.
     *
     * @return true if proxied / forwarded
     */
    boolean isProxied();

    /**
     * OBS-Studio.
     *
     * @return true if OBS client and false otherwise
     */
    boolean isOBS();

}
