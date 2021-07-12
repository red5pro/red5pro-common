package com.red5pro.media.sdp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * Parameter and parsing for SRTP configuration parameters.
 *
 * @author David Heimann
 */
public class CryptographyParam {

    private static Logger log = LoggerFactory.getLogger(CryptographyParam.class);

    public CryptoSuite streamCipherType = CryptoSuite.NONE;

    public CryptoSuite streamAuthType = CryptoSuite.NONE;

    public byte[] rsaKeyHash;

    public CryptoSuite keyHashFunction = CryptoSuite.NONE;

    public boolean parseSDPLine(String line) {
        // expecting: a=crypto:1 AES_CM_128_HMAC_SHA1_32 inline:<hash>|SHA1
        if (line != null) {
            String[] split = line.split(" ");
            if (split.length < 3) {
                log.warn("SRTP SDP parse failed - not enough components");
                return false;
            }
            // Find Cipher type (we only support one)
            if (split[1].contains(CryptoSuite.AESCM128.toString())) {
                streamCipherType = CryptoSuite.AESCM128;
            }
            // Find Authentication type (we only support one)[required]
            if (split[1].contains(CryptoSuite.HMACSHA132.toString())) {
                streamAuthType = CryptoSuite.HMACSHA132;
            } else {
                log.warn("SRTP SDP parse failed - not using auth");
                return false;
            }
            // Get the hash[required] and its method[required]
            split = split[2].split(":", 2)[1].split("\\|");
            if (split.length < 2) {
                log.warn("SRTP SDP parse failed - inline info malformed; attribute line: {}", line);
                return false;
            }
            if (split[1].contains(CryptoSuite.SHA1.toString())) {
                keyHashFunction = CryptoSuite.SHA1;
            } else {
                log.debug("SRTP SDP parse failed - no key hash method");
                return false;
            }
            try {
                rsaKeyHash = Base64.getDecoder().decode(split[0]);
            } catch (Exception e) {
                log.warn("SRTP SDP parse failed - hash isn't in base64");
                return false;
            }
            if (log.isDebugEnabled()) {
                log.debug("Parsed: {} into {}", line, this);
            }
            return true;
        }
        log.warn("SRTP SDP parse failed due to null line");
        return false;
    }

    public String toString() {
        StringBuilder out = new StringBuilder("1 ");
        if (streamCipherType != CryptoSuite.NONE) {
            out = out.append(streamCipherType.toString()).append("_");
        }
        return out.append(streamAuthType.toString()).append(" inline:").append(Base64.getEncoder().encodeToString(rsaKeyHash)).append("|").append(keyHashFunction.toString()).toString();
    }

}
