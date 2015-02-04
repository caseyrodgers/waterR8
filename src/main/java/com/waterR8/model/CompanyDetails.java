package com.waterR8.model;

import java.util.ArrayList;
import java.util.List;

public class CompanyDetails {
	List<Complex> complexes = new ArrayList<Complex>();
	private Company company;
	
	public CompanyDetails(Company company, List<Complex> complexes) {
		this.company = company;
		this.complexes.addAll(complexes);
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<Complex> getComplexes() {
		return complexes;
	}

	public void setComplexes(List<Complex> complexes) {
		this.complexes = complexes;
	}
	
}
