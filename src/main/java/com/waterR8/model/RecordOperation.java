package com.waterR8.model;

public class RecordOperation {
	public static enum CrudType {CREATE, READ,DELETE, UPDATE};
	CrudType crudType;
	private int primaryKey;
	String message;
	public RecordOperation() {}
	public RecordOperation(CrudType crudType, int primaryKey, String message) {
		this.crudType = crudType;
		this.primaryKey = primaryKey;
		this.message = message;
	}
	public int getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(int primaryKey) {
		this.primaryKey = primaryKey;
	}
	public CrudType getCrudType() {
		return crudType;
	}
	public void setCrudType(CrudType crudType) {
		this.crudType = crudType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
