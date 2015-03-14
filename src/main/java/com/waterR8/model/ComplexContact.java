package com.waterR8.model;

public class ComplexContact {
	
	int complex;
	private String fullName;
	private String phone;
	private String emailAddress;
	private int id;
	
	boolean notifyOnError;

	public ComplexContact() {}
	
	public ComplexContact(int id, int complex, String fullName, String phone, String emailAddress, boolean notifyOnError) {
		this.id = id;
		this.complex = complex;
		this.fullName = fullName;
		this.phone = phone;
		this.emailAddress = emailAddress;
		this.notifyOnError = notifyOnError;
	}

	public boolean isNotifyOnError() {
		return notifyOnError;
	}

	public void setNotifyOnError(boolean notifyOnError) {
		this.notifyOnError = notifyOnError;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
