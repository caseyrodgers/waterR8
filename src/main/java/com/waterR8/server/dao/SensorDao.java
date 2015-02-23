package com.waterR8.server.dao;

public class SensorDao {
	
	private static SensorDao __instance;

	public static SensorDao getInstance() {
		if(__instance == null) {
			__instance = new SensorDao();
		}
		return __instance;
	}
	
	private SensorDao() {}

	
	
	
	
}
