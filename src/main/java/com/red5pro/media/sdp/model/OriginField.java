package com.red5pro.media.sdp.model;

import java.util.regex.Pattern;

import com.red5pro.server.util.NetworkManager;

/**
 * The origin "o=" field gives the originator of the session (her username and
 * the address of the user's host) plus a session identifier and version.
 *
 * <pre>
    o=<username> <sess-id> <sess-version> <nettype> <addrtype> <unicast-address>
 * </pre>
 *
 * @author Paul Gregoire
 */
public class OriginField {

    public final static Pattern PATTERN = Pattern.compile("([\\w|\\d|-]+) ([\\d]+) ([\\d]+) IN IP4 ([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})");

    private String userName;

    private String sessionId;

    private long sessionVersion;

    private NetworkManager.NetworkType networkType = NetworkManager.NetworkType.IN;

    private NetworkManager.AddressType addressType = NetworkManager.AddressType.IP4;

    private String address;

    public OriginField() {
    }

    public OriginField(String userName, String sessionId, long sessionVersion, String address) {
        this.userName = userName;
        this.sessionId = sessionId;
        this.sessionVersion = sessionVersion;
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getSessionVersion() {
        return sessionVersion;
    }

    public void setSessionVersion(long sessionVersion) {
        this.sessionVersion = sessionVersion;
    }

    public NetworkManager.NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkManager.NetworkType networkType) {
        this.networkType = networkType;
    }

    public NetworkManager.AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(NetworkManager.AddressType addressType) {
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("o=%s %s %d %s %s %s\n", userName, sessionId, sessionVersion, networkType, addressType, address);
    }

}
