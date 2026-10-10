package com.red5pro.security;

/**
 * Enum for various types of authentication.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication">HTTP authentication</a>
 *
 * @author Paul Gregoire
 *
 */
public enum AuthType {

    /** HTTP Basic authentication. */
    Basic,
    /** Bearer-token authentication. */
    Bearer,
    /** HTTP Digest authentication. */
    Digest,
    /** HTTP Origin-Bound Authentication. */
    HOBA,
    /** Mutual authentication. */
    Mutual,
    /** NTLM authentication. */
    NTLM,
    /** AWS Signature Version 4 authentication. */
    AWS4;

}
