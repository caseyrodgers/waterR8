package com.waterR8.model;

public class UnitNode extends NetworkDevice {
	
	public UnitNode() {}
	
	public UnitNode(NetworkDevice parent, Unit unit) {
		super(Role.UNIT.name(), unit.getId(),0,0); 
	}

}
