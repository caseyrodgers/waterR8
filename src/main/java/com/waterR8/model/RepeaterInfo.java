package com.waterR8.model;

public class RepeaterInfo {
	
	private Sensor sensor;
	private int seq;
	private boolean inSequence;

	public RepeaterInfo() {}
	
	public RepeaterInfo(Sensor sensor, int seq, boolean inSequence) {
		this.sensor = sensor;
		this.seq = seq;
		this.inSequence = inSequence;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public boolean isInSequence() {
		return inSequence;
	}

	public void setInSequence(boolean inSequence) {
		this.inSequence = inSequence;
	}
}
