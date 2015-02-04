package com.waterR8.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.waterR8.gwt.event_list.client.model.WaterEvent;




/** parse apart a single data record string
 * 
 * @author casey
 *
 */
public class DataRecordParser {
	private static final int RECORD_LEN = 34;
	public static final String TEST_RECORD = "0c0a1c0f270e0be081f80017007c0b96b7";
	
	/** Expected format:
	 * 
	 * 0c0a1c0f270e0be081f80017007c0b96b7
	 * 
	 * @param data
	 * @return
	 * @throws DataRecordParseException
	 */
	public WaterEvent parseDataRecord(String data) throws DataRecordParseException {
		
		try {
			
			if(data == null || data.length() != RECORD_LEN) {
				throw new DataRecordParseException("Incorrect length, should be " + RECORD_LEN + " -- " + data);
			}
			
			int year = extractIntData(data, 0, 2);
			int month = extractIntData(data, 2, 2);  // (1 based)
			int day = extractIntData(data, 4, 2);
			int hour = extractIntData(data, 6,2);
			int min = extractIntData(data, 8, 2);
			int sec = extractIntData(data, 10, 2);
			Date timeStamp= new GregorianCalendar(2000 + year, month-1, day, hour, min, sec).getTime();
			
			String uniqueDevId = extractStringData(data, 12, 8);
			int eventId=extractIntData(data, 20, 4);
			int duration=extractIntData(data, 24,4);
			double batteryVolts=extractDoubleData(data, 28,4) / 1000;
			int rssi=extractIntData(data, 32,2) - 256;
			
			return new WaterEvent(-1,timeStamp, uniqueDevId,eventId,duration,batteryVolts,rssi);
		}
		catch(DataRecordParseException dpe) {
			throw dpe;
		}
		catch(Exception e) {
			throw new DataRecordParseException("Error parsing data record: " + data, e);
		}
	}

	private String extractStringData(String hexData, int start, int len) {
		return hexData.substring(start,start+len);
	}
	private int extractIntData(String hexData, int start, int len) {
		String token = hexData.substring(start,start+len);
		int value = Hex2Decimal.hex2decimal(token);
		return value;
	}
	
	private double extractDoubleData(String hexData, int start, int len) {
		String token = hexData.substring(start,start+len);
		double value = new BigInteger(token, 16).doubleValue();
		return value;
	}

	public List<WaterEvent> parseDataRecords(List<String> dataRecordStrings) throws DataRecordParseException {
		List<WaterEvent> dataRecords = new ArrayList<WaterEvent>();
		for(String dataStr: dataRecordStrings) {
			dataRecords.add(parseDataRecord(dataStr));
		}
		return dataRecords;
	}
}
