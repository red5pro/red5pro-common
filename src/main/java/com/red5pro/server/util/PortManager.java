package com.red5pro.server.util;

import java.net.DatagramSocket;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.apache.commons.lang3.RandomUtils;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * Provides ports and more.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire
 */
public class PortManager {

	private static Logger log = Red5LoggerFactory.getLogger(PortManager.class);

	// maximum ephemeral port inclusive
	private static final int MAX_PORT = 65535;

	// default low anon ephemeral port inclusive; max 65535
	private static int rtpPortBase = 49152;

	// maximum anon ephemeral port 65535
	private static int rtpPortCeiling = MAX_PORT;

	// holds ports that have been allocated and only those that are allocated
	private static CopyOnWriteArraySet<Integer> allocatedPorts = new CopyOnWriteArraySet<>();

	// port selection base
	@SuppressWarnings("unused")
	private volatile int port = Math.max(1024, (rtpPortBase - 1)); // use 1024 as base max to avoid root issue on unix

	// atomic updater for the port
	private static AtomicIntegerFieldUpdater<PortManager> portUpdater = AtomicIntegerFieldUpdater
			.newUpdater(PortManager.class, "port");

	// single static instance of this port manager for use with the atomic updater
	private static final PortManager instance = new PortManager();

	/**
	 * Clear an allocated port entry.
	 * 
	 * @param rtpPort
	 *            the port to clear
	 */
	public static void clearRTPServerPort(int rtpPort) {
		if (allocatedPorts.remove(rtpPort)) {
			log.debug("Removing server port {}", rtpPort);
		} else {
			log.debug("Port {} was not allocated or has already been cleared", rtpPort);
		}
	}

	/**
	 * Clear a collection of allocated port entries.
	 * 
	 * @param allocatedPorts
	 *            ports to clear
	 */
	public static void clearRTPServerPorts(Collection<Integer> allocatedPorts) {
		for (Integer port : allocatedPorts) {
			PortManager.clearRTPServerPort(port);
		}
	}

	/**
	 * Get an available port.
	 * 
	 * @return port for use with a socket
	 */
	public static int getRTPServerPort() {
		log.debug("Get port");
		int serverPort = portUpdater.incrementAndGet(instance);
		for (; serverPort < rtpPortCeiling; serverPort = portUpdater.incrementAndGet(instance)) {
			// add only works if its not already allocated
			if (allocatedPorts.add(serverPort)) {
				// if the port isn't allocated, allocate it and check availability
				if (!checkAvailable(serverPort)) {
					// port is not available
					log.warn("Unallocated port is already bound {}", serverPort);
					continue;
				}
				// break out with currently available port
				break;
			}
		}
		// reset the port incrementer if we've exceeded the bounds (minus 1 since we
		// always use incrementAndGet)
		if (serverPort > rtpPortCeiling) {
			// if the first port exceeded the bounds reset
			portUpdater.set(instance, (rtpPortBase - 1));
			log.info("Port range reset due to exceeding bounds");
			// get a new port post reset
			serverPort = portUpdater.incrementAndGet(instance);
		}
		log.debug("Port allocated {}", serverPort);
		return serverPort;
	}

	/**
	 * Get an available port using randomizer.
	 * 
	 * @return port for use with a socket
	 */
	public static int getRTPServerPortRandom() {
		log.debug("Get port");
		// start a random port within range
		int serverPort = RandomUtils.nextInt(rtpPortBase, rtpPortCeiling);
		for (; serverPort < rtpPortCeiling; serverPort++) {
			// add only works if its not already allocated
			if (allocatedPorts.add(serverPort)) {
				// flag to check availability
				if (!checkAvailable(serverPort)) {
					// port is not available
					log.warn("Unallocated port is already bound {}", serverPort);
					continue;
				}
				// break out with currently available port
				break;
			}
		}
		log.debug("Port allocated {}", serverPort);
		return serverPort;
	}

	/**
	 * Checks a port for availability using DatagramSocket; this may or may not be
	 * useful for TCP as well.
	 * 
	 * @param port
	 *            to check
	 * @return true if port is available and false otherwise
	 */
	public static boolean checkAvailable(int port) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			socket.setReuseAddress(true);
			socket.setSoTimeout(100);
			int retPort = socket.getLocalPort();
			if (port == retPort) {
				log.debug("Port: {} is available", port);
			} else {
				log.debug("Port didnt match: {}", retPort);
			}
			return true;
		} catch (Throwable t) {
			if (log.isTraceEnabled()) {
				log.warn("Exception checking port: {}", port, t);
			}
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return false;
	}

	/**
	 * Returns a free UDP port on this machine.
	 * 
	 * @return port
	 */
	public static int findFreeUdpPort() {
		DatagramSocket socket = null;
		int port = 0;
		do {
			try {
				socket = new DatagramSocket(0);
				socket.setReuseAddress(true);
				socket.setSoTimeout(100);
				port = socket.getLocalPort();
				return port;
			} catch (Throwable t) {
				if (log.isTraceEnabled()) {
					log.warn("Exception checking port: {}", port, t);
				}
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		} while (port < MAX_PORT);
		throw new IllegalStateException("Could not find a free UDP port");
	}

	/**
	 * Sets the port base and also reset last port allocated to the newly selected
	 * base minus one.
	 * 
	 * @param rtpPortBase
	 *            base port to start on
	 */
	public static void setRtpPortBase(int rtpPortBase) {
		if (rtpPortBase > MAX_PORT) {
			log.error("Invalid base port: {}; defaulting to 1024", rtpPortBase);
			// XXX setting this to the max would seem to prevent any port allocations at
			// all; use default >root port instead 1024
			rtpPortBase = 1024;
		}
		PortManager.rtpPortBase = rtpPortBase;
		// set the port base on this change (minus 1 since we always use
		// incrementAndGet)
		portUpdater.set(instance, (rtpPortBase - 1));
	}

	public static void setRtpPortCeiling(int rtpPortCeiling) {
		if (rtpPortCeiling > MAX_PORT) {
			log.error("Invalid max port: {}; defaulting to {}", rtpPortCeiling, MAX_PORT);
			rtpPortCeiling = MAX_PORT;
		}
		PortManager.rtpPortCeiling = rtpPortCeiling;
	}

	/**
	 * Sets whether or not to test a port for availability before returning it.
	 * 
	 * @param checkPortAvailability
	 *            true to check the port with a binding and false to simply return
	 *            it
	 */
	public static void setCheckPortAvailability(boolean checkPortAvailability) {
		// log.info("ignoring request for port checks {}",checkPortAvailability);
		// PortManager.checkPortAvailability = checkPortAvailability;
	}

	/**
	 * Returns allocated UDP ports; this can include ports allocated outside this
	 * application in the specified range.
	 * 
	 * @return allocated port count
	 */
	public static int getCount() {
		// may not be accurate
		return allocatedPorts.size();
	}

	/**
	 * Returns a "pretty" string of the port base and ceiling.
	 * 
	 * @return port range string
	 */
	public static String getRange() {
		return String.format("%d-%d", rtpPortBase, rtpPortCeiling);
	}

	/**
	 * Clear out ports that have closed without having been deallocated.
	 */
	public static void cleanAllocations() {
		allocatedPorts.forEach(port -> {
			if (checkAvailable(port)) {
				clearRTPServerPort(port);
			}
			try {
				Thread.sleep(10L);
			} catch (Exception e) {
				// no-op
			}
		});
	}

}
