package com.waterR8.gwt.event_list.client.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents the base data record as transmitted by the gateway
 * 
 * 
 * 
 * @author casey
 * 
 */
public class WaterEvent implements Serializable {

	/** The unique event record id */
	private int id;
	
	
	/** Timestamp of the Water-Event, when the RF data reached the Gateway */
	private Date timeStamp;

	/**
	 * This is the Device ID number listed on the barcode on the side of each
	 * Flow-Timer unit.
	 */
	private String uniqueDevId;

	/**
	 * Each Flow-Timer, Repeater, and Gateway maintains its own packet counter
	 * that increments on each radio transmission
	 */
	private int eventId;

	/**
	 * This field contains the overall duration of the Water-Event in 1/10th of
	 * second resolution.
	 */
	private double duration;
	
	/** This field contains the battery voltage when the Water-Event occurred. The voltage is measured in millivolts so the number must be scaled by 1000. (0x0b96 = 2.966 Volts) */  
	private double batteryVolts;
	
	
	/** This field indicates the Rf signal-strength of the received message in Decibels (dB), and is always a negative number. 
	 * Values in the -30dB range indicate very close RF reception. Values in the range of -90dB or lower indicate that a repeater 
	 * may be needed to ensure reception. (0xb7 = -73dB)Hour */
	private int rsRssi;

	public WaterEvent() {}
	
	public WaterEvent(int id, Date timeStamp, String uniqueDevId, int eventId,double duration, double batteryVolts, int rsRssi) {
		this.id = id;
		this.timeStamp = timeStamp;
		this.uniqueDevId = uniqueDevId;
		this.eventId = eventId;
		this.duration = duration;
		this.batteryVolts = batteryVolts;
		this.rsRssi = rsRssi;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRsRssi() {
		return rsRssi;
	}

	public void setRsRssi(int rsRssi) {
		this.rsRssi = rsRssi;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUniqueDevId() {
		return uniqueDevId;
	}

	public void setUniqueDevId(String uniqueDevId) {
		this.uniqueDevId = uniqueDevId;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getBatteryVolts() {
		return batteryVolts;
	}

	public void setBatteryVolts(double batteryVolts) {
		this.batteryVolts = batteryVolts;
	}

	public int getRfRssi() {
		return rsRssi;
	}

	public void setRfRssi(int rsRssi) {
		this.rsRssi = rsRssi;
	}

	@Override
	public String toString() {
		return "DataRecord [id=" + id + ", timeStamp=" + timeStamp
				+ ", uniqueDevId=" + uniqueDevId + ", eventId=" + eventId
				+ ", duration=" + duration + ", batteryVolts=" + batteryVolts
				+ ", rsRssi=" + rsRssi + "]";
	}

}
