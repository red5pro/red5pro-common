package com.red5pro.media.sdp.model;

import java.util.regex.Pattern;

import com.red5pro.server.util.NetworkManager;

/**
 * The origin "o=" field gives the originator of the session (her username and
 * the address of the user's host) plus a session identifier and version.
 *
 * <pre>
    o=&lt;username&gt; &lt;sess-id&gt; &lt;sess-version&gt; &lt;nettype&gt; &lt;addrtype&gt; &lt;unicast-address&gt;
 * </pre>
 *
 * @author Paul Gregoire
 */
public class OriginField {

    /** Pattern matching the value portion of an origin line for IN IP4 addresses. */
    public final static Pattern PATTERN = Pattern.compile("([\\w|\\d|-]+) ([\\d]+) ([\\d]+) IN IP4 ([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})");

    private String userName;

    private String sessionId;

    private long sessionVersion;

    private NetworkManager.NetworkType networkType = NetworkManager.NetworkType.IN;

    private NetworkManager.AddressType addressType = NetworkManager.AddressType.IP4;

    private String address;

    /** Creates an empty origin field with IN IP4 defaults. */
    public OriginField() {
    }

    /**
     * Creates an origin field.
     * @param userName the username
     * @param sessionId the session identifier
     * @param sessionVersion the session version
     * @param address the unicast address
     */
    public OriginField(String userName, String sessionId, long sessionVersion, String address) {
        this.userName = userName;
        this.sessionId = sessionId;
        this.sessionVersion = sessionVersion;
        this.address = address;
    }

    /**
     * Returns the username.
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username.
     * @param userName the username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the session identifier.
     * @return the session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session identifier.
     * @param sessionId the session id
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns the session version.
     * @return the session version
     */
    public long getSessionVersion() {
        return sessionVersion;
    }

    /**
     * Sets the session version.
     * @param sessionVersion the session version
     */
    public void setSessionVersion(long sessionVersion) {
        this.sessionVersion = sessionVersion;
    }

    /**
     * Returns the network type.
     * @return the network type
     */
    public NetworkManager.NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Sets the network type.
     * @param networkType the network type
     */
    public void setNetworkType(NetworkManager.NetworkType networkType) {
        this.networkType = networkType;
    }

    /**
     * Returns the address type.
     * @return the address type
     */
    public NetworkManager.AddressType getAddressType() {
        return addressType;
    }

    /**
     * Sets the address type.
     * @param addressType the address type
     */
    public void setAddressType(NetworkManager.AddressType addressType) {
        this.addressType = addressType;
    }

    /**
     * Returns the unicast address.
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the unicast address.
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("o=%s %s %d %s %s %s\n", userName, sessionId, sessionVersion, networkType, addressType, address);
    }

}
