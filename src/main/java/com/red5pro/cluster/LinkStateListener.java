package com.red5pro.cluster;


public interface LinkStateListener{
	void originAck(String origin);		
	void originClose(String host);
	void edgeClose(String host);
}
