package com.waterR8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.waterR8.gwt.event_list.client.model.WaterEvent;


/** Handle processing of DataRecord database objects
 * 
 * @author casey
 *
 */
public class DataRecordDao extends SimpleJdbcDaoSupport  {

	static Logger __logger = Logger.getLogger(DataRecordDao.class);
	
	private static DataRecordDao __instance;
	public static DataRecordDao getInstance() throws Exception {
		if(__instance == null) {
			__instance = (DataRecordDao)SpringManager.getInstance().getBeanFactory().getBean(DataRecordDao.class.getName());
		}
		return __instance;
	}
	
	
	public List<String> addDataRecords(final List<WaterEvent> parseDataRecords) {
		
		__logger.info("Adding data records: " + parseDataRecords.size());

		List<String> errors = new ArrayList<String>();
		String sql = "insert into WATER_EVENT(event_timestamp, device_id, event_id, duration, battery_voltage, rf_rssi) " +
                     " values(?,?,?,?,?,?)";
		
		getJdbcTemplate().batchUpdate(sql,  new BatchPreparedStatementSetter() {
			
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				WaterEvent dr = parseDataRecords.get(i);
				ps.setTimestamp(1, new Timestamp(dr.getTimeStamp().getTime()));
				ps.setString(2,dr.getUniqueDevId());
				ps.setInt(3, dr.getEventId());
				ps.setDouble(4,  dr.getDuration());
				ps.setDouble(5,  dr.getBatteryVolts());
				ps.setInt(6,  dr.getRfRssi());
			}
			
			public int getBatchSize() {
				return parseDataRecords.size();
			}
		});
	
		return errors;
	}


	public List<WaterEvent> getDataRecords() {
		String sql = "select id, event_timestamp, device_id, event_id,duration,battery_voltage, rf_rssi from WATER_EVENT order by id desc";
		List<WaterEvent> rows = getJdbcTemplate().query(sql, new Object[]{}, new RowMapper<WaterEvent>() {
			public WaterEvent mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				return new WaterEvent(rs.getInt(1), new Date(rs.getTimestamp(2).getTime()),rs.getString(3),rs.getInt(4), rs.getDouble(5), rs.getDouble(6),rs.getInt(7));
			}
		});
		
		return rows;
	}


	public int deleteAllEventData() {
		return getJdbcTemplate().update("delete from WATER_EVENT");
	}

}
