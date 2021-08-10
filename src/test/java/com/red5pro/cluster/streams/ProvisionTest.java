package com.red5pro.cluster.streams;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.red5pro.util.ProvisionAdapter;

public class ProvisionTest {

    private static Logger log = LoggerFactory.getLogger(ProvisionTest.class);

    static String groupProv = "{\"guid\": \"live/conference1\"," + "      \"context\": \"live/conference1\"," + "      \"name\": \"conference1\"," + "      \"level\": 0," + "      \"isRestricted\": false," + "      \"restrictions\": []," + "      \"primaries\": []," + "      \"secondaries\": []," + "      \"parameters\": {" + "        \"group\": true," + "        \"audiotracks\": 3," + "        \"videotracks\": 1"
            + "      }" + "    }";

    static String floatProv = "{\"guid\": \"live/conference1\"," + "      \"context\": \"live/conference1\"," + "      \"name\": \"conference1\"," + "      \"level\": 1.0," + "      \"isRestricted\": false," + "      \"restrictions\": []," + "      \"primaries\": []," + "      \"secondaries\": []," + "      \"parameters\": {" + "        \"width\": 320.0," + "        \"audiotracks\": 3," + "        \"videotracks\": 1"
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

        Provision floats = Provision.buildFromJson(floatProv);
        ProvisionAdapter ad = new ProvisionAdapter();

        //float on level input
        JsonObject je = ad.serialize(floats, null, null).getAsJsonObject();
        @SuppressWarnings("unused")
        Provision newprov = Provision.getGson().fromJson(je, Provision.class);

        //int parsed to double.(for adapter)
        je.addProperty("level", 1);

        int val = Double.valueOf(je.get("level").getAsString()).intValue();
        assertTrue(val == 1);
        newprov = Provision.getGson().fromJson(je, Provision.class);

        //string input
        je.addProperty("level", "1.0");

        val = Double.valueOf(je.get("level").getAsString()).intValue();
        assertTrue(val == 1);
        newprov = Provision.getGson().fromJson(je, Provision.class);

        log.info("Deserialized: {}", floats);
    }

}
