package com.red5pro.interstitial.api;

import java.util.List;

/**
 * JSON request to insert one or more instertitials, or to resume a stream.
 *
 * @author Nate Roe
 */
public class InterstitialRequest {
    public String user;

    public String digest;

    public List<InterstitialInsert> inserts;

    public String resume;
}
