package com.red5pro.cluster.streams;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisionTest {

    private static Logger log = LoggerFactory.getLogger(ProvisionTest.class);

    static String groupProv = "{\"guid\": \"live/conference1\"," + "      \"context\": \"live/conference1\"," + "      \"name\": \"conference1\"," + "      \"level\": 0," + "      \"isRestricted\": false," + "      \"restrictions\": []," + "      \"primaries\": []," + "      \"secondaries\": []," + "      \"parameters\": {" + "        \"group\": true," + "        \"audiotracks\": 3," + "        \"videotracks\": 1"
            + "      }" + "    }";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGroup() {
        Provision prov = Provision.buildFromJson(groupProv);
        log.info("Deserialized: {}", prov);
        assertNotNull(prov.getStreamName());
        assertTrue((Boolean) prov.getParameters().get("group"));
    }

}
