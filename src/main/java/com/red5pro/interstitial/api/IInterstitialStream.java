package com.red5pro.interstitial.api;

import org.red5.server.api.event.IEvent;

public interface IInterstitialStream {
    public void dispatchInterstitial(IEvent event);
}
