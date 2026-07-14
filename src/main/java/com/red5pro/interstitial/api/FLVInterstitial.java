package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;

import org.red5.server.api.IContext;
import org.red5.server.api.scope.IScope;
import org.red5.server.messaging.IConsumer;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.BaseEvent;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.stream.IProviderService;
import org.red5.server.stream.ISeekableProvider;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides interstitial source from a single FLV file within a given scope.
 *
 * @author Andy
 */
public class FLVInterstitial extends InterstitialSession implements IConsumer {

    private static Logger log = LoggerFactory.getLogger(FLVInterstitial.class);

    private IScope appScope;

    private long timeStart = -1;

    /**
     * Tags pulled from io but whose timestamps are past the current dispatchTo cutoff. Held for the next process() call so we don't overshoot the live clock.
     */
    private LinkedTransferQueue<IRTMPEvent> pendingEvents = new LinkedTransferQueue<>();

    private boolean hasPackets;

    public FLVInterstitial(IScope appScope, String fileName, boolean isForwardAudio, boolean isForwardVideo) {
        super(isForwardAudio, isForwardVideo);
        this.appScope = appScope;
        this.fileName = fileName;
    }

    @Override
    public void queue() {
    }

    @Override
    public void open() {
        log.debug("Open {}", fileName);
        if (io != null) {
            io.unsubscribe(this);
            io = null;// affirm.
        }
        // in case we get abruptly reopened
        pendingEvents.clear();
        hasPackets = false;
        IContext context = appScope.getContext();
        IProviderService providerService = (IProviderService) context.getBean(IProviderService.BEAN_NAME);
        io = providerService.getVODProviderInput(appScope, fileName);
        if (io == null) {
            log.error("FLV not found");
        } else {
            log.debug("Found FLV");
            io.subscribe(this, new HashMap<String, Object>());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(long timestamp, IRTMPEvent event, IInterstitialStream output) throws IOException {
        if (io == null) {
            throw new IOException("FLV not found");
        }
        // The first timestamp we see in the file becomes the anchor for the stream. We use the first A/V tag to set
        // the anchor for its respective stream, but if the first tag is not A/V, we set both anchors to that timestamp
        // so they track together.
        if (timeStart == -1) {
            log.debug("Setting first timestamp: {}", timestamp);
            // anchor timestamp so a/v track together
            timeStart = timestamp;
            if (log.isDebugEnabled()) {
                log.debug("First tag timestamp: {}", event.getTimestamp());
                byte dataType = event.getDataType();
                if (dataType == BaseEvent.TYPE_AUDIO_DATA) {
                    log.debug("First tag is audio");
                } else if (dataType == BaseEvent.TYPE_VIDEO_DATA) {
                    log.debug("First tag is video");
                }
            }
        }

        if (codec == 0 && (width == 0 || height == 0)) {
            log.warn("FLV has no video and no audio");
            return;
        }

        // Drain any tag held over from the previous call first.
        if (!pendingEvents.isEmpty()) {
            IRTMPEvent pending = pendingEvents.peek();
            long pNow = pending.getTimestamp();
            long pDispatchTo = timestamp - timeStart;
            if (pNow <= pDispatchTo) {
                // This pending tag is past cutoff — dispatch it and loop back to check if the next one is also past cutoff.
                long pStamped = pNow + timeStart;
                warnIfOvershootsLive(pStamped, timestamp, "pending");
                pending.setTimestamp((int) pStamped);
                log.debug("Dispatching pending tag with timestamp: {}", pending.getTimestamp());
                dispatchEvent(pending, false, output);
                // remove the one we just dispatched, and loop back to check if the next one is also ready for dispatch.
                pendingEvents.poll();
            } else {
                // Still past cutoff — keep holding, dispatch the live event and return.
                dispatchEvent(event, true, output);
                return;
            }
        }

        IMessage message;
        while ((message = io.pullMessage()) != null) {
            if (message instanceof RTMPMessage) {
                IRTMPEvent body = ((RTMPMessage) message).getBody();
                if (body != null) {
                    hasPackets = true;
                    log.debug("Pulled tag timestamp: {}", body.getTimestamp());
                    byte dataType = body.getDataType();
                    if (dataType == BaseEvent.TYPE_AUDIO_DATA) {
                        log.debug("Pulled tag is audio");
                        if (codec != 0) {
                            switch (audioCompatibility) {
                                case YES:
                                    break;
                                case UNKNOWN:
                                    AudioInfo info = parseAudioParams((AudioData) body);
                                    audioCompatibility = info.matchesStream;
                                    if (audioCompatibility == AudioCompatibility.YES) {
                                        break;
                                    } else if (isForwardAudio() && audioCompatibility == AudioCompatibility.NO) {
                                        log.error("FLV has incompatible audio. rate: {}  Channels: {}", info.audioSampleRate, info.audioChannels);
                                        throw new IOException("FLV has incompatible audio");
                                    }
                                case NO:
                                    continue;
                            }
                        }
                    } else if (dataType == BaseEvent.TYPE_VIDEO_DATA) {
                        log.debug("Pulled tag is video");
                        if (width == 0 || height == 0) {
                            continue;
                        }
                    } else if (dataType == BaseEvent.TYPE_NOTIFY) {
                        log.debug("Pulled tag is notify / metadata");
                        if (!includeMetaData) {
                            continue;
                        }
                    }
                    long now = body.getTimestamp();
                    long bDispatchTo = timestamp - timeStart;
                    if (now <= bDispatchTo) {
                        long stamped = now + timeStart;
                        warnIfOvershootsLive(stamped, timestamp, "pull");
                        body.setTimestamp((int) stamped);
                        log.debug("Dispatching tag with timestamp: {}", body.getTimestamp());
                        dispatchEvent(body, false, output);
                    } else {
                        // Past cutoff — hold for the next call, loop back to check if the next one is also past cutoff, and dispatch the live event before returning.
                        pendingEvents.offer(body);
                        dispatchEvent(event, true, output);
                        return;
                    }
                } else {
                    log.debug("Pulled message body is null");
                }
            } else {
                log.debug("Pulled message is not RTMPMessage");
            }
        }

        // We got here because dispatchTo limiter has not run out,
        // and duration has not expired,
        // and no more packets to pull.
        // First action is to make sure we dispatch the incoming event tag.
        dispatchEvent(event, true, output);

        // Next action is to determine if our session has expired and if we are expected to loop.
        // If our duration has not expired, next call to process will loop. If looping reset parameters.
        if (!hasPackets) {
            //If the FLV was empty of tags, toss exception.
            throw new IOException("Empty insert");
        }
        if (sessionControl.canLoop()) {
            log.debug("loop file");
            OOBControlMessage oobCtrlMsg = new OOBControlMessage();
            oobCtrlMsg.setTarget(ISeekableProvider.KEY);
            oobCtrlMsg.setServiceName("seek");
            Map<String, Object> paramMap = new HashMap<String, Object>(1);
            paramMap.put("position", 0);
            oobCtrlMsg.setServiceParamMap(paramMap);
            io.sendOOBControlMessage(this, oobCtrlMsg);
            if (oobCtrlMsg.getResult() instanceof Integer) {
                log.debug("rewind {}", oobCtrlMsg.getResult());
            } else {
                open();
            }
            //Reset timestamp delta.
            timeStart = -1;
            pendingEvents.clear(); // any held tag is from the pre-loop run; discard
        } else {
            log.debug("Interstitial complete, timestamp: {}", timestamp);
            dispose();
            this.sessionControl.resumeProgram();
        }
    }

    /**
     * Drift-violation detector: logs WARN if a re-stamped FLV body's dispatched timestamp ever
     * exceeds the current live-clock timestamp passed into {@link #process}. The pending /
     * pull-cutoff guard should make this impossible — if it fires, either the cutoff math drifted
     * or a per-stream anchor was set ahead of the live clock at a pod boundary. Use this signal
     * to pinpoint downstream nonmonotonic-DTS reports back to the FLV interstitial.
     *
     * @param stampedTs    the dispatched timestamp the body is about to carry (raw + anchor)
     * @param liveTs       the engine's live-clock timestamp for this {@code process()} call
     * @param origin       short tag identifying the dispatch site ("pending" / "pull") for the log
     */
    private void warnIfOvershootsLive(long stampedTs, long liveTs, String origin) {
        if (stampedTs > liveTs) {
            log.warn("FLV interstitial overshoot ({}): stampedTs={} liveTs={} delta={}ms file={} timeStart={}", origin, stampedTs, liveTs, stampedTs - liveTs, fileName, timeStart);
        }
    }

    @Override
    public void dispose() {
        log.debug("dispose");
        if (io != null) {
            io.unsubscribe(this);
            io = null;
        }
        hasPackets = false;
        fileName = null;
        pendingEvents.clear();
        fireDisposedCallbackOnce();
    }

    @Override
    public void onOOBControlMessage(IMessageComponent source, IPipe pipe, OOBControlMessage oobCtrlMsg) {
    }
}
