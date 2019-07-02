package com.red5pro.server.stream;

import java.util.concurrent.CopyOnWriteArraySet;

public interface IClientReportReceiver {

	static final CopyOnWriteArraySet<IClientReportReceiver> receivers = new CopyOnWriteArraySet<IClientReportReceiver>();
	/**
	 * 
	 * @param fourCC
	 *            IoSessionAware foucCC
	 * @param timestamp
	 * @param info
	 */
	public void logReport(int fourCC, long timestamp, String info);

}
