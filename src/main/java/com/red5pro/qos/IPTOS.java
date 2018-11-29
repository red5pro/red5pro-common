package com.red5pro.qos;

public enum IPTOS {

	IPTOS_LOWCOST(0x02), IPTOS_RELIABILITY(0x04), IPTOS_THROUGHPUT(0x08), IPTOS_LOWDELAY(0x10);

	private final int id;

	private IPTOS(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}
