package com.red5pro.media.sdp;

/**
 * Cryptographic suite and algorithm names used in SDP crypto attributes.
 */
public enum CryptoSuite {

    /** AES counter mode with a 128 bit key. */
    AESCM128("AES_CM_128"),
    /** HMAC-SHA1 authentication with a 32 bit tag. */
    HMACSHA132("HMAC_SHA1_32"),
    /** SHA1 digest. */
    SHA1("SHA1"),
    /** No cryptography. */
    NONE("none");

    private String value;

    CryptoSuite(String val) {
        this.value = val;
    }

    /**
     * Determines whether the given name matches the name of one of the suite constants.
     *
     * @param test the enum constant name to look for, such as AESCM128
     * @return true if a constant with that name exists, false otherwise
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
