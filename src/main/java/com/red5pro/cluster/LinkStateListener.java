package com.red5pro.cluster;

/**
 * Used to be notified of cluster link states. 
 * @author Andy Shaules
 *
 */
public interface LinkStateListener{
	void originAck(String origin);		
	void originClose(String host);
	void edgeClose(String host);
}
