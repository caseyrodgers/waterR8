package com.waterR8.model;

public class SensorEvent {
	
	private int id;
	private String json;
	private int src;
	private int seq;
	private int hopCnt;
	private int first;
	private String type;
	private int bat;
	private String timeStamp;
	private long time;
	private int dur;
	private int rssiRcv;

	public SensorEvent() {}
	
	public SensorEvent(int id, String json, String type, String timeStamp, long time, int src, int seq, int hopCnt,int first, int battery, int duration, int rssiRcv) {
		this.id = id;
		this.json = json;
		this.type = type;
		this.timeStamp = timeStamp;
		this.time = time;
		this.src = src;
		this.seq = seq;
		this.hopCnt = hopCnt;
		this.first = first;
		this.bat = battery;
		this.dur = duration;
		this.rssiRcv = rssiRcv;
	}

	public int getRssiRcv() {
		return rssiRcv;
	}

	public void setRssiRcv(int rssiRcv) {
		this.rssiRcv = rssiRcv;
	}

	public int getDur() {
		return dur;
	}

	public void setDur(int dur) {
		this.dur = dur;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getHopCnt() {
		return hopCnt;
	}

	public void setHopCnt(int hopCnt) {
		this.hopCnt = hopCnt;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getBat() {
		return bat;
	}

	public void setBat(int bat) {
		this.bat = bat;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
		
}
