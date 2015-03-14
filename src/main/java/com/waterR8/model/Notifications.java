package com.waterR8.model;

public class Notifications {
	
	private boolean notifyOnError;

	public Notifications() {}
	
	public Notifications(boolean notifyOnError) {
		this.notifyOnError  = notifyOnError;
	}

	public boolean isNotifyOnError() {
		return notifyOnError;
	}

	public void setNotifyOnError(boolean notifyOnError) {
		this.notifyOnError = notifyOnError;
	}
}
