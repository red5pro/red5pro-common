package com.red5pro.group.webrtc;

import com.red5pro.group.GroupEvent;
import com.red5pro.group.expressions.GroupCompositorAdapter;

/**
 * WebRTC specific expression compositor.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire
 *
 */
public class RTCGroupCompositor extends GroupCompositorAdapter {

	@Override
	public void push(GroupEvent event) {
		// TODO get loudest talkers.
		// TODO handle video.
		// TODO sort Sort Sort
		// TODO distribute.
	}

	@Override
	public void doExpressionEvent(Object event) {
		// TODO future.
	}

}
