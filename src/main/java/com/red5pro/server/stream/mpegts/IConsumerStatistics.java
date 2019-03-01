package com.red5pro.server.stream.mpegts;

import java.util.concurrent.CopyOnWriteArraySet;
/**
 * Use to receive HLS player stats.
 * 
 * @author Andy Shaules
 *
 */
public interface IConsumerStatistics {
	static final CopyOnWriteArraySet<IConsumerStatistics> listeners = new CopyOnWriteArraySet<>();
	/**
	 * Called after client session stops requesting segments and play lists.
	 * 
	 * @param remoteAddress
	 *            host and port
	 * @param path
	 *            stream context path
	 * @param name
	 *            stream name
	 * @param bytes
	 *            total bytes
	 * @param seconds
	 *            total duration of stream segments downloaded.
	 */
	public void receiveStats(String remoteAddress, String path, String name, long bytes, double seconds);

}
