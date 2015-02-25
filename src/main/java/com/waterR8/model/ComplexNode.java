package com.waterR8.model;

public class ComplexNode extends NetworkDevice {
	
	public ComplexNode() {}
	
	public ComplexNode(NetworkDevice parent, Complex complex) {
		super(Role.COMPLEX.name(), complex.getId(),0,0); 
	}

}
