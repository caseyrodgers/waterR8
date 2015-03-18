package com.waterR8.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.Notification;

import org.joda.time.DateTime;

import com.waterR8.model.AvailableSensor;
import com.waterR8.model.Company;
import com.waterR8.model.CompanyDetails;
import com.waterR8.model.CompanyNetworkMap;
import com.waterR8.model.Complex;
import com.waterR8.model.ComplexContact;
import com.waterR8.model.ComplexDetails;
import com.waterR8.model.ContactsDetail;
import com.waterR8.model.Gateway;
import com.waterR8.model.NetworkDevice;
import com.waterR8.model.NetworkDevice.Role;
import com.waterR8.model.NetworkGraphNode;
import com.waterR8.model.NetworkGraphNode.Type;
import com.waterR8.model.NetworkNode;
import com.waterR8.model.Notifications;
import com.waterR8.model.RecordOperation;
import com.waterR8.model.RecordOperation.CrudType;
import com.waterR8.model.RepeaterInfo;
import com.waterR8.model.Sensor;
import com.waterR8.model.SensorDetails;
import com.waterR8.model.SensorEvent;
import com.waterR8.model.SensorNetworkStatus;
import com.waterR8.model.SeqHit;
import com.waterR8.model.SequenceInfo;
import com.waterR8.model.Unit;
import com.waterR8.model.UnitDetails;
import com.waterR8.server.ConnectionPool;
import com.waterR8.util.DateUtils;
import com.waterR8.util.SqlUtilities;

public class CompanyDao {

	static private CompanyDao __instance;

	static public CompanyDao getInstance() throws Exception {
		if (__instance == null) {
			__instance = new CompanyDao();
		}
		return __instance;
	}

	private CompanyDao() {
	}

	Logger LOGGER = Logger.getLogger(CompanyDao.class.getName());

	public List<Company> getCompanies() throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			String sql = "select * from company order by company_name";
			connection = ConnectionPool.getConnection();
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<Company> results = new ArrayList<Company>();
			while (rs.next()) {
				Company c = new Company(rs.getInt("id"),
						rs.getString("company_name"), rs.getString("owner"),
						rs.getString("address"), rs.getString("city"),
						rs.getString("state"), rs.getString("zip"));
				results.add(c);
			}
			return results;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception getting user home status", e);
			throw new Exception("Error getting user's home status", e);
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public CompanyDetails getCompanyDetails(int id) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();

			Company company = getCompany(id);
			CompanyDetails details = new CompanyDetails(company, getComplexes(
					connection, id));
			details.setNetworkStatus(getNetworkStatus(connection, id, 0, 0, 0));
			return details;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception getting user home status", e);
			throw new Exception("Error getting user's home status", e);
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}
	}

	/**
	 * Get the network information about the current context
	 *
	 * uses first id specified in following order:list sensor, unit, complex,
	 * company
	 * 
	 * where sensor is most granular and company broadest.
	 * 
	 * 
	 * 
	 * @param connection
	 * @param company
	 * @param complex
	 * @param unit
	 * @param sensor
	 * @return
	 * @throws Exception
	 */
	public SensorNetworkStatus getNetworkStatus(Connection connection,
			int company, int complex, int unit, int sensor) throws Exception {
		SensorNetworkStatus status = new SensorNetworkStatus();
		PreparedStatement ps = null;
		try {
			String sql = " select count(*) as event_count " + " from events e"
					+ " where src in (" + "     select sa.sn "
					+ " 	from   company c"
					+ " 	   JOIN complex  x on x.company = c.id"
					+ " 	   JOIN unit u on u.complex = x.id"
					+ " 	   JOIN sensor_assignment sa on sa.unit = u.id";

			String where = "";
			int idToUse = 0;
			if (sensor > 0) {
				where = " sa.id = ? ";
				idToUse = sensor;
			} else if (unit > 0) {
				where = " u.id = ? ";
				idToUse = unit;
			} else if (complex > 0) {
				where = " x.id = ? ";
				idToUse = complex;
			} else {
				where = " c.id = ? ";
				idToUse = company;
			}
			sql += (" where " + where);

			sql += ") and e.type = 2 ";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, idToUse);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				status.setEventCount(rs.getInt(1));
			}
			return status;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}

		return new SensorNetworkStatus(0);
	}

	public Company getCompany(int id) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "select * from company where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new Exception("Could not load company with id '" + id
						+ "'");
			}
			return getCompanyRecord(rs);

		} catch (Exception e) {
			throw new Exception("Error getting company '" + id + "'", e);
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	private List<Complex> getComplexes(Connection connection, int id)
			throws Exception {
		List<Complex> complexes = new ArrayList<Complex>();
		PreparedStatement ps = null;
		try {
			String sql = " select c.*, g.mac_addr as gateway_mac_addr, g.ip as gateway_ip"
					+ " from complex  c "
					+ "  LEFT JOIN gateway g on g.sn = c.gateway_sn "
					+ " where c.company = ? order by c.complex_name";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				complexes.add(getComplexRecord(rs));
			}
			return complexes;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	private Complex getComplexRecord(ResultSet rs) throws Exception {

		int gatewaySn = rs.getInt("gateway_sn");
		String gatewayMac = rs.getString("gateway_mac_addr");
		String gatewayIp = rs.getString("gateway_ip");

		Gateway gateway = new Gateway(gatewaySn, gatewayMac, gatewayIp);

		return new Complex(rs.getInt("id"), rs.getInt("company"),
				rs.getString("complex_name"), rs.getString("address"),
				rs.getString("city"), rs.getString("state"),
				rs.getString("zip"), rs.getString("phone"),
				rs.getString("email"), rs.getInt("building_count"),
				rs.getString("construction_type"), rs.getString("floor_type"),
				rs.getInt("lot_size"), rs.getInt("floors"),
				rs.getString("notes"), gateway);
	}

	public ComplexDetails getComplexDetails(int id) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			ComplexDetails details = new ComplexDetails(getComplex(connection,
					id), getUnits(connection, id));
			details.setCompany(getCompany(details.getComplex().getCompany()));
			details.setNetworkStatus(getNetworkStatus(connection, details
					.getCompany().getId(), details.getComplex().getId(), 0, 0));

			return details;
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}
	}

	private List<Unit> getUnits(Connection connection, int complexId)
			throws Exception {
		List<Unit> units = new ArrayList<Unit>();
		PreparedStatement ps = null;
		try {

			String sql = "SELECT u.*,  "
					+ "       ec.event_count,  "
					+ "       le.last_sensor_event,  "
					+ "       lrs.last_repeater_seq  "
					+ "FROM   unit u  "
					+ "       LEFT JOIN (SELECT u.id,  "
					+ "                         Count(*) AS event_count  "
					+ "                  FROM   unit u  "
					+ "                         JOIN sensor_assignment sa  "
					+ "                           ON sa.unit = u.id  "
					+ "                         JOIN events e  "
					+ "                           ON e.src = sa.sn  "
					+ "                  WHERE  u.complex = ?  "
					+ "                         AND e.type = 2  "
					+ "                  GROUP  BY u.id) ec  "
					+ "              ON ec.id = u.id  "
					+ "       LEFT JOIN (SELECT u.id,  "
					+ "                         Max(ts) AS last_sensor_event  "
					+ "                  FROM   unit u  "
					+ "                         JOIN sensor_assignment sa  "
					+ "                           ON sa.unit = u.id  "
					+ "                         JOIN events e on e.src = sa.sn   "
					+ "                  WHERE  u.complex = ?  "
					+ "                         AND e.type = 2  "
					+ "                  GROUP  BY u.id) le  "
					+ "              ON le.id = u.id  "
					+ "       LEFT JOIN (SELECT u.id,  "
					+ "                         e.seq AS last_repeater_seq  "
					+ "                  FROM   unit u  "
					+ "                         JOIN sensor_assignment sa  "
					+ "                           ON sa.unit = u.id  "
					+ "                         JOIN events e  "
					+ "                           ON e.src = sa.sn   "
					+ "                         JOIN (SELECT u.id,  "
					+ "                                      Max(e.id) AS max_id  "
					+ "                               FROM   unit u  "
					+ "                                      JOIN sensor_assignment sa  "
					+ "                                        ON sa.unit = u.id  "
					+ "                                      JOIN events e  on e.src = sa.sn  "
					+ "       WHERE  u.complex = ?  "
					+ "       AND e.type = 132  "
					+ "       GROUP  BY u.id) me  "
					+ "       ON me.max_id = e.id) lrs  "
					+ "              ON lrs.id = u.id  "
					+ " WHERE  u.complex = ?  ";

			// "select * from unit where complex = ? order by unit_number"
			ps = connection.prepareStatement(sql);
			ps.setInt(1, complexId);
			ps.setInt(2, complexId);
			ps.setInt(3, complexId);
			ps.setInt(4, complexId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				units.add(getUnitRecord(rs));
			}
			return units;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	private Unit getUnitRecord(ResultSet rs) throws Exception {

		String lastEvent = getLastEvent(rs.getTimestamp("last_sensor_event"));
		int lastRepeaterSeq = rs.getInt("last_repeater_seq");

		return new Unit(rs.getInt("id"), rs.getInt("complex"),
				rs.getString("unit_number"), rs.getString("type"),
				rs.getInt("beds"), rs.getInt("tenants"),
				rs.getInt("event_count"), lastEvent, lastRepeaterSeq);
	}

	private String getLastEvent(Timestamp timestamp) {
		String lastTs = "";
		if (timestamp != null) {
			// lastTs = _dateFormat.format(timestamp.getTime());
			long time = timestamp.getTime();
			lastTs = DateUtils.getTimeSinceLabel(new Date(time));
		}
		return lastTs;
	}

	public Complex getComplex(Connection connection, int id) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = " select c.*, g.mac_addr as gateway_mac_addr, g.ip as gateway_ip"
					+ " from complex  c "
					+ "  LEFT JOIN gateway g on g.sn = c.gateway_sn "
					+ " where c.id = ? order by c.complex_name";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new Exception("Could not load complex '" + id + "'");
			}
			return getComplexRecord(rs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
		return null;

	}

	public UnitDetails getUnitDetails(int id) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			UnitDetails details = new UnitDetails(getUnit(connection, id));
			details.getSensors().addAll(getSensors(connection, id));
			details.setComplex(getComplex(connection, details.getUnit()
					.getComplex()));
			details.setCompany(getCompanyForUnit(connection, details.getUnit()
					.getId()));
			details.setNetworkStatus(getNetworkStatus(connection, details
					.getCompany().getId(), details.getComplex().getId(),
					details.getUnit().getId(), 0));
			details.setAvailableSensors(getAvailableSensors(connection, details
					.getComplex().getId()));

			return details;
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}

	}

	/**
	 * get all manufactored sensors to be used as lookup
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private List<AvailableSensor> getAvailableSensors(Connection conn,
			int complexId) throws Exception {

		List<AvailableSensor> list = new ArrayList<AvailableSensor>();
		PreparedStatement st = null;
		try {
			String sql = "select * "
					+ " from radio_sn  "
					+ " where sn not in ( "
					+ "     select sn from sensor_assignment "
					+ " ) and role in (1,2) "
					+ " and sn in (select sn from sn_complex where complex = ?) "
					+ " order by sn ";
			st = conn.prepareStatement(sql);
			st.setInt(1, complexId);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				list.add(new AvailableSensor(rs.getInt("sn"), rs.getInt("role")));
			}
		} finally {
			SqlUtilities.releaseResources(null, null, null);
		}

		return list;
	}

	private Collection<? extends Sensor> getSensors(Connection connection,
			int unitId) throws Exception {
		List<Sensor> sensors = new ArrayList<Sensor>();
		PreparedStatement ps = null;
		try {
			String sql =

			"select sa.*, ec.event_count, le.last_sensor_event  "
					+ "from sensor_assignment sa " + "LEFT JOIN ( "
					+ "       select sa.id, count(*) as event_count "
					+ "         from sensor_assignment sa "
					+ "         join events e on sa.sn  = e.src "
					+ "      where sa.unit= ? " + "      group by sa.id "
					+ "  ) ec on ec.id = sa.id " + " " + "LEFT JOIN ( "
					+ "       select sa.id, max(ts) as last_sensor_event "
					+ "         from sensor_assignment sa "
					+ "         join events e on sa.sn  = e.src "
					+ "      where sa.unit = ? " + "      group by sa.id "
					+ "  ) le on le.id = sa.id " + "where sa.unit  = ? ";

			// "select * from sensor_assignment where unit = ?";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, unitId);
			ps.setInt(2, unitId);
			ps.setInt(3, unitId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Sensor sensorRecord = getSensorRecord(rs);
				sensorRecord.setEventCount(rs.getInt("event_count"));
				sensorRecord.setLastEvent(getLastEvent(rs
						.getTimestamp("last_sensor_event")));
				sensors.add(sensorRecord);
			}
			return sensors;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	private Sensor getSensorRecord(ResultSet rs) throws Exception {
		int ssn = rs.getInt("sn");
		Role role = NetworkDevice.Role.values()[rs.getInt("role")];

		Sensor sensor = new Sensor(rs.getInt("id"), rs.getInt("unit"),
				role.ordinal(), ssn);

		sensor.setSensorHex(_convertSensorIntSerialSsnToHex(ssn));

		return sensor;
	}

	private Unit getUnit(Connection connection, int id) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "select u.*, 0 as event_count, null as last_sensor_event, 0 as last_repeater_seq from unit u where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new Exception("Could not load unit '" + id + "'");
			}
			return getUnitRecord(rs);
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}

	}

	public SensorDetails getSensorDetail(int sensorId) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			SensorDetails details = new SensorDetails(getSensor(connection,
					sensorId));
			details.getEvents().addAll(
					getSensorEvents(connection, details.getSensor()));
			Unit unit = getUnit(connection, details.getSensor().getUnit());
			details.setUnit(unit);
			details.setCompany(getCompanyForUnit(connection, unit.getId()));
			details.setComplex(getComplex(connection, unit.getComplex()));
			details.setNetworkStatus(getNetworkStatus(connection, 0, 0, 0,
					details.getSensor().getId()));

			return details;
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}
	}

	private Company getCompanyForUnit(Connection connection, int unit)
			throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = " select  c.* " + " from unit u  "
					+ " JOIN complex x on x.id = u.complex "
					+ " JOIN company c on c.id = x.company "
					+ " where u.id = ? " + " order by u.unit_number ";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, unit);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new Exception("Could not company for unit '" + unit + "'");
			}
			return getCompanyRecord(rs);
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	private Company getCompanyRecord(ResultSet rs) throws Exception {
		return new Company(rs.getInt("id"), rs.getString("company_name"),
				rs.getString("owner"), rs.getString("address"),
				rs.getString("city"), rs.getString("state"),
				rs.getString("zip"));
	}

	SimpleDateFormat _dateFormat = new SimpleDateFormat("h:mm a M/d/yy"); // DateFormat.getDateTimeInstance(DateFormat.SHORT,
																			// DateFormat.SHORT);

	// look for events with this src;
	private List<SensorEvent> getSensorEvents(Connection connection,
			Sensor sensor) throws Exception {
		List<SensorEvent> events = new ArrayList<SensorEvent>();
		try {
			PreparedStatement ps = null;
			try {
				String sql = "select * from events where src = ? order by ts desc limit 100";
				ps = connection.prepareStatement(sql);
				ps.setInt(1, sensor.getSensor());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					events.add(getSensorEventRecord(rs));
				}
			} finally {
				SqlUtilities.releaseResources(null, ps, null);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error getting sensor/repeater data", e);
		}

		return events;
	}

	private SensorEvent getSensorEventRecord(ResultSet rs) throws Exception {
		int src = rs.getInt("src");
		int seq = rs.getInt("seq");
		int hopCnt = rs.getInt("hopcnt");
		int first = rs.getInt("first");
		String json = rs.getString("json");
		String type = rs.getString("type");
		int battery = rs.getInt("bat");
		int dur = rs.getInt("dur");
		long time = rs.getTimestamp("ts").getTime();
		int rssiRcv = rs.getInt("rssi_rcv");

		String timeStamp = _dateFormat.format(new Date(time));
		return new SensorEvent(rs.getInt("id"), json, type, timeStamp, time,
				src, seq, hopCnt, first, battery, dur, rssiRcv);
	}

	/**
	 * Convert integer sensor serial number into hex 8 char long hex number
	 * 
	 * ssnInDecAsString should be an integer (as string)
	 * 
	 * TODO: change sensor to be int.
	 * 
	 * @param sensorIdInDecimal
	 * @return
	 */
	private String _convertSensorIntSerialSsnToHex(int ssnInDec) {
		String ssnInHex = Integer.toHexString(ssnInDec);

		// make it 8 chars long
		ssnInHex = ("00000000" + ssnInHex);
		ssnInHex = ssnInHex.substring(ssnInHex.length() - 8);

		return ssnInHex;
	}

	private int convertSensorHexSerialSsnToInt(String ssnInHex) {
		return Integer.parseInt(ssnInHex.trim(), 16);
	}

	private Sensor getSensor(Connection connection, int id) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "select * from sensor_assignment where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new Exception("Could not load sensor '" + id + "'");
			}
			return getSensorRecord(rs);
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	public RecordOperation addCompany(Company company) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "insert into company(company_name, address, city, state, zip)values(?,?,?,?,?)";
			ps = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, company.getCompanyName());
			ps.setString(2, company.getAddress());
			ps.setString(3, company.getCity());
			ps.setString(4, company.getState());
			ps.setString(5, company.getZip());

			int cnt = ps.executeUpdate();
			if (cnt != 1) {
				throw new Exception("Company record could not be added: "
						+ company);
			}
			ResultSet rs = ps.getGeneratedKeys();

			int companyId = -1;
			if (rs.next()) {
				companyId = rs.getInt(1);
			} else {
				throw new Exception("Could not retreive new company id : "
						+ company);
			}
			return new RecordOperation(CrudType.CREATE, companyId, null);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public RecordOperation deleteCompany(int id) throws Exception {
		assert (id > 0);

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "delete from company where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not deleted" : "";
			return new RecordOperation(CrudType.DELETE, id, msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}

	}

	public RecordOperation addComplex(Complex complex) throws Exception {

		if (complex.getCompany() == 0) {
			throw new Exception("Complex must have valid company specified");
		}

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "insert into complex(company, complex_name, address, city, state, zip "
					+ ", phone, email, building_count, construction_type, floor_type, lot_size "
					+ " , floors, notes) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, complex.getCompany());
			ps.setString(2, complex.getComplexName());
			ps.setString(3, complex.getAddress());
			ps.setString(4, complex.getCity());
			ps.setString(5, complex.getState());
			ps.setString(6, complex.getZip());
			ps.setString(7, complex.getPhone());
			ps.setString(8, complex.getEmail());
			ps.setInt(9, complex.getBuildingCount());
			ps.setString(10, complex.getConstructionType());
			ps.setString(11, complex.getFloorType());
			ps.setInt(12, complex.getLotSize());
			ps.setInt(13, complex.getFloors());
			ps.setString(14, complex.getNotes());
			
			int cnt = ps.executeUpdate();
			if (cnt != 1) {
				throw new Exception(
						"Company Complex record could not be added: " + complex);
			}
			ResultSet rs = ps.getGeneratedKeys();

			int complexId = -1;
			if (rs.next()) {
				complexId = rs.getInt(1);
			} else {
				throw new Exception(
						"Could not retreive new company complex id : "
								+ complex);
			}
			return new RecordOperation(CrudType.CREATE, complexId, null);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
		return null;
	}

	public RecordOperation deleteComplex(int id) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "delete from complex where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not deleted" : "";
			return new RecordOperation(CrudType.DELETE, id, msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public RecordOperation addUnit(Unit unit) throws Exception {

		if (unit.getComplex() == 0) {
			throw new Exception("Unit must have valid complex specified");
		}

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "insert into unit(complex,unit_number,type,beds,tenants)values(?,?,?,?,?)";
			ps = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, unit.getComplex());
			ps.setString(2, unit.getUnitNumber());
			ps.setString(3, unit.getType());
			ps.setInt(4, unit.getBeds());
			ps.setInt(5, unit.getTenants());

			int cnt = ps.executeUpdate();
			if (cnt != 1) {
				throw new Exception("Company Unit record could not be added: "
						+ unit);
			}
			ResultSet rs = ps.getGeneratedKeys();

			int complexId = -1;
			if (rs.next()) {
				complexId = rs.getInt(1);
			} else {
				throw new Exception(
						"Could not retreive new company complex id : " + unit);
			}
			return new RecordOperation(CrudType.CREATE, complexId, null);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
		return null;
	}

	public RecordOperation deleteUnit(int id) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "delete from unit where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not deleted" : "";
			return new RecordOperation(CrudType.DELETE, id, msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public RecordOperation addSensor(Sensor sensor) throws Exception {

		if (sensor.getUnit() == 0) {
			throw new Exception("Sensor must have valid Unit specified");
		}

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "insert into sensor_assignment(unit,role,sn)values(?,?,?)";
			ps = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, sensor.getUnit());
			ps.setInt(2, sensor.getRole());
			ps.setInt(3, sensor.getSensor());

			int cnt = ps.executeUpdate();
			if (cnt != 1) {
				throw new Exception(
						"Company Unit Sensor record could not be added: "
								+ sensor);
			}
			ResultSet rs = ps.getGeneratedKeys();

			int newId = -1;
			if (rs.next()) {
				newId = rs.getInt(1);
			} else {
				throw new Exception("Could not retreive new Sensor id : "
						+ sensor);
			}
			return new RecordOperation(CrudType.CREATE, newId, null);

		} catch (Exception e) {
			throw e;
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public RecordOperation deleteSensor(int id) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "delete from sensor_assignment where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not deleted" : "";
			return new RecordOperation(CrudType.DELETE, id, msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public CompanyNetworkMap getCompanyMapForComplex(int complexId)
			throws Exception {

		List<NetworkGraphNode> sensors = new ArrayList<NetworkGraphNode>();

		List<NetworkNode> networkMap = new ArrayList<NetworkNode>();

		CompanyNetworkMap companyMap = new CompanyNetworkMap();

		Connection connection = null;
		PreparedStatement psComplex = null;
		PreparedStatement psUnit = null;
		PreparedStatement psSensor = null;

		try {
			connection = ConnectionPool.getConnection();

			// add the company, root node
			NetworkGraphNode rootNode = new NetworkGraphNode(Type.ROOT, 0, 0,
					"Root");

			psComplex = connection
					.prepareStatement("select id, complex_name from complex where id = ?");
			psComplex.setInt(1, complexId);

			ResultSet rsComplex = psComplex.executeQuery();
			if (!rsComplex.next()) {
				throw new Exception("No such complex: " + complexId);
			}

			String complexName = rsComplex.getString("complex_name");
			NetworkGraphNode complexNode = new NetworkGraphNode(Type.COMPLEX,
					complexId, 0, "Complex: " + complexName);
			networkMap.add(new NetworkNode(rootNode, complexNode));

			psUnit = connection
					.prepareStatement("select id, unit_number from unit where complex = ?");
			psUnit.setInt(1, complexId);
			ResultSet rsUnit = psUnit.executeQuery();

			while (rsUnit.next()) {
				String unitNumber = rsUnit.getString("unit_number");
				int unitId = rsUnit.getInt("id");
				NetworkGraphNode unitNode = new NetworkGraphNode(Type.UNIT,
						unitId, 0, "Unit: " + unitNumber);
				networkMap.add(new NetworkNode(complexNode, unitNode));

				psSensor = connection
						.prepareStatement("select id, sn from sensor_assignment where role = ? and unit = ?");
				psSensor.setInt(1, Role.REPEATER.ordinal());
				psSensor.setInt(2, unitId);

				ResultSet rsSensor = psSensor.executeQuery();

				while (rsSensor.next()) {
					String sensor = rsSensor.getString("sensor");
					int sensorId = rsSensor.getInt("id");

					NetworkGraphNode sensorNode = new NetworkGraphNode(
							Type.SENSOR, sensorId, 0, "Repeater: " + sensor);
					networkMap.add(new NetworkNode(unitNode, sensorNode));

					sensors.add(sensorNode);
				}
			}

			fixupSensorLastValues(connection, sensors);

			companyMap.setNetworkNodes(networkMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, psComplex, connection);
			SqlUtilities.releaseResources(null, psUnit, null);
			SqlUtilities.releaseResources(null, psSensor, null);
		}

		return companyMap;
	}

	/**
	 * TODO: combine all getMap into single dynamic one * @param unitId
	 * 
	 * @return
	 * @throws Exception
	 */
	public CompanyNetworkMap getCompanyMapForUnit(int unitId) throws Exception {

		List<NetworkGraphNode> sensors = new ArrayList<NetworkGraphNode>();

		List<NetworkNode> networkMap = new ArrayList<NetworkNode>();

		CompanyNetworkMap companyMap = new CompanyNetworkMap();

		Connection connection = null;
		PreparedStatement psUnit = null;
		PreparedStatement psSensor = null;

		try {
			connection = ConnectionPool.getConnection();

			// add the company, root node
			NetworkGraphNode rootNode = new NetworkGraphNode(Type.ROOT, 0, 0,
					"Root");

			psUnit = connection
					.prepareStatement("select id, unit_number from unit where id = ?");
			psUnit.setInt(1, unitId);
			ResultSet rsUnit = psUnit.executeQuery();
			if (!rsUnit.next()) {
				throw new Exception("no such unit: " + unitId);
			}

			String unitNumber = rsUnit.getString("unit_number");

			NetworkGraphNode unitNode = new NetworkGraphNode(Type.UNIT, unitId,
					0, "Unit: " + unitNumber);
			networkMap.add(new NetworkNode(rootNode, unitNode));

			psSensor = connection
					.prepareStatement("select id, sensor from sensor_assignment where unit = ?");
			psSensor.setInt(1, unitId);

			ResultSet rsSensor = psSensor.executeQuery();

			while (rsSensor.next()) {
				String sensor = rsSensor.getString("sensor");
				int sensorId = rsSensor.getInt("id");

				NetworkGraphNode sensorNode = new NetworkGraphNode(Type.SENSOR,
						sensorId, 0, "Repeater: " + sensor);
				networkMap.add(new NetworkNode(unitNode, sensorNode));

				sensors.add(sensorNode);
			}

			fixupSensorLastValues(connection, sensors);

			companyMap.setNetworkNodes(networkMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, psUnit, connection);
			SqlUtilities.releaseResources(null, psSensor, null);
		}

		return companyMap;
	}

	public CompanyNetworkMap getCompanyMapForCompany(int companyId,
			SequenceInfo selectedSequence) throws Exception {

		List<NetworkNode> networkMap = new ArrayList<NetworkNode>();

		CompanyNetworkMap companyMap = new CompanyNetworkMap();

		Connection connection = null;
		PreparedStatement psCompany = null;
		PreparedStatement psComplex = null;
		PreparedStatement psUnit = null;
		PreparedStatement psSensor = null;

		try {

			List<NetworkGraphNode> sensorsInCompany = new ArrayList<NetworkGraphNode>();

			connection = ConnectionPool.getConnection();

			psCompany = connection
					.prepareStatement("select company_name from company where id = ?");
			psCompany.setInt(1, companyId);
			ResultSet rs = psCompany.executeQuery();
			if (!rs.next()) {
				throw new Exception("no company found to build network graph");
			}
			String companyName = rs.getString("company_name");
			psCompany.close();

			// add the company, root node
			NetworkGraphNode rootNode = new NetworkGraphNode(Type.ROOT, 0, 0,
					"Root");
			NetworkGraphNode companyNode = new NetworkGraphNode(Type.COMPANY,
					companyId, 0, "Company: " + companyName);
			networkMap.add(new NetworkNode(rootNode, companyNode));

			DateTime today = new DateTime().withTimeAtStartOfDay();

			List<SequenceInfo> seqNumbers = getRepeaterSequenceNumbers(
					connection, new Date(today.getMillis()));
			int lastBeaconSeq = 0;
			if (selectedSequence != null) {
				lastBeaconSeq = selectedSequence.getSeq();
			} else {
				lastBeaconSeq = seqNumbers.size() == 0 ? 0 : seqNumbers.get(0)
						.getSeq();
			}
			List<NetworkNode> repeaterMap = createRepeaterMap(connection,
					companyId, lastBeaconSeq);

			// List<RepeaterInfo> repeaterInfo = getRepeaterInfo(connection,
			// companyId, seqNumbers);

			// for each repeater in this company

			// fixupRepeatersLastValues(connection, sensorsInCompany);

			List<Sensor> allRep = getRepeatersInCompany(connection, companyId);
			List<RepeaterInfo> repeatersInCompany = fixupRepeatersInfo(allRep,
					seqNumbers);

			companyMap.setNetworkNodes(repeaterMap);
			companyMap.setAllRepeaters(repeatersInCompany);
			companyMap.setSequenceNumbers(seqNumbers);
			companyMap.setCurrentSequence(lastBeaconSeq);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			SqlUtilities.releaseResources(null, psCompany, connection);
			SqlUtilities.releaseResources(null, psComplex, null);
			SqlUtilities.releaseResources(null, psUnit, null);
			SqlUtilities.releaseResources(null, psSensor, null);
		}

		return companyMap;
	}

	private List<RepeaterInfo> fixupRepeatersInfo(
			List<Sensor> repeatersInCompany, List<SequenceInfo> seqNumbers) {

		List<RepeaterInfo> repInfoList = new ArrayList<RepeaterInfo>();

		int total = seqNumbers.size();
		for (Sensor repeater : repeatersInCompany) {
			int countResponses = 0;
			int avgHopCnt = 0;
			int avgRssiRcv = 0;
			// how many responses
			for (SequenceInfo seq : seqNumbers) {

				for (SeqHit d : seq.getDevicesThatResponded()) {
					if (d.getSensor().getSensor() == repeater.getSensor()) {
						countResponses++;

						avgRssiRcv += d.getRssiRcv();
						avgHopCnt += d.getHopCnt();
						break;
					}
				}

			}

			int percent = countResponses > 0 ? (int) ((countResponses * 100.0f) / total)
					: 0;

			avgRssiRcv = countResponses > 0 ? avgRssiRcv / countResponses : 0;
			avgHopCnt = countResponses > 0 ? avgHopCnt / countResponses : 0;

			RepeaterInfo repInfo = new RepeaterInfo(repeater, avgHopCnt,
					avgRssiRcv, percent);
			repInfo.setRespondPercent(percent);

			repInfoList.add(repInfo);
		}
		return repInfoList;
	}

	private List<String> getRepeatersInSequence(Connection connection,
			int companyId, List<SequenceInfo> seqNumbers) throws Exception {
		PreparedStatement ps = null;
		List<String> repeaterSrc = new ArrayList<String>();

		String il = null;
		for (SequenceInfo r : seqNumbers) {
			if (il != null) {
				il += ",";
			}
			il += r.getSeq();
		}

		try {
			String sql = " select distinct seq, src, count(*) "
					+ " from events " + " where seq in (" + il + ")"
					+ " and type = 132 " + " group by seq, src";
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int seq = rs.getInt("seq");
				String src = rs.getString("src");

			}
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
		return repeaterSrc;
	}

	private List<NetworkNode> createRepeaterMap(Connection connection,
			int companyId, int lastBeaconSeq) throws Exception {

		PreparedStatement ps = null;
		List<NetworkNode> network = new ArrayList<NetworkNode>();

		try {

			Map<Integer, List<Sensor>> hopMap = new HashMap<Integer, List<Sensor>>();

			List<SensorEvent> events = getSensorEventsForSeq(connection,
					companyId, lastBeaconSeq);

			Company company = getCompany(companyId);
			NetworkGraphNode root = new NetworkGraphNode(Type.ROOT, 0, 0,
					company.getCompanyName());

			buildNetworkTree(network, root, 0, 1, events);
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
		return network;
	}

	private void buildNetworkTree(List<NetworkNode> network,
			NetworkGraphNode parent, int parentSrc, int hop,
			List<SensorEvent> events) {
		for (SensorEvent e : events) {
			// System.out.println("Checking: hop: " + hop + ", " +
			// " parentSrc: " + parentSrc + ", src: " + e.getSrc());

			if (e.getHopCnt() == hop) {

				// System.out.println("Match hopcnt: " + hop + " , " +
				// e.getSrc());
				boolean add = false;
				if (hop == 1 && parent.getType() == Type.ROOT) {
					add = true;
				} else if (e.getFirst() == parentSrc) {
					add = true;
				}

				if (add) {

					if (!alreadyInList(e, network)) {

						String timeLastEvent = getTimeLastEvent(events,
								e.getSrc());
						// System.out.println("Adding: hop: " + e.getHopCnt() +
						// ", parent: " + parentSrc + ",  first: " +
						// e.getFirst() + ", src: " + e.getSrc());
						String label = e.getSrc() + ",h:" + e.getHopCnt()
								+ ",r:" + e.getRssiRcv();

						// System.out.println("Adding: " + label);

						NetworkGraphNode child = new NetworkGraphNode(
								Type.REPEATER, e.getId(), e.getSrc(), label);
						child.setSubLabel(timeLastEvent);
						network.add(new NetworkNode(parent, child));

						// get all children of this child
						buildNetworkTree(network, child, e.getSrc(), hop + 1,
								events);
					}
				}
			}
		}
	}

	private String getTimeLastEvent(List<SensorEvent> events, int src) {
		long newestTs = 0;
		for (SensorEvent e : events) {
			if (e.getSrc() == src) {
				long ts = e.getTime();
				if (ts > newestTs) {
					newestTs = ts;
				}
			}
		}

		return newestTs > 0 ? DateUtils.getTimeSinceLabel(new Date(newestTs))
				: "No Events";
	}

	private boolean alreadyInList(SensorEvent e, List<NetworkNode> network) {
		for (NetworkNode n : network) {
			if (n.getDeviceChild().getSrc() == e.getSrc()
					|| n.getDeviceParent().getSrc() == e.getSrc()) {
				return true;
			}
		}
		return false;
	}

	private List<SensorEvent> getSensorEventsForSeq(Connection connection,
			int companyId, int lastBeaconSeq) throws Exception {
		PreparedStatement ps = null;
		List<SensorEvent> events = new ArrayList<SensorEvent>();
		try {
			String sql = "select e.* " + "from events e "
					+ "  join sensor_assignment sa on sa.sn  = e.src "
					+ "  join unit u on u.id = sa.unit "
					+ "  join complex c on c.id = u.complex "
					+ "where c.company = ? " + "and e.type = 132 "
					+ "and e.seq = ? ";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, companyId);
			ps.setInt(2, lastBeaconSeq);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				events.add(getSensorEventRecord(rs));
			}
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
		return events;
	}

	private List<Sensor> getRepeatersThatRespondedToThisOne(
			Connection connection, int companyId, Sensor sensorParent,
			int lastBeaconSeq, int h) throws Exception {
		PreparedStatement ps = null;
		List<Sensor> repeaters = new ArrayList<Sensor>();
		try {

			String sql = "select e.id, e.first " + "from events e "
					+ "  join sensor_assignment sa on  sa.sn  = e.src "
					+ "  join unit u on u.id = sa.unit "
					+ "  join complex c on c.id = u.complex "
					+ "where c.company = ? " + "and e.type = 132 "
					+ "and e.seq = ? " + "and e.first = cast(? as integer)";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, companyId);
			ps.setInt(2, lastBeaconSeq);
			ps.setInt(3, sensorParent.getSensor());

		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
		return repeaters;
	}

	private List<Sensor> getRepeatersInCompany(Connection connection,
			int companyId) throws Exception {

		PreparedStatement ps = null;
		List<Sensor> repeaters = new ArrayList<Sensor>();
		try {

			String sql = "select sa.id, u.id as unit_id, sa.sn "
					+ "  from sensor_assignment sa  "
					+ "      join unit u on u.id = sa.unit "
					+ "     join complex c on c.id = u.complex "
					+ "where c.company = ? " + "and sa.role = ?";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, companyId);
			ps.setInt(2, NetworkDevice.Role.REPEATER.ordinal());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Sensor s = new Sensor(rs.getInt("id"), rs.getInt("unit_id"),
						NetworkDevice.Role.REPEATER.ordinal(), rs.getInt("sn"));
				repeaters.add(s);
			}
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
		return repeaters;
	}

	/**
	 * looks up the last values for named sensors and updates the graph label to
	 * shown.
	 * 
	 * @param connection
	 * @param sensors
	 * @throws Exception
	 */
	private void fixupSensorLastValues(Connection connection,
			List<NetworkGraphNode> sensors) throws Exception {

		if (sensors.size() == 0) {
			return; // nothing to do
		}

		String inList = "";
		for (NetworkGraphNode n : sensors) {

			if (inList.length() > 0) {
				inList += ", ";
			}
			inList += n.getId();
		}
		inList = "(" + inList + ")";

		/** mark all sensors with no data */
		for (NetworkGraphNode n : sensors) {
			switch (n.getType()) {
			case SENSOR:
				n.setSubLabel("No Data");
				break;

			default:
				break;
			}
		}

		PreparedStatement ps = null;
		try {
			String sql = "SELECT sa.id as sensor_id, ts, src,hopcnt,bat,rssi,dur,e.seq "
					+ "from events e  "
					+ " JOIN sensor_assignment sa on sa.sn  = e.src "
					+ " where sa.id in " + inList + " order by sa.id, ts desc";

			ps = connection.prepareStatement(sql);

			List<Integer> seen = new ArrayList<Integer>();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				int sensorId = rs.getInt("sensor_id");

				// is this one already record
				if (seen.size() > 0) {
					if (seen.get(seen.size() - 1) == sensorId) {
						continue; // ready in list
					}
				}

				seen.add(sensorId);

				Timestamp timeStamp = rs.getTimestamp("ts");
				int hop = rs.getInt("hopcnt");
				int bat = rs.getInt("bat");
				int rssi = rs.getInt("rssi");
				int dur = rs.getInt("dur");
				int seq = rs.getInt("seq");

				SimpleDateFormat dateFormat = new SimpleDateFormat("H:m");
				boolean found = true;
				for (NetworkGraphNode n : sensors) {
					if (n.getId() == sensorId) {

						String ts = DateUtils.getTimeSinceLabel(new Date(
								timeStamp.getTime()));
						String subLabel = ts + ",hop: " + hop + ",seq:" + seq;
						n.setSubLabel(subLabel);
						break;
					}
				}

			}
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	/**
	 * Return list of distinct sequence numbers since a given date
	 * 
	 * @param conn
	 * @param since
	 * @return
	 * @throws Exception
	 */
	public List<SequenceInfo> getRepeaterSequenceNumbers(Connection conn,
			Date since) throws Exception {
		List<SequenceInfo> li = new ArrayList<SequenceInfo>();
		PreparedStatement ps = null;
		PreparedStatement psSrc = null;
		try {

			String sql = "select distinct e.seq, sr.seq_run_date, sr.max_id "
					+ "from events e "
					+ "  left join ( "
					+ "       select e.seq, max(id) as max_id, max(ts) as seq_run_date "
					+ "       from  events e " + "       where ts > ? "
					+ "       and type = 132 " + "       group by e.seq "
					+ "   ) sr on sr.seq = e.seq " + "where ts > ? "
					+ " and type = 132 " + "order by max_id desc";

			ps = conn.prepareStatement(sql);
			ps.setTimestamp(1, new Timestamp(since.getTime()));
			ps.setTimestamp(2, new Timestamp(since.getTime()));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int seq = rs.getInt("seq");

				Date seqDate = new Date(rs.getTimestamp("seq_run_date")
						.getTime());
				String label = seq + "  ("
						+ DateUtils.getTimeSinceLabel(seqDate) + ")";
				SequenceInfo seqInfo = new SequenceInfo(seq, seqDate, label);
				li.add(seqInfo);
			}

			String inList = "";
			for (SequenceInfo l : li) {
				if (inList.length() > 0) {
					inList += ",";
				}
				inList += l.getSeq();
			}

			if (li.size() == 0) {
				return li;
			} else {
				sql = "select seq, e.src, avg(e.hopcnt) as avg_hop, avg(e.rssi_rcv) as avg_rssi "
						+ " from events e "
						+ " where seq in ("
						+ inList
						+ ")"
						+ " and type = 132 "
						+ " and ts > ? "
						+ " group by seq, e.src";

				psSrc = conn.prepareStatement(sql);
				psSrc.setTimestamp(1, new Timestamp(since.getTime()));
				rs = psSrc.executeQuery();
				while (rs.next()) {
					int srcInDec = rs.getInt("src");
					int seq = rs.getInt("seq");
					int avgHopCnt = rs.getInt("avg_hop");
					int avgRcciRcv = rs.getInt("avg_rssi");

					for (SequenceInfo l : li) {
						if (l.getSeq() == seq) {
							Sensor repeater = new Sensor(0, 0,
									Role.REPEATER.ordinal(), srcInDec);
							l.getDevicesThatResponded().add(
									new SeqHit(seq, repeater, avgHopCnt,
											avgRcciRcv));
							break;
						}
					}

				}
			}

		} finally {
			SqlUtilities.releaseResources(null, ps, null);
			SqlUtilities.releaseResources(null, psSrc, null);
		}
		return li;
	}

	public RecordOperation updateCompany(Company company) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "update company " + "set company_name=? "
					+ ",address=? " + ",city=? " + ",state=? " + ",zip=?"
					+ " where id = ?";

			ps = connection.prepareStatement(sql);
			ps.setString(1, company.getCompanyName());
			ps.setString(2, company.getAddress());
			ps.setString(3, company.getCity());
			ps.setString(4, company.getState());
			ps.setString(5, company.getZip());
			ps.setInt(6, company.getId());

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not updated" : "";

			return new RecordOperation(CrudType.UPDATE, company.getId(), msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public RecordOperation updateComplex(Complex complex) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();

			String sql = "update complex " + "set company=?,"
					+ "complex_name=?,address=?," + "city=?," + "state=?,"
					+ "zip=?," + "phone=?," + "email=?," + "building_count=?,"
					+ "construction_type=?," + "floor_type=?," + "floors=?,"
					+ "lot_size=?," + "notes=?" + " where id = ?";
			ps = connection.prepareStatement(sql);

			ps.setInt(1, complex.getCompany());
			ps.setString(2, complex.getComplexName());
			ps.setString(3, complex.getAddress());
			ps.setString(4, complex.getCity());
			ps.setString(5, complex.getState());
			ps.setString(6, complex.getZip());
			ps.setString(7, complex.getPhone());
			ps.setString(8, complex.getEmail());
			ps.setInt(9, complex.getBuildingCount());
			ps.setString(10, complex.getConstructionType());
			ps.setString(11, complex.getFloorType());
			ps.setInt(12, complex.getFloors());
			ps.setInt(13, complex.getLotSize());
			ps.setString(14, complex.getNotes());

			ps.setInt(15, complex.getId());

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not updated" : "";

			return new RecordOperation(CrudType.UPDATE, complex.getId(), msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public RecordOperation updateUnit(Unit unit) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();

			String sql = "update unit" + " set complex=?," + " unit_number=?,"
					+ " type=?," + " beds=?," + "tenants=? " + " where id = ?";

			ps = connection.prepareStatement(sql);

			ps.setInt(1, unit.getComplex());
			ps.setString(2, unit.getUnitNumber());
			ps.setString(3, unit.getType());
			ps.setInt(4, unit.getBeds());
			ps.setInt(5, unit.getTenants());
			ps.setInt(6, unit.getId());

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not updated" : "";

			return new RecordOperation(CrudType.UPDATE, unit.getId(), msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public RecordOperation updateSensor(Sensor sensor) throws Exception {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = ConnectionPool.getConnection();

			String sql = "update sensor_assignment " + " set unit=?,"
					+ " role=?," + " sensor = ? " + " where id = ?";

			ps = connection.prepareStatement(sql);

			ps.setInt(1, sensor.getUnit());
			ps.setInt(2, sensor.getRole());
			ps.setInt(3, sensor.getSensor());
			ps.setInt(4, sensor.getId());

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not updated" : "";

			return new RecordOperation(CrudType.UPDATE, sensor.getId(), msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

	public ContactsDetail getComplexContacts(int complexId)
			throws Exception {
		Connection conn = null;
		List<ComplexContact> list = new ArrayList<ComplexContact>();
		PreparedStatement st = null;
		Complex complex=null;
		try {

			conn = ConnectionPool.getConnection();
			
			complex = getComplex(conn,  complexId);

			String sql = "select * from contacts where complex = ? order by full_name";
			st = conn.prepareStatement(sql);
			st.setInt(1, complexId);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				list.add(new ComplexContact(rs.getInt("id"), complexId, rs.getString("full_name"), rs
						.getString("phone_number"), rs.getString("email_address"), rs.getBoolean("notify_on_error")));
			}
		} finally {
			SqlUtilities.releaseResources(null, null, conn);
		}

		int companyId = 0;
		String complexName = null;
		if(complex!=null) {
			companyId = complex.getId();
			complexName = complex.getComplexName();
		}
		
		ContactsDetail details = new ContactsDetail(list,complexId, companyId, complexName);
		
		return details;
	}

	public RecordOperation addComplexContact(ComplexContact contact)
			throws Exception {
		
		Connection conn = null;
		PreparedStatement st = null;
		try {

			conn = ConnectionPool.getConnection();
			String sql = "insert into contacts(complex, full_name, phone_number, email_address)values(?,?,?,?)";
			st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, contact.getComplex());
			st.setString(2,  contact.getFullName());
			st.setString(3,  contact.getPhone());;
			st.setString(4,  contact.getEmailAddress());
			
			int cnt = st.executeUpdate();
			if (cnt != 1) {
				throw new Exception("Contact record could not be added");
			}
			ResultSet rs = st.getGeneratedKeys();

			int contactId = -1;
			if (rs.next()) {
				contactId = rs.getInt(1);
			} else {
				throw new Exception(
						"Could not retrieve new contact id");
			}
			return new RecordOperation(CrudType.CREATE, contactId, null);
			
		} finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
	
	
	public RecordOperation updateComplexContact(ComplexContact contact)
			throws Exception {
		
		Connection conn = null;
		PreparedStatement st = null;
		try {

			conn = ConnectionPool.getConnection();
			String sql = 
					"update contacts " +
					" set complex = ?, full_name = ?, phone_number = ?, email_address = ?, notify_on_error = ? " +
					" where id = ?";
			
			st = conn.prepareStatement(sql);
			st.setInt(1, contact.getComplex());
			st.setString(2,  contact.getFullName());
			st.setString(3,  contact.getPhone());;
			st.setString(4,  contact.getEmailAddress());
			st.setBoolean(5,  contact.isNotifyOnError());
			st.setInt(6, contact.getId());
			
			int cnt = st.executeUpdate();
			String msg = null;
			if (cnt != 1) {
				msg = "Contact record could not be updated";
			}
			return new RecordOperation(CrudType.UPDATE, contact.getId(), msg);
			
		} finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}

	public RecordOperation deleteComplexContact(int contactId) throws Exception {
		Connection conn = null;
		PreparedStatement st = null;
		try {

			conn = ConnectionPool.getConnection();
			String sql = 
					"delete from  contacts where id = ?";
			
			st = conn.prepareStatement(sql);
			st.setInt(1, contactId);
			int cnt = st.executeUpdate();
			String msg = null;
			if (cnt != 1) {
				msg = "Contact record was not deleted";
			}
			return new RecordOperation(CrudType.DELETE, contactId, msg);
			
		} finally {
			SqlUtilities.releaseResources(null, null, conn);
		}		
	}
	
	
}
