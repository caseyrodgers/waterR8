package com.waterR8.model;

public class SeqHit {
	
	private Sensor sensor;
	private int hopCnt;
	private int rssiRcv;

	public SeqHit() {}
	
	public SeqHit(Sensor sensor, int hopCnt, int rssiRcv) {
		this.sensor = sensor;
		this.hopCnt = hopCnt;
		this.rssiRcv = rssiRcv;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public int getHopCnt() {
		return hopCnt;
	}

	public void setHopCnt(int hopCnt) {
		this.hopCnt = hopCnt;
	}

	public int getRssiRcv() {
		return rssiRcv;
	}

	public void setRssiRcv(int rssiRcv) {
		this.rssiRcv = rssiRcv;
	}
	

}
