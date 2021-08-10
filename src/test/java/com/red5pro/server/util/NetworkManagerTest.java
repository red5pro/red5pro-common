package com.red5pro.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.server.util.NetworkManager.TopologyMode;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NetworkManagerTest {

    private static Logger log = LoggerFactory.getLogger(NetworkManagerTest.class);

    @Before
    public void setUp() throws Exception {
        System.setProperty("red5.config_root", "/home/mondain/workspace/github-red5pro/common/red5pro-common/target/test-classes/conf");
    }

    @After
    public void tearDown() throws Exception {
        NetworkManager.reset();
    }

    @Test
    public void testGetDefaultTopologyAddresses() {
        log.info("\n testGetDefaultTopologyAddresses");
        String localIP = NetworkManager.getLocalAddress();
        log.info("Local IP: {}", localIP);
        assertNotNull(localIP);
        assertNotEquals("no-ip", localIP);
        String publicIP = NetworkManager.getPublicAddress();
        log.info("Public IP: {}", publicIP);
        assertNotNull(publicIP);
        assertNotEquals("no-ip", publicIP);
    }

    @Test
    public void testGetPropertiesAddress() {
        log.info("\n testGetPropertiesAddress");
        NetworkManager.setTopologyMode(TopologyMode.PROPERTIES);
        String localIP = NetworkManager.getLocalAddress();
        log.info("Local IP: {}", localIP);
        assertNotNull(localIP);
        // ip can be 1.1.1.1 if set via env props as LOCAL_IP
        // or if read from network.properties file it'd be 10.0.0.36
        if (System.getenv("LOCAL_IP") != null) {
            assertEquals("1.1.1.1", localIP);
        } else {
            assertEquals("10.0.0.36", localIP);
        }
        String publicIP = NetworkManager.getPublicAddress();
        log.info("Public IP: {}", publicIP);
        assertNotNull(publicIP);
        // ip can be 1.1.1.1 if set via env props as PUBLIC_IP
        // or if read from network.properties file it'd be 71.38.180.149
        if (System.getenv("PUBLIC_IP") != null) {
            assertEquals("1.1.1.1", publicIP);
        } else {
            assertEquals("71.38.180.149", publicIP);
        }
    }

    @Test
    public void testGetWavelengthAddress() {
        log.info("\n testGetWavelengthAddress");
        NetworkManager.setTopologyMode(TopologyMode.WAVELENGTH);
        String localIP = NetworkManager.getLocalAddress();
        log.info("Local IP: {}", localIP);
        assertNull(localIP);
        String publicIP = NetworkManager.getPublicAddress();
        log.info("Public IP: {}", publicIP);
        assertNull(publicIP);
    }

}
