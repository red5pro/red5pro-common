package com.red5pro.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.SourceType;

/**
 * Provides network functions.
 * 
 * @author Paul Gregoire
 */
public class NetworkManager {

	private static Logger log = LoggerFactory.getLogger(NetworkManager.class);

	private static final String IP_CHECK_URI = "http://checkip.amazonaws.com";

	// represents an un-set IP address
	private static final String NO_IP_ADDRESS = "no-ip";

	// public / external-facing IP address
	private static final AtomicReference<String> serverIp = new AtomicReference<>(NO_IP_ADDRESS);

	// private / local IP address
	private static final AtomicReference<String> serverLocalIp = new AtomicReference<>(NO_IP_ADDRESS);

	// collection of end-points created herein with the key being constructed based
	// on type and address+port combo
	private static ConcurrentMap<String, IngestEndpoint<?>> ingestEndPoints = new ConcurrentHashMap<>();

	/**
	 * Network type.
	 */
	public enum NetworkType {
		IN; // internet
	}

	/**
	 * Network address type.
	 */
	public enum AddressType {
		IP4, // IPv4
		IP6; // IPv6
	}

	/**
	 * Returns a publicly accessible IP address for this originator using a free
	 * service on Amazon AWS.
	 * 
	 * @return IP address
	 */
	public static String getPublicAddress() {
		String ipAddress = serverIp.get();
		if (NO_IP_ADDRESS.equals(ipAddress)) {
			ipAddress = null;
			BufferedReader in = null;
			try {
				URL checkip = new URL(IP_CHECK_URI);
				URLConnection con = checkip.openConnection();
				con.setConnectTimeout(3000);
				con.setReadTimeout(3000);
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				// set ip from service
				ipAddress = in.readLine();
				log.debug("Public address (detected): {}", ipAddress);
				// store it
				setServerIp(ipAddress);
				/*
				 * if (serverIp.compareAndSet(NO_IP_ADDRESS, ipAddress)) {
				 * log.debug("Server public IP set to {}", ipAddress); } else {
				 * log.warn("Server public IP address failure: {} {}", ipAddress,
				 * serverIp.get()); }
				 */
			} catch (Throwable t) {
				log.warn("Host could not be reached or timed-out", t);
				// fall-back to server local
				ipAddress = getLocalAddress();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						log.warn("Exception getting public IP", e);
					}
				}
			}
		}
		// grab for any updates
		ipAddress = serverIp.get();
		log.debug("Public address (stored): {}", ipAddress);
		// one last check to ensure we send null for our no-ip placeholder
		ipAddress = NO_IP_ADDRESS.equals(ipAddress) ? null : ipAddress;
		log.info("Public address: {}", ipAddress);
		return ipAddress;
	}

	/**
	 * Returns the private / local IP address for the active network interface(s).
	 * 
	 * @return IP address
	 */
	public static String getLocalAddress() {
		String ipAddress = serverLocalIp.get();
		if (NO_IP_ADDRESS.equals(ipAddress)) {
			ipAddress = null;
			try {
				// cheap and dirty way to get the preferred local IP
				DatagramSocket socket = null;
				try {
					socket = new DatagramSocket();
					socket.connect(InetAddress.getByName("8.8.8.8"), PortManager.findFreeUdpPort());
					ipAddress = socket.getLocalAddress().getHostAddress();
				} catch (Exception ioe) {
					log.warn("Exception getting local address via dgram", ioe);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (Exception ce) {
						}
					}
				}
				BUST_OUT : // check nic cards
				if (ipAddress == null) {
					Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
					while (ifaces.hasMoreElements()) {
						NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
						Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
						while (iaddresses.hasMoreElements()) {
							InetAddress iaddress = (InetAddress) iaddresses.nextElement();
							if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress()
									&& !iaddress.isSiteLocalAddress()) {
								ipAddress = iaddress.getHostAddress() != null
										? iaddress.getHostAddress()
										: iaddress.getHostName();
								log.debug("Local address (nic): {}", ipAddress);
								break BUST_OUT;
							}
						}
					}
				}
				// last resort, local host
				if (ipAddress == null) {
					ipAddress = InetAddress.getLocalHost().getHostAddress() != null
							? InetAddress.getLocalHost().getHostAddress()
							: InetAddress.getLocalHost().getHostName();
				}
				log.debug("Local address (detected): {}", ipAddress);
				// store it
				setServerLocalIp(ipAddress);
				/*
				 * if (serverLocalIp.compareAndSet(NO_IP_ADDRESS, ipAddress)) {
				 * log.debug("Server local IP set to {}", ipAddress); } else {
				 * log.warn("Server local IP address failure: {} {}", ipAddress,
				 * serverLocalIp.get()); }
				 */
			} catch (Exception e) {
				log.warn("Exception getting local address", e);
			}
		}
		// grab for any updates
		ipAddress = serverLocalIp.get();
		log.debug("Local address (stored): {}", ipAddress);
		// one last check to ensure we send null for our no-ip placeholder
		ipAddress = NO_IP_ADDRESS.equals(ipAddress) ? null : ipAddress;
		log.info("Local address: {}", ipAddress);
		return ipAddress;
	}

	public static void setServerIp(String ipAddress) {
		log.debug("setServerIp: {}", ipAddress);
		// disallow null or blank IP
		if (ipAddress != null && ipAddress.length() > 6) {
			NetworkManager.serverIp.set(ipAddress);
		}
	}

	public static void setServerLocalIp(String ipAddress) {
		log.debug("setServerLocalIp: {}", ipAddress);
		// disallow null or blank IP
		if (ipAddress != null && ipAddress.length() > 6) {
			NetworkManager.serverLocalIp.set(ipAddress);
		}
	}

	/**
	 * Validates a given IP address and port (optional) for binding.
	 * 
	 * @param ipAddress
	 * @param port
	 * @return true if bindable and false otherwise
	 */
	public static boolean validIPAddress(String ipAddress, int port) {
		log.debug("validIPAddress: {}:{}", ipAddress, port);
		// start off invalid
		boolean valid = false;
		// check the port
		boolean validPort = (port > 0 && port < 65535);
		// if its the ANY address accept it
		if ("0.0.0.0".equals(ipAddress)) {
			valid = true;
		} else if (getLocalAddress().equals(ipAddress) || getPublicAddress().equals(ipAddress)) {
			// if the address is equal to our local or public IP address then its a-ok so
			// far
			log.debug("Local or public address matched");
			// IP is valid since we're already bound to it
			valid = true;
		} else {
			InetAddress addr;
			try {
				// attempt to create an inet address
				addr = InetAddress.getByName(ipAddress);
				// check for Class-D address and accept by default
				if (addr.isMulticastAddress()) {
					log.debug("Multicast address valid");
					valid = true;
				} else {
					int count = 0;
					Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
					while (ifaces.hasMoreElements()) {
						NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
						Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
						while (iaddresses.hasMoreElements()) {
							InetAddress iaddress = (InetAddress) iaddresses.nextElement();
							log.debug("Local address {}: {} link local: {} site local: {}", iface.getName(), iaddress,
									iaddress.isLinkLocalAddress(), iaddress.isSiteLocalAddress());
							if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress()) {
								String nicAddress = iaddress.getHostAddress() != null
										? iaddress.getHostAddress()
										: iaddress.getHostName();
								// log.debug("Local address {}: {}", iface.getName(), nicAddress);
								if (ipAddress.equals(nicAddress)) {
									return true & validPort;
								}
							}
						}
						count++;
					}
					log.debug("Network interfaces: {}", count);
				}
			} catch (Exception e) {
				log.warn("Exception validating address {}", ipAddress, e);
			}
		}
		return valid & validPort;
	}

	/**
	 * Validates a given IP address and port (optional) for binding.
	 * 
	 * @param ipAddress
	 * @param port
	 * @return true if bindable and false otherwise
	 */
	public static boolean validMulticastIPAddress(String groupName, int groupPort) {
		log.debug("validMulticastIPAddress: {}:{}", groupName, groupPort);
		boolean valid = false;
		// start off by checking the port
		boolean validPort = (groupPort > 0 && groupPort < 65535);
		InetAddress addr;
		try {
			// attempt to create an inet address
			addr = InetAddress.getByName(groupName);
			// check for Class-D address and accept by default
			if (addr.isMulticastAddress()) {
				valid = true;
			}
		} catch (Exception e) {
			log.warn("Exception validating address {}", groupName, e);
		}
		return valid & validPort;
	}

	/**
	 * Returns the ingest end-point map.
	 * 
	 * @return ingestEndPoints
	 */
	public static ConcurrentMap<String, IngestEndpoint<?>> getIngestEndPoints() {
		return ingestEndPoints;
	}

	/**
	 * Returns an unmodifiable copy of the ingest end-points matching the source
	 * type.
	 * 
	 * @return ingestEndPoints
	 */
	public static Map<String, IngestEndpoint<?>> getIngestEndPointsByType(SourceType type) {
		// create a non-concurrent copy of the current end-points map
		Map<String, IngestEndpoint<?>> result = ingestEndPoints.entrySet().stream()
				.filter(e -> e.getValue().getType() == type)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		// make it unmodifiable / read-only
		return (Map<String, IngestEndpoint<?>>) Collections.unmodifiableMap(result);
	}

	/**
	 * Removes an end-point by its id.
	 * 
	 * @param endPointId
	 * @return ingest end-point matching the id or null if no match exists
	 */
	public static IngestEndpoint<?> disposeIngestEndpoint(String endPointId) {
		IngestEndpoint<?> endPoint = ingestEndPoints.remove(endPointId);
		log.debug("disposeIngestEndpoint: {} {}", endPointId, endPoint);
		if (endPoint != null) {
			endPoint.getConnection().close();
		} else {
			log.warn("Endpoint not found, most likely already disposed {}", endPointId);
		}
		return endPoint;
	}

}
