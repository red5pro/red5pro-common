package com.red5pro.security;

/**
 * Enum for various types of authentication.
 * 
 * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication
 * 
 * @author Paul Gregoire
 *
 */
public enum AuthType {

    Basic, Bearer, Digest, HOBA, Mutual, NTLM, AWS4;
    
}
