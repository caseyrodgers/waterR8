package com.waterR8.model;

import java.util.ArrayList;
import java.util.List;

public class UnitDetails {
	
	Unit unit;
	Company company;
	Complex complex;
	List<NetworkDevice> sensors = new ArrayList<NetworkDevice>();
	SensorNetworkStatus networkStatus;
	
	public UnitDetails() {}
	public UnitDetails(Unit unit) {
		this.unit = unit;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public List<NetworkDevice> getSensors() {
		return sensors;
	}
	public void setSensors(List<NetworkDevice> sensors) {
		this.sensors = sensors;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Complex getComplex() {
		return complex;
	}
	public void setComplex(Complex complex) {
		this.complex = complex;
	}
	public SensorNetworkStatus getNetworkStatus() {
		return networkStatus;
	}
	public void setNetworkStatus(SensorNetworkStatus networkStatus) {
		this.networkStatus = networkStatus;
	}
}
