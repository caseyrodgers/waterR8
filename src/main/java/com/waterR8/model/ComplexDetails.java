package com.waterR8.model;

import java.util.ArrayList;
import java.util.List;

public class ComplexDetails {
	
	Company company;
	Complex complex;
	List<Unit> units = new ArrayList<Unit>();
	
	public ComplexDetails() {}
	public ComplexDetails(Complex complex, List<Unit> units) {
		this.complex = complex;
		this.units.addAll(units);
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
	public List<Unit> getUnits() {
		return units;
	}
	public void setUnits(List<Unit> units) {
		this.units = units;
	}
	
}
