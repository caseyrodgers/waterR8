package com.waterR8.model;

public class RepeaterInfo {

	private Sensor sensor;
	private int seq;
	private int first;
	private int hopCnt;
	private int rssiRcv;
	int respondPercent;

	public RepeaterInfo() {
	}

	public RepeaterInfo(Sensor sensor, int seq, int hopCnt, int first, int rssiRcv, int respondPercent) {
		this.sensor = sensor;
		this.seq = seq;
		this.hopCnt = hopCnt;
		this.first = first;
		this.rssiRcv = rssiRcv;
		this.respondPercent = respondPercent;
	}

	public int getRespondPercent() {
		return respondPercent;
	}

	public void setRespondPercent(int respondPercent) {
		this.respondPercent = respondPercent;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
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

	public void setRssiRcv(int rssRcv) {
		this.rssiRcv = rssRcv;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
}
