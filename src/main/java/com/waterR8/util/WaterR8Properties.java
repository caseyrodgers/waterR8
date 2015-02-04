package com.waterR8.util;



import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class WaterR8Properties extends Properties {

	private static WaterR8Properties __instance;
	public static WaterR8Properties getInstance() throws Exception {
		if(__instance == null) {
			__instance = new WaterR8Properties();
		}
		return __instance;
	}
	
	public void readProperties() throws Exception {
		try {
			/**
			 * allow override of system properties
			 * 
			 */
			String propsToUse = System.getenv("WATERR8_PROPERTIES");
			File pfile = null;
			if (propsToUse != null && propsToUse.length() > 0) {
				pfile = new File(propsToUse);
			} else {
				pfile = new File(System.getProperty("user.home"),"wr8.properties");
			}
			if (!pfile.exists()) {
				throw new Exception("Property file '" + pfile + "' does not exist");
			}
			clear();
			load(new FileReader(pfile));
		} catch (Exception e) {
			throw e;
		}
	}
	
	private WaterR8Properties() throws Exception {
		readProperties();
	}

	public String getContentDir() {
		return __instance.getProperty("content.dir");
	}

	public String getContentVideoDir() {
		return getContentDir() + "/videos";
	}

}
