package com.red5pro.cluster.streams;
/**
 * Defines an RTMP path for publishing clients.
 * Port is assumed to be rtmp for RTC and RTSP clients when resolving instances.
 * @author Andy Shaules
 *
 */
public class Ingest {
	/**
	 * Only one version of the stream available.
	 */
	public static int SBR = 0;
	/**
	 * Highest quality of multi bitrate stream variations. Lower quality streams are higher indexes. 
	 */
	public static int MBR_MAIN = 1;
	
	private String host;
	private int port;
	private String context;
	private String name;	
	private int level; 

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevel(){
		return level;
	}	
	public boolean isMBR(){
		return level>SBR;
	}
}
