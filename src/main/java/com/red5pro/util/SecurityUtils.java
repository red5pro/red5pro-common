package com.red5pro.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

	public static String generateSignature(String algorithm, String action, String content, long timestamp,
			String secret, Object... others) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance(algorithm);
		String message = action + content + timestamp + secret;

		for (Object param : others) {
			message += String.valueOf(param);
		}

		byte[] dataBytes = message.getBytes();
		md.update(dataBytes);

		byte[] mdbytes = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
		}

		return hexString.toString();
	}

	public static boolean verifySignature(String signature, String algorithm, String action, String content,
			long timestamp, String secret) throws NoSuchAlgorithmException {
		String expected = generateSignature(algorithm, action, content, timestamp, secret);
		return (expected.equalsIgnoreCase(signature));
	}

}
