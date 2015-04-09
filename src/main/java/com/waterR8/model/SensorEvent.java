package com.waterR8.model;

public class SensorEvent {
	
	private EventType type;
	private SensorRecord data;
	public SensorEvent() {}
	
	public enum EventType {LONG_DURATION};
	public SensorEvent(EventType type, SensorRecord data) {
		this.type = type;
		this.data = data;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public SensorRecord getData() {
		return data;
	}
	public void setData(SensorRecord data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SensorEvent [type=" + type + ", data=" + data + "]";
	}

}
