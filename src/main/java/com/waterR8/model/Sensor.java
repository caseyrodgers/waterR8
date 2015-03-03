package com.waterR8.model;

public class Sensor extends NetworkDevice {
	public Sensor(int id, int unit, int role, int sensorAddress) {
		super(role,id, unit, sensorAddress);
		
		setSensorHex(sensorHex);
	}
}
