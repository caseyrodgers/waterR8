package com.waterR8.model;

public class NetworkDevice {
	
	public static enum Role{
		SENSOR("Sensor"), REPEATER("Repeater"), COMPANY("Company"), COMPLEX("Complex"), UNIT("Unit");
	
		private String label;

		private Role(String name) {
			this.label = name;
		}
		
		public String getLabel() {
			return this.label;
		}
	}
	
	int id;
	int unit;
	String role;
	String sensor;
	
	public NetworkDevice() {}
		
	protected NetworkDevice(String role, int id,int unit, String sensor) {
		this.role = role;
		this.id = id;
		this.unit = unit;
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
