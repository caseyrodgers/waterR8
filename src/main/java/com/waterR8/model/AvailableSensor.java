package com.waterR8.model;

public class AvailableSensor {
	int sn;
	int role;
	
	public AvailableSensor() {}
	
	public AvailableSensor(int sn, int role) {
		this.sn = sn;
		this.role = role;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
	
}
