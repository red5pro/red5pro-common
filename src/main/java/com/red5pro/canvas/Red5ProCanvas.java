package com.red5pro.canvas;

import java.io.IOException;
import java.util.Map;

import org.red5.server.api.scope.IScope;

public interface Red5ProCanvas {
	/**
	 * Set object that will fill buffers.
	 * 
	 * @param provider
	 *            FrameProvider
	 */
	void setFrameProvider(FrameProvider provider);
	/**
	 * 
	 * @return frame provider
	 */
	FrameProvider getFrameProvider();
	/**
	 * 
	 * @param bitrate
	 *            video bitrate.
	 */
	void setBitrate(int bitrate);
	/**
	 * Start encoding engine.
	 */
	void start();
	/**
	 * Start output engine, option 1 local publish. Call after starting encoding
	 * engine.
	 * 
	 * @param scope
	 * @param name
	 * @param record
	 * @param appeand
	 * @throws IOException
	 */
	void loopBack(IScope scope, String name, boolean record, boolean appeand) throws IOException;

	/**
	 * Start output engine option 2 rtmp publish to network. Call after calling
	 * start.
	 * 
	 * @param host
	 * @param port
	 * @param path
	 * @param name
	 * @param params
	 */
	void forward(String host, int port, String path, String name, Map<String, Object> connectParams) throws IOException;
	/**
	 * Stop streaming process.
	 */
	void stop();
}
