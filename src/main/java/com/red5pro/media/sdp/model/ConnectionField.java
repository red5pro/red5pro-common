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
      c=<nettype> <addrtype> <connection-address>
 * </pre>
 *
 * @author Paul Gregoire
 */
public class ConnectionField {

	private NetworkManager.NetworkType networkType = NetworkManager.NetworkType.IN;

	private NetworkManager.AddressType addressType = NetworkManager.AddressType.IP4;

	private String address = "0.0.0.0";

	public ConnectionField() {
	}

	public ConnectionField(String address) {
		this.address = address;
	}

	public ConnectionField(String address, NetworkManager.NetworkType networkType,
			NetworkManager.AddressType addressType) {
		this.address = address;
		this.networkType = networkType;
		this.addressType = addressType;
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
		return String.format("c=%s %s %s\n", networkType, addressType, address);
	}

}
