package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.red5.server.api.IContext;
import org.red5.server.api.scope.IScope;
import org.red5.server.messaging.IConsumer;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageComponent;
import org.red5.server.messaging.IPipe;
import org.red5.server.messaging.OOBControlMessage;
import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.IRTMPEvent;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;
import org.red5.server.stream.IProviderService;
import org.red5.server.stream.ISeekableProvider;
import org.red5.server.stream.message.RTMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides interstitial source from FLV files from within a given
 * scope.
 * 
 * @author Andy
 *
 */
public class FLVInterstitial extends InterstitialSession implements IConsumer {

	private static Logger log = LoggerFactory.getLogger(FLVInterstitial.class);

	private IScope app;

	private boolean firstTimestamp;

	private long timeStart;

	private boolean hasPackets;

	public FLVInterstitial(IScope app, String uri, boolean isForwardAudio, boolean isForwardVideo) {
		super(isForwardAudio, isForwardVideo);

		this.app = app;
		this.uri = uri;
	}

	@Override
	public void queue() {
	}

	@Override
	public void open() {
		log.debug("Open {}", uri);
		if (io != null) {
			io.unsubscribe(this);
			io = null;// affirm.
		}
		// in case we get abruptly reopened.
		hasPackets = false;
		IContext context = app.getContext();
		IProviderService providerService = (IProviderService) context.getBean(IProviderService.BEAN_NAME);
		io = providerService.getVODProviderInput(app, uri);
		if (io == null) {
			log.error("Flv not found");
		} else {
			log.debug("found flv");
			io.subscribe(this, new HashMap<String, Object>());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(long timestamp, IRTMPEvent event, IInterstitialStream output) throws IOException {
		if (io == null) {
			throw new IOException();
		}
		if (!firstTimestamp) {
			// log.debug("setting first ts {}", timestamp);
			firstTimestamp = true;
			timeStart = timestamp;
		}
		long dispatchTo = timestamp - timeStart;

		IMessage message = null;
		if (codec == 0 && (width == 0 || height == 0)) {
			return;
		}
		while ((message = io.pullMessage()) != null) {
			hasPackets = true;
			// log.debug("insert message {}",message);
			if (message instanceof RTMPMessage) {
				IRTMPEvent body = ((RTMPMessage) message).getBody();
				if (body != null) {
					// Packet unused if:
					if (body instanceof Notify && !includeMetaData) {
						continue;// we dont want insert's meta
					} else if (body instanceof AudioData && codec == 0) {
						continue; // live stream has no Audio
					} else if (body instanceof VideoData && (width == 0 || height == 0)) {
						continue;// live stream has no video.
					}

					long now = body.getTimestamp();
					body.setTimestamp((int) (now + timeStart));
					// log.debug("dispatchInterstitial {} {} {}", timestamp, now,now+timeStart);

					// this will dispatch one event or the other
					dispatchEvent(body, false, output);
					dispatchEvent(event, true, output);
					if (now > dispatchTo) {
						// log.debug("segment done {} {} ", now,dispatchTo);
						return;
					}
				}
			}
		}
		// We got here because dispatchTo limiter has not run out,
		// and duration has not expired,
		// and no more packets to pull.
		// So we will do nothing unless we are expected to loop.
		// If our duration has not expired, next call to process will loop.
		if (!hasPackets) {
			throw new IOException("Empty insert");
		}
		if (sessionControl.getLifeCycle() == InterstitialDurationControlType.Indefinite || sessionControl.canLoop()) {
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
			firstTimestamp = false;
		} else {
			log.debug("interstitial complete  {}", timestamp);
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
		uri = null;
	}

	@Override
	public void onOOBControlMessage(IMessageComponent source, IPipe pipe, OOBControlMessage oobCtrlMsg) {
	}
}
