package com.red5pro.interstitial.api;

import org.red5.codec.IStreamCodecInfo;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IStreamListener;

import com.red5pro.override.api.ProStreamTerminationEventListener;

/**
 * Public interface to InterstitialStream.
 *
 * @author Nate Roe
 */
public interface IInterstitialStream {
    /**
     * Dispatch event.
     *
     * @param event
     *            the event to dispatch to the stream
     */
    public void dispatchInterstitial(IEvent event);

    /**
     * Add a stream termination listener for interstitials based on live streams
     *
     * @param terminationListener
     *            the listener
     */
    public void addTerminationEventListener(ProStreamTerminationEventListener terminationListener);

    /**
     * Returns the timestamp at which the stream was created.
     *
     * @return creation timestamp
     */
    public long getCreationTime();

    /**
     * Return codec information.
     *
     * @return Stream codec information
     */
    public IStreamCodecInfo getCodecInfo();

    /**
     * Register a stream listener.
     *
     * @param streamListener
     *            the listener
     */
    public void addStreamListener(IStreamListener streamListener);

    /**
     * Remove a previously registered stream listener.
     *
     * @param streamListener
     *            the listener
     */
    public void removeStreamListener(IStreamListener streamListener);

    /**
     * @return the stream's IInterstitialEngine
     */
    public IInterstitialEngine getInterstitialEngine();
}
