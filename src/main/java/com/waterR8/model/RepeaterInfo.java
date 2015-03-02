package com.waterR8.model;

public class RepeaterInfo {

	private Sensor sensor;

	
	private int avgHopCnt;
	private int avgRssiRcv;
	int respondPercent;

	public RepeaterInfo() {
	}

	public RepeaterInfo(Sensor sensor, int avgHopCnt, int avgRssiRcv, int respondPercent) {
		this.sensor = sensor;
		this.avgHopCnt = avgHopCnt;
		
		this.avgRssiRcv = avgRssiRcv;
		this.respondPercent = respondPercent;
	}

	public int getRespondPercent() {
		return respondPercent;
	}

	public void setRespondPercent(int respondPercent) {
		this.respondPercent = respondPercent;
	}

	public int getAvgHopCnt() {
		return avgHopCnt;
	}

	public void setAvgHopCnt(int avgHopCnt) {
		this.avgHopCnt = avgHopCnt;
	}

	public int getAvgRssiRcv() {
		return avgRssiRcv;
	}

	public void setAvgRssiRcv(int avgRssiRcv) {
		this.avgRssiRcv = avgRssiRcv;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
}
