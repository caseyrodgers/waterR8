package com.waterR8.server;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import com.waterR8.gwt.event_list.client.model.WaterEvent;
import com.waterR8.util.DataRecordParser;

public class DataRecordParser_Test extends TestCase {
	
	public DataRecordParser_Test(String name) {
		super(name);
	}
	
	public void test1() throws Exception {
		String data="0E03110F041C2D6D6E6A001500030003CF";
		WaterEvent dr = new DataRecordParser().parseDataRecord(data);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(dr.getTimeStamp());
		
		assertTrue(cal.get(Calendar.YEAR) == 2014);
		assertTrue(cal.get(Calendar.MONTH) == 2);
		assertTrue(cal.get(Calendar.HOUR_OF_DAY) == 15);
		assertTrue(cal.get(Calendar.MINUTE) == 4);
		assertTrue(cal.get(Calendar.SECOND) == 28);
		assertTrue(dr.getUniqueDevId().equals("2D6D6E6A"));
		assertTrue(dr.getEventId() == 21);
		assertTrue(dr.getDuration() == 3);
		assertTrue(dr.getRsRssi() == -49);
	}
	
	
	String testData = DataRecordParser.TEST_RECORD;
	public void testI() throws Exception {
		WaterEvent dr = new DataRecordParser().parseDataRecord(testData);
		assertTrue(dr.getDuration() == 124);
		assertTrue(dr.getEventId() == 23);
		assertTrue(dr.getBatteryVolts() == 2.966);
		assertTrue(dr.getRfRssi() == -73);
	}

}
