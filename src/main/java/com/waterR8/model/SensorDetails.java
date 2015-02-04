package com.waterR8.model;

import java.util.ArrayList;
import java.util.List;

public class SensorDetails {

	Sensor sensor;
	List<SensorEvent> events = new ArrayList<SensorEvent>();
	private Company company;

	public SensorDetails() {}
	
	public SensorDetails(Sensor sensor) {
		this.sensor = sensor;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public List<SensorEvent> getEvents() {
		return events;
	}

	public void setEvents(List<SensorEvent> events) {
		this.events = events;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}
}
