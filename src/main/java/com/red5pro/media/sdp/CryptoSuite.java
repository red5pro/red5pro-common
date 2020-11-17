package com.red5pro.media.sdp;

public enum CryptoSuite {

	AESCM128("AES_CM_128"), HMACSHA132("HMAC_SHA1_32"), SHA1("SHA1"), NONE("none");

	private String value;

	CryptoSuite(String val) {
		this.value = val;
	}

	/**
	 * @param test
	 * @return
	 */
	public static boolean contains(String test) {
		for (CryptoSuite suite : CryptoSuite.values()) {
			if (suite.name().equals(test)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.value;
	}

}
