package com.red5pro.server.stream;

import java.util.concurrent.CopyOnWriteArraySet;

public interface IClientReportReceiver {

	static final CopyOnWriteArraySet<IClientReportReceiver> receivers = new CopyOnWriteArraySet<>();

	/**
	 * 
	 * @param fourCC
	 *            IoSessionAware foucCC
	 * @param timestamp
	 *            time stamp
	 * @param info
	 *            information
	 */
	public void logReport(int fourCC, long timestamp, String info);

}
