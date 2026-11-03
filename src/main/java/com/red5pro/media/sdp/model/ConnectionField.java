package com.red5pro.media.sdp.model;

import com.red5pro.server.util.NetworkManager;

/**
 * The connection data "c=" field contains connection data.
 *
 * A session description MUST contain either at least one "c=" field in each
 * media description or a single "c=" field at the session level. It MAY contain
 * a single session-level "c=" field and additional "c=" field(s) per media
 * description, in which case the per-media values override the session-level
 * settings for the respective media.
 *
 * <pre>
      c=&lt;nettype&gt; &lt;addrtype&gt; &lt;connection-address&gt;
 * </pre>
 *
 * @author Paul Gregoire
 */
public class ConnectionField {

    private NetworkManager.NetworkType networkType = NetworkManager.NetworkType.IN;

    private NetworkManager.AddressType addressType = NetworkManager.AddressType.IP4;

    private String address = "0.0.0.0";

    /** Creates a connection field with the default IN IP4 0.0.0.0 values. */
    public ConnectionField() {
    }

    /**
     * Creates a connection field for the given address using the default IN IP4 types.
     * @param address the connection address
     */
    public ConnectionField(String address) {
        this.address = address;
    }

    /**
     * Creates a fully specified connection field.
     * @param address the connection address
     * @param networkType the network type
     * @param addressType the address type
     */
    public ConnectionField(String address, NetworkManager.NetworkType networkType, NetworkManager.AddressType addressType) {
        this.address = address;
        this.networkType = networkType;
        this.addressType = addressType;
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
     * Returns the connection address.
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the connection address.
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("c=%s %s %s\n", networkType, addressType, address);
    }

}
