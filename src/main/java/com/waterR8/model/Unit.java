package com.waterR8.model;

public class Unit {
	int id;
	int complex;
	String unitNumber;
	String type;
	int beds;
	int tenants;
	private int eventCount;
	private String lastEvent;
	private int lastRepeaterSeq;

	public Unit() {}
	
	public Unit(int id, int complex, String unitNumber, String type, int beds, int tenants, int eventCount, String lastEvent, int lastRepeaterSeq) {
		this.id = id;
		this.complex = complex;
		this.unitNumber = unitNumber;
		this.type = type;
		this.beds = beds;
		this.tenants = tenants;
		this.eventCount = eventCount;
		this.lastEvent = lastEvent;
		this.lastRepeaterSeq = lastRepeaterSeq;
	}

	public int getLastRepeaterSeq() {
		return lastRepeaterSeq;
	}

	public void setLastRepeaterSeq(int lastRepeaterSeq) {
		this.lastRepeaterSeq = lastRepeaterSeq;
	}

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public String getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(String lastEvent) {
		this.lastEvent = lastEvent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getComplex() {
		return complex;
	}

	public void setComplex(int complex) {
		this.complex = complex;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}

	public int getTenants() {
		return tenants;
	}

	public void setTenants(int tenants) {
		this.tenants = tenants;
	}
}
