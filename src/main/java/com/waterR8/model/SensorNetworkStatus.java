package com.waterR8.model;

public class SensorNetworkStatus {
	
	private int eventCount;

	public SensorNetworkStatus() {}
	
	public SensorNetworkStatus(int eventCount) {
		this.eventCount = eventCount;
	}

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}
}
