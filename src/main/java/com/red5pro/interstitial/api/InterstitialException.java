package com.red5pro.interstitial.api;

/**
 * Interstitial-specific exception
 *
 * @author Andy
 * @author Nate Roe
 */
public class InterstitialException extends Exception {
    private static final long serialVersionUID = 4290284386325006776L;

    public InterstitialException(String msg) {
        super(msg);
    }
}
