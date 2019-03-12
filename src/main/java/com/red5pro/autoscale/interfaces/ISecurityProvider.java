package com.red5pro.autoscale.interfaces;

import java.security.NoSuchAlgorithmException;

public interface ISecurityProvider {

	boolean verifySignature(String signature, String action, String content, long timestamp);

	String generateSignature(String action, String content, long timestamp, String secret, Object... others)
			throws NoSuchAlgorithmException;

}
