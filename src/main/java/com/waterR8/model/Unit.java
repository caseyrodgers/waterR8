package com.waterR8.model;

public class Unit {
	int id;
	int complex;
	String unitNumber;
	String type;
	int beds;
	int tenants;

	public Unit() {}
	
	public Unit(int id, int complex, String unitNumber, String type, int beds, int tenants) {
		this.id = id;
		this.complex = complex;
		this.unitNumber = unitNumber;
		this.type = type;
		this.beds = beds;
		this.tenants = tenants;
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
