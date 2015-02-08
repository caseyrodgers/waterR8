package com.waterR8.model;

public class NetworkNode {
	
	private NetworkGraphNode deviceParent;
	private NetworkGraphNode deviceChild;

	public NetworkNode() {}
	
	public NetworkNode(NetworkGraphNode deviceParent, NetworkGraphNode deviceChild) {
		this();
		this.deviceParent = deviceParent;
		this.deviceChild = deviceChild;
	}

	public NetworkGraphNode getDeviceParent() {
		return deviceParent;
	}

	public void setDeviceParent(NetworkGraphNode deviceParent) {
		this.deviceParent = deviceParent;
	}

	public NetworkGraphNode getDeviceChild() {
		return deviceChild;
	}

	public void setDeviceChild(NetworkGraphNode deviceChild) {
		this.deviceChild = deviceChild;
	}

}
