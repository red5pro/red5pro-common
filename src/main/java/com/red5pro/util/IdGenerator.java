package com.red5pro.util;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.text.RandomStringGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;

/**
 * Id generator.
 * 
 * @author Paul Gregoire
 */
public class IdGenerator {

	private static final DigestRandomGenerator random = new DigestRandomGenerator(new SHA1Digest());

	// random numeric string generator - thread safe and immutable
	public static RandomStringGenerator randomNumericStringGenerator = new RandomStringGenerator.Builder()
			.withinRange('1', '9').build();

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
	 * Generates a random numeric string with a given length.
	 * 
	 * @param length
	 * @return String
	 */
	public static final String generateNumericStringId(int length) {
		return randomNumericStringGenerator.generate(13);
	}

}
