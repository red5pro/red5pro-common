package com.red5pro.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PortManagerTest {

	private static Logger log = LoggerFactory.getLogger(PortManagerTest.class);

	private DatagramSocket socket;

	private int unavailablePort = 49156;

	@Before
	public void setUp() throws Exception {
		socket = new DatagramSocket(unavailablePort);
		unavailablePort = socket.getLocalPort();
	}

	@After
	public void tearDown() throws Exception {
		if (socket != null) {
			socket.close();
		}
		PortManager.cleanAllocations();
	}

	@Test
	public void testGetRTPServerPortPair() {
		int a = PortManager.getRTPServerPort();
		int b = PortManager.getRTPServerPort();
		int[] ports = new int[]{a, b};
		assertEquals(49152, ports[0]);
		assertEquals(49153, ports[1]);
		a = PortManager.getRTPServerPort();
		b = PortManager.getRTPServerPort();
		ports = new int[]{a, b};
		assertEquals(49154, ports[0]);
		assertEquals(49155, ports[1]);
		// try the unavailable port
		a = PortManager.getRTPServerPort();
		// unavailable + 1
		assertEquals((unavailablePort + 1), a);
		// // no clear the first port and try to re-allocate it
		// PortManager.clearRTPServerPort(49152);
		// a = PortManager.getRTPServerPort();
		// assertEquals(49152, a);
		a = PortManager.getRTPServerPort();
		assertTrue(a > 49155);
	}

	@Test
	public void testPortStayingAllocated() throws SocketException {
		int intitialCount = PortManager.getCount();
		int port = PortManager.getRTPServerPort();
		System.out.println("Port returned: " + port);
		DatagramSocket socket = new DatagramSocket(port);
		assertFalse(PortManager.checkAvailable(port));
		assertEquals(PortManager.getCount(), (intitialCount + 1));
		socket.close();
		PortManager.clearRTPServerPort(port);
		assertTrue(PortManager.checkAvailable(port));
		assertEquals(PortManager.getCount(), intitialCount);
		PortManager.cleanAllocations();
		assertEquals(PortManager.getCount(), 1);
	}

	@Test
	public void testZMaxItOutLinear() throws InterruptedException {
		final int portBase = 49152, portCeiling = 65535;
		// configure range
		PortManager.setRtpPortBase(portBase);
		PortManager.setRtpPortCeiling(portCeiling);
		// counters
		final AtomicInteger hit = new AtomicInteger();
		final AtomicInteger miss = new AtomicInteger();
		final AtomicLong elapsedTime = new AtomicLong();
		//
		int exes = 0, max = 18;
		ExecutorService executor = Executors.newFixedThreadPool(max);
		CountDownLatch latch = new CountDownLatch(max);
		do {
			executor.submit(() -> {
				Instant start = Instant.now();
				for (int i = 0; i < 1000; i++) {
					int port = PortManager.getRTPServerPort();
					if (port >= portBase && port <= portCeiling) {
						// hit
						hit.incrementAndGet();
					} else {
						// miss
						System.out.println("Port miss: " + port);
						miss.incrementAndGet();
					}
				}
				elapsedTime.addAndGet(Instant.now().toEpochMilli() - start.toEpochMilli());
				latch.countDown();
			});
			exes++;
		} while (exes < max);
		try {
			// wait for the latch
			if (latch.await(3L, TimeUnit.MINUTES)) {
				// try to get a port
				int port = PortManager.getRTPServerPort();
				System.out.println("Port: " + port);
				assertTrue(port != -1);
			} else {
				log.warn("Latch timed out");
			}
		} finally {
			executor.shutdown();
		}
		log.info("Linear hits: {} misses: {} total allocated: {} total average time: {}s", hit.get(), miss.get(),
				PortManager.getCount(), ((elapsedTime.get() / max) / 1000L));
	}

	@Test
	public void testZMaxItOutRandom() throws InterruptedException {
		final int portBase = 49152, portCeiling = 65535;
		// configure range
		PortManager.setRtpPortBase(portBase);
		PortManager.setRtpPortCeiling(portCeiling);
		// counters
		final AtomicInteger hit = new AtomicInteger();
		final AtomicInteger miss = new AtomicInteger();
		final AtomicLong elapsedTime = new AtomicLong();
		int exes = 0, max = 18;
		ExecutorService executor = Executors.newFixedThreadPool(max);
		CountDownLatch latch = new CountDownLatch(max);
		do {
			executor.submit(() -> {
				Instant start = Instant.now();
				for (int i = 0; i < 1000; i++) {
					int port = PortManager.getRTPServerPortRandom();
					if (port >= portBase && port <= portCeiling) {
						// hit
						hit.incrementAndGet();
					} else {
						// miss
						miss.incrementAndGet();
					}
				}
				elapsedTime.addAndGet(Instant.now().toEpochMilli() - start.toEpochMilli());
				latch.countDown();
			});
			exes++;
		} while (exes < max);
		try {
			// wait for the latch
			if (latch.await(3L, TimeUnit.MINUTES)) {
				// try to get a port
				int port = PortManager.getRTPServerPortRandom();
				System.out.println("Port: " + port);
				assertTrue(port != -1);
			} else {
				log.warn("Latch timed out");
			}
		} finally {
			executor.shutdown();
		}
		log.info("Random hits: {} misses: {} total allocated: {} total average time: {}s", hit.get(), miss.get(),
				PortManager.getCount(), ((elapsedTime.get() / max) / 1000L));
	}

}
