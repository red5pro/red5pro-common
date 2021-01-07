package com.red5pro.interstitial.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Handle Interstitial requests. The InterstitialREST servlet delegates
 * interstitial insert calls to the registered list of handlers (@see
 * IInterstitialRequestHandler.handlers, below). Every call is delegated to
 * every handler.
 *
 * @author Andy
 * @author Nate Roe
 */
public interface IInterstitialRequestHandler {
    /**
     * Static collection of registered handlers
     */
    static final CopyOnWriteArraySet<IInterstitialRequestHandler> handlers = new CopyOnWriteArraySet<>();

    /**
     * Handle request for new interstitial insert(s).
     *
     * @param user
     *            from InterstitialRequest
     * @param digest
     *            from InterstitialRequest
     * @param inserts
     *            the interstitial inserts
     * @throws InterstitialException
     *             if an exception occurs
     */
    void newRequest(String user, String digest, List<InterstitialInsert> inserts) throws InterstitialException;

    /**
     * Handle request to end an insert and return to original stream.
     *
     * @param user
     *            from InterstitialRequest
     * @param digest
     *            from InterstitialRequest
     * @param path
     *            the path to the original stream
     * @throws InterstitialException
     *             if an exception occurs
     */
    void resume(String user, String digest, String path) throws InterstitialException;
}
