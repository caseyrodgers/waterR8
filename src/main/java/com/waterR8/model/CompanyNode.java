package com.waterR8.model;

public class CompanyNode extends NetworkDevice {
	
	public CompanyNode() {}
	
	public CompanyNode(Company company) {
		super(Role.COMPANY.name(),company.getId(),0,null);
	}

}
