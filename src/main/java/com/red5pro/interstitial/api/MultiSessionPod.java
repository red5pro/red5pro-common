package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a container for multiple interstitial sessions. It will queue
 * and process each session in order until all sessions have been processed.
 *
 * @author Paul Gregoire
 */
public class MultiSessionPod extends InterstitialSession {

    private static Logger log = LoggerFactory.getLogger(MultiSessionPod.class);

    private LinkedList<InterstitialSession> sessions = new LinkedList<>();

    private InterstitialSession currentSession;

    /**
     * Creates an empty pod.
     *
     * @param isForwardAudio whether audio is forwarded
     * @param isForwardVideo whether video is forwarded
     */
    public MultiSessionPod(boolean isForwardAudio, boolean isForwardVideo) {
        super(isForwardAudio, isForwardVideo);
    }

    /**
     * Creates a pod pre-populated with sessions, processed in the given order.
     *
     * @param isForwardAudio whether audio is forwarded
     * @param isForwardVideo whether video is forwarded
     * @param sessions the sessions to queue
     */
    public MultiSessionPod(boolean isForwardAudio, boolean isForwardVideo, InterstitialSession... sessions) {
        this(isForwardAudio, isForwardVideo);
        this.sessions.addAll(Arrays.asList(sessions));
    }

    /**
     * Appends a session to the end of the pod's queue.
     *
     * @param session the session to add
     * @return true if the session was added
     */
    public boolean addSession(InterstitialSession session) {
        return sessions.add(session);
    }

    @Override
    public void queue() {
        log.debug("Queueing next session in pod");
        currentSession = sessions.poll();
        if (currentSession != null) {
            currentSession.queue();
        }
    }

    @Override
    public void open() throws Exception {
        log.debug("Opening next session in pod: {}", currentSession);
        if (currentSession != null) {
            currentSession.open();
        }
    }

    @Override
    public void process(long timestamp, IRTMPEvent event, IInterstitialStream output) throws IOException {
        log.debug("Processing next session in pod: {} timestamp: {} output: {}", currentSession, timestamp, output != null ? output.getBroadcastStreamPublishName() : "null");
        if (currentSession != null) {
            currentSession.process(timestamp, event, output);
        }
    }

    @Override
    public void dispose() {
        log.debug("Disposing next session in pod: {}", currentSession);
        if (currentSession != null) {
            currentSession.dispose();
        }
    }

}
