package com.waterR8.model;

/** A single Sequence number and Sensor
 *  and aggregated data.
 *  
 * @author casey
 *
 */
public class SeqHit {
	
	int seq;
	private Sensor sensor;
	private int hopCnt;
	private int rssiRcv;

	public SeqHit() {}
	
	public SeqHit(int seq, Sensor sensor, int hopCnt, int rssiRcv) {
		this.seq = seq;
		this.sensor = sensor;
		this.hopCnt = hopCnt;
		this.rssiRcv = rssiRcv;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
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
