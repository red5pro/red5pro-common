package com.red5pro.interstitial.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public interface InterstitialRequestHandler {

    static final CopyOnWriteArraySet<InterstitialRequestHandler> handlers = new CopyOnWriteArraySet<>();

    void newRequest(String user, String digest, List<InterstitialInsert> inserts) throws InterstitialException;

    void resume(String user, String digest, String path) throws InterstitialException;

}
