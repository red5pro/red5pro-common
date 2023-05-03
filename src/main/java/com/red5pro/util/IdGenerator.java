package com.red5pro.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Id generator.
 *
 * @author Paul Gregoire
 */
public class IdGenerator {

    private static Logger log = LoggerFactory.getLogger(IdGenerator.class);

    private static final DigestRandomGenerator random = new DigestRandomGenerator(new SHA1Digest());

    // random numeric string generator - thread safe and immutable
    public static RandomStringGenerator randomNumericStringGenerator = new RandomStringGenerator.Builder().withinRange('1', '9').build();

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            log.warn("Algorithm SHA256 not found, falling back to MD5");
            // use simplified digest
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e1) {
                log.warn("Algorithm MD5 not found, falling back to plain hex");
            }
        }
    }

    /**
     * Returns a cryptographically generated id of 128 bits.
     *
     * @return id
     */
    public static final long generateId() {
        long id = 0;
        // add new seed material from current time
        random.addSeedMaterial(ThreadLocalRandom.current().nextLong());
        // get a new id
        byte[] bytes = new byte[16];
        // get random bytes
        random.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {
            id += ((long) bytes[i] & 0xffL) << (8 * i);
        }
        // System.out.println("Id: " + id);
        return id;
    }

    /**
     * Returns a cryptographically generated id.
     *
     * @param bits
     *            maximum number of bits to use
     * @return id
     */
    public static final long generateId(int bits) {
        long id = 0;
        // don't allow bogus bits param (8 bits minimum)
        if (bits < 8) {
            bits = 8;
        }
        // add new seed material from current time
        random.addSeedMaterial(ThreadLocalRandom.current().nextLong());
        // get a new id
        byte[] bytes = new byte[Math.abs(bits / 8)];
        // get random bytes
        random.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {
            id += ((long) bytes[i] & 0xffL) << (8 * i);
        }
        // System.out.println("Id: " + id);
        return id;
    }

    /**
     * Generates a random numeric string with a default length of 13.
     *
     * @return String
     */
    public static final String generateNumericStringId() {
        return randomNumericStringGenerator.generate(13);
    }

    /**
     * Generates a random numeric string with a given length.
     *
     * @param length
     * @return String
     */
    public static final String generateNumericStringId(int length) {
        // range only allows up to 1024 chars to prevent abuse
        return randomNumericStringGenerator.generate(Math.min(length, 1024));
    }

    /**
     * Generate a UUID with source bytes from UUID.randomUUID().
     *
     * @return hashed localized UUID
     */
    public static String generateSha256UUID() {
        try {
            String uuid = UUID.randomUUID().toString();
            return (md != null) ? new DigestUtils(md).digestAsHex(uuid) : Hex.toHexString(uuid.getBytes());
        } finally {
            // md is not thread-safe so be aware of it!
            if (md != null) {
                // reset so we can use the digest
                md.reset();
            }
        }
    }

    /**
     * Generate a UUID with source bytes from a local MAC address and system time.
     *
     * @return hashed localized UUID
     */
    public static String generateLocalizedUUID() {
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            byte[] mac = network.getHardwareAddress();
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                log.debug("MAC {} bytes: {}", sb.toString(), Hex.toHexString(mac));
            }
            ByteBuffer uuid = ByteBuffer.allocate(mac.length + 8);
            uuid.put(mac);
            uuid.putLong(System.currentTimeMillis());
            uuid.flip();
            return (md != null) ? new DigestUtils(md).digestAsHex(uuid.array()) : Hex.toHexString(uuid.array());
        } catch (Exception e) {
            log.warn("Exception getting MAC address, falling back to random UUID");
            return generateSha256UUID();
        } finally {
            // md is not thread-safe so be aware of it!
            if (md != null) {
                // reset so we can use the digest
                md.reset();
            }
        }
    }

}
