package com.waterR8.model;

import java.sql.Connection;

import com.waterR8.EventCheckException;

public interface EventCheck {
	String doCheck(Connection conn) throws EventCheckException;

	String getName();
}
