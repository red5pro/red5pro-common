package com.red5pro.server.stream.webrtc;

public interface SessionSourceTableEventHandler {

    void sessionSourceTimeout(int ssrc);

    void sessionInputAbandoned();

    boolean isValidSource(int ssrc);

}
