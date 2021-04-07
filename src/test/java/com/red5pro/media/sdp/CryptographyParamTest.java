package com.red5pro.media.sdp;

import static org.junit.Assert.*;

import org.junit.Test;

public class CryptographyParamTest {

    @Test
    public void testParseSDPLine() {
        String test1 = "a=crypto:1 AES_CM_128_HMAC_SHA1_32 inline:a94uHpX0EdzQSd6y/Xic0Iz5aaQ=|SHA1";
        String test2 = "crypto:1 AES_CM_128_HMAC_SHA1_32 inline:a94uHpX0EdzQSd6y/Xic0Iz5aaQ=|SHA1";
        String test3 = "1 AES_CM_128_HMAC_SHA1_32 inline:a94uHpX0EdzQSd6y/Xic0Iz5aaQ=|SHA1";
        String test4 = "";
        String test5 = null;
        CryptographyParam param = new CryptographyParam();
        assertTrue(param.parseSDPLine(test1));
        assertTrue(param.parseSDPLine(test2));
        assertTrue(param.parseSDPLine(test3));
        assertFalse(param.parseSDPLine(test4));
        assertFalse(param.parseSDPLine(test5));
    }

}
