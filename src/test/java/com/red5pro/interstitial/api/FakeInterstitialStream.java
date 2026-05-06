package com.red5pro.interstitial.api;

import java.util.ArrayList;
import java.util.List;

import org.red5.codec.IStreamCodecInfo;
import org.red5.server.api.event.IEvent;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.net.rtmp.event.IRTMPEvent;

import com.red5pro.override.api.ProStreamTerminationEventListener;

/**
 * Test double for IInterstitialStream. Captures dispatched IRTMPEvents and
 * provides settable fields for codec info and creation time.
 */
class FakeInterstitialStream implements IInterstitialStream {

    final List<IRTMPEvent> dispatched = new ArrayList<>();

    private final String publishName;

    private long creationTime = 0L;

    private IStreamCodecInfo codecInfo;

    FakeInterstitialStream(String publishName) {
        this.publishName = publishName;
    }

    @Override
    public void dispatchInterstitial(IEvent event) {
        if (event instanceof IRTMPEvent) {
            dispatched.add((IRTMPEvent) event);
        }
    }

    @Override
    public void addTerminationEventListener(ProStreamTerminationEventListener terminationListener) {
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public IStreamCodecInfo getCodecInfo() {
        return codecInfo;
    }

    void setCodecInfo(IStreamCodecInfo codecInfo) {
        this.codecInfo = codecInfo;
    }

    @Override
    public void addStreamListener(IStreamListener streamListener) {
    }

    @Override
    public void removeStreamListener(IStreamListener streamListener) {
    }

    @Override
    public IInterstitialEngine getInterstitialEngine() {
        return null;
    }

    @Override
    public String getBroadcastStreamPublishName() {
        return publishName;
    }

}
