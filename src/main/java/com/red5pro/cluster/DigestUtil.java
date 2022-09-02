package com.red5pro.cluster;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for digest creation
 *
 * @author Nate Roe
 */
public class DigestUtil {
    private static final Logger log = LoggerFactory.getLogger(DigestUtil.class);

    /**
     * Create an SHA-256 signature hash from the given info. The input fields are concatenated to compute
     * the digest: action + content + timestamp + clusterPassword.
     *
     * @param action
     * @param content often empty string ""
     * @param timestamp
     * @param password cluster password
     * @return
     */
    public static String getSignature(String action, String content, long timestamp, String password) {
        try {
            String clusterPassword = password;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String message = action + content + timestamp + clusterPassword;
            byte[] dataBytes = message.getBytes();
            md.update(dataBytes);

            byte[] mdbytes = md.digest();

            //convert the byte to hex format method
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(String.format("%02X", (0xFF & mdbytes[i])));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.warn("Exception", e);
        }
        return null;
    }
}
