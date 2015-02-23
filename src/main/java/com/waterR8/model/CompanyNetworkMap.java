package com.waterR8.model;

import java.util.ArrayList;
import java.util.List;

public class CompanyNetworkMap {
	
	List<NetworkNode> networkNodes = new ArrayList<NetworkNode>();
	List<SequenceInfo> sequenceNumbers = new ArrayList<SequenceInfo>();
	List<Sensor> allRepeaters = new ArrayList<Sensor>();
	int currentSequence;
	
	public CompanyNetworkMap() {}
	
	public CompanyNetworkMap(List<NetworkNode> networkNodes, List<Sensor> allRepeaters, List<SequenceInfo> sequenceNumbers,int currentSequence) {
		this.networkNodes = networkNodes;
		this.allRepeaters = allRepeaters;
		this.sequenceNumbers = sequenceNumbers;
		this.currentSequence = currentSequence;
	}

	public List<Sensor> getAllRepeaters() {
		return allRepeaters;
	}

	public void setAllRepeaters(List<Sensor> allRepeaters) {
		this.allRepeaters = allRepeaters;
	}

	public int getCurrentSequence() {
		return currentSequence;
	}

	public void setCurrentSequence(int currentSequence) {
		this.currentSequence = currentSequence;
	}

	public List<NetworkNode> getNetworkNodes() {
		return networkNodes;
	}

	public void setNetworkNodes(List<NetworkNode> networkNodes) {
		this.networkNodes = networkNodes;
	}

	public List<SequenceInfo> getSequenceNumbers() {
		return sequenceNumbers;
	}

	public void setSequenceNumbers(List<SequenceInfo> sequenceNumbers) {
		this.sequenceNumbers = sequenceNumbers;
	}
}
