package com.red5pro.cluster;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Used to be notified of cluster link states. 
 * @author Andy Shaules
 *
 */
public interface LinkStateListener{
	
    public static CopyOnWriteArraySet<LinkStateListener> linkListeners = new CopyOnWriteArraySet<>();
	
	void originAck(String origin);		
	void originClose(String host);
	void edgeClose(String host);
}
