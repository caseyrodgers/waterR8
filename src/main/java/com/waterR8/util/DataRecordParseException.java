package com.waterR8.util;

public class DataRecordParseException extends Exception {
	
	public DataRecordParseException(String message) {
		super(message);
	}

	public DataRecordParseException(String message, Exception exp) {
		super(message, exp);
	}
}
