package com.waterR8.model;

public class SensorEvent {
	
	private int id;
	private String json;
	private String src;
	private int seq;
	private int hopCnt;
	private String first;
	private String type;
	private int bat;
	private String timeStamp;
	private long time;

	public SensorEvent() {}
	
	public SensorEvent(int id, String json, String type, String timeStamp, long time, String src, int seq, int hopCnt,String first, int battery) {
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

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
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

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
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
