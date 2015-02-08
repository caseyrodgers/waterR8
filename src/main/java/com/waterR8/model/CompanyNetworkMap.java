package com.waterR8.model;

import java.util.ArrayList;
import java.util.List;

public class CompanyNetworkMap {
	
	List<NetworkNode> networkNodes = new ArrayList<NetworkNode>();
	
	public CompanyNetworkMap() {}
	
	public CompanyNetworkMap(List<NetworkNode> sensors) {
		this.networkNodes = sensors;
	}

	public List<NetworkNode> getNetworkNodes() {
		return networkNodes;
	}

	public void setNetworkNodes(List<NetworkNode> networkNodes) {
		this.networkNodes = networkNodes;
	}

}
