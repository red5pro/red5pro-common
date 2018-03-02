package com.red5pro.cluster.streams;

import java.util.List;

public class Provisioning {
	
	private String guid;
	
	private List<Provision> streams;
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public List<Provision> getStreams() {
		return streams;
	}
	public void setStreams(List<Provision> streams) {
		this.streams = streams;
	} 
}
