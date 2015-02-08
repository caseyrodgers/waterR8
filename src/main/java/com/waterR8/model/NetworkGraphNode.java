package com.waterR8.model;

public class NetworkGraphNode  {
	
	private static int __seq; 
	public static enum Type{
		COMPANY, COMPLEX, ROOT, UNIT, SENSOR};
	private Type type;
	private String label;
	private int id;
	private int key = (++__seq);
	String subLabel="";
	
	public NetworkGraphNode() {}
	
	public NetworkGraphNode(Type type, int id, String label) {
		this.type = type;
		this.id = id;
		this.label = label;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public int getKey() {
		return key;
	}


	public void setKey(int key) {
		this.key = key;
	}

	public void setSubLabel(String subLabel) {
		this.subLabel = subLabel;
	}
	
	public String getSubLabel() {
		return this.subLabel;
	}
}
