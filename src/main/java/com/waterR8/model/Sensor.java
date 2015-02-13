package com.waterR8.model;

public class Sensor extends NetworkDevice {
	public Sensor(int id, int unit, Role role, String sensorAddress, String sensorHex) {
		super(role,id, unit, sensorAddress);
		
		setSensorHex(sensorHex);
	}
}
