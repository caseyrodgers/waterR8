package com.waterR8.model;

public class Sensor {

	int id;
	int unit;
	String role;
	String sensor;

	
	public Sensor() {}
		
	public Sensor(int id, int unit, String role, String sensor) {
		this.id = id;
		this.unit = unit;
		this.role = role;
		this.sensor = sensor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}
	

}
