package com.waterR8.model;

import java.util.ArrayList;
import java.util.List;

public class ContactsDetail {
	
	List<ComplexContact> contacts = new ArrayList<ComplexContact>();
	int companyId;
	int complexId;
	private String complexName;
	
	public ContactsDetail() {}
	
	public ContactsDetail(List<ComplexContact> contacts, int companyId, int complexId, String complexName) {
		this.contacts = contacts;
		this.companyId = companyId;
		this.complexId = complexId;
		this.complexName = complexName;
	}

	public List<ComplexContact> getContacts() {
		return contacts;
	}

	public void setContacts(List<ComplexContact> contacts) {
		this.contacts = contacts;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getComplexId() {
		return complexId;
	}

	public void setComplexId(int complexId) {
		this.complexId = complexId;
	}

}
