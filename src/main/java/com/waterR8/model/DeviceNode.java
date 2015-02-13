package com.waterR8.model;

public class DeviceNode extends NetworkDevice {
	
	public DeviceNode() {}
	
	public DeviceNode(NetworkDevice parent, Unit unit) {
		super(Role.UNIT, unit.getId(),0,null); 
	}

}
