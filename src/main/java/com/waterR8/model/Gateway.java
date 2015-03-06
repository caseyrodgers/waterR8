package com.waterR8.model;

public class Gateway {
	private int sn;
	private String macAddress;
	private String ipAddress;
	

	public Gateway() {}
	
	public Gateway(int sn, String macAddress, String ipAddress) {
		this.sn = sn;
		this.macAddress = macAddress;
		this.ipAddress = ipAddress;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}


	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
