package com.waterR8.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.waterR8.model.Company;
import com.waterR8.model.CompanyDetails;
import com.waterR8.model.CompanyNetworkMap;
import com.waterR8.model.Complex;
import com.waterR8.model.ComplexDetails;
import com.waterR8.model.NetworkGraphNode;
import com.waterR8.model.NetworkGraphNode.Type;
import com.waterR8.model.NetworkNode;
import com.waterR8.model.RecordOperation;
import com.waterR8.model.RecordOperation.CrudType;
import com.waterR8.model.Sensor;
import com.waterR8.model.SensorDetails;
import com.waterR8.model.SensorEvent;
import com.waterR8.model.SensorNetworkStatus;
import com.waterR8.model.Unit;
import com.waterR8.model.UnitDetails;
import com.waterR8.server.ConnectionPool;
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
			String sql = " select count(*) as event_count "
					+ " from events"
					+ " where src in ("
					+ "     select right(concat('00000000', to_hex(CAST(coalesce(sa.sensor, '0') AS integer))), 8) as sensor_src"
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

			sql += ")";

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
			String sql = "select * from complex where company = ? order by complex_name";
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
		return new Complex(rs.getInt("id"), rs.getInt("company"),
				rs.getString("complex_name"), rs.getString("address"),
				rs.getString("city"), rs.getString("state"),
				rs.getString("zip"), rs.getString("phone"),
				rs.getString("email"), rs.getInt("building_count"),
				rs.getString("construction_type"), rs.getString("floor_type"),
				rs.getInt("lot_size"), rs.getInt("floors"),
				rs.getString("notes"));
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
			ps = connection
					.prepareStatement("select * from unit where complex = ? order by unit_number");
			ps.setInt(1, complexId);
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
		return new Unit(rs.getInt("id"), rs.getInt("complex"),
				rs.getString("unit_number"), rs.getString("type"),
				rs.getInt("beds"), rs.getInt("tenants"));
	}

	private Complex getComplex(Connection connection, int id) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "select * from complex where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new Exception("Could not load complex '" + id + "'");
			}
			return getComplexRecord(rs);
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}

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

			return details;
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}

	}

	private Collection<? extends Sensor> getSensors(Connection connection,
			int unitId) throws Exception {
		List<Sensor> sensors = new ArrayList<Sensor>();
		PreparedStatement ps = null;
		try {
			String sql = "select * from sensor_assignment where unit = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, unitId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				sensors.add(getSensorRecord(rs));
			}
			return sensors;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	private Sensor getSensorRecord(ResultSet rs) throws Exception {
		String ssn = rs.getString("sensor");
		Sensor sensor = new Sensor(rs.getInt("id"), rs.getInt("unit"),
				rs.getString("role"), ssn);
		
		sensor.setSensorHex(convertSensorIntSerialSsnToHex(ssn));
		
		return sensor;
	}

	private Unit getUnit(Connection connection, int id) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("select * from unit where id = ?");
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

	 
	// look for events with this src;
	private Collection<? extends SensorEvent> getSensorEvents(
			Connection connection, Sensor sensor) throws Exception {
		List<SensorEvent> events = new ArrayList<SensorEvent>();
	
		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		
		try {
			String sensorSerialInHex = convertSensorIntSerialSsnToHex(sensor.getSensor());

			PreparedStatement ps = null;

			try {
				String sql = "select * from events where src = ? order by ts desc limit 100";
				ps = connection.prepareStatement(sql);
				ps.setString(1, sensorSerialInHex);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {

					String src = rs.getString("src");
					int seq = rs.getInt("seq");
					int hopCnt = rs.getInt("hopcnt");
					int first = rs.getInt("first");
					String json = rs.getString("json");
					String type = rs.getString("type");
					int battery = rs.getInt("bat");
					long time = rs.getTimestamp("ts").getTime();

					
					String timeStamp = format.format(new Date(time));
					events.add(new SensorEvent(rs.getInt("id"), json,type, timeStamp,time,src, seq,
							hopCnt, first, battery));
				}
			} finally {
				SqlUtilities.releaseResources(null, ps, null);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error getting sensor/repeater data", e);
		}

		return events;
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
	private String convertSensorIntSerialSsnToHex(String ssnInDecAsStr) {
		int ssnInDec = Integer.parseInt(ssnInDecAsStr);
		String ssnInHex = Integer.toHexString(ssnInDec);

		// make it 8 chars long
		ssnInHex = ("00000000" + ssnInHex);
		ssnInHex = ssnInHex.substring(ssnInHex
				.length() - 8);

		return ssnInHex;
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
			String sql = "insert into sensor_assignment(unit,role,sensor)values(?,?,?)";
			ps = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, sensor.getUnit());
			ps.setString(2, sensor.getRole());
			ps.setString(3, sensor.getSensor());

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
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
		return null;
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
			NetworkGraphNode rootNode = new NetworkGraphNode(Type.ROOT, 0,
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
					complexId, "Complex: " + complexName);
			networkMap.add(new NetworkNode(rootNode, complexNode));

			psUnit = connection
					.prepareStatement("select id, unit_number from unit where complex = ?");
			psUnit.setInt(1, complexId);
			ResultSet rsUnit = psUnit.executeQuery();

			while (rsUnit.next()) {
				String unitNumber = rsUnit.getString("unit_number");
				int unitId = rsUnit.getInt("id");
				NetworkGraphNode unitNode = new NetworkGraphNode(Type.UNIT,
						unitId, "Unit: " + unitNumber);
				networkMap.add(new NetworkNode(complexNode, unitNode));

				psSensor = connection
						.prepareStatement("select id, sensor from sensor_assignment where unit = ?");
				psSensor.setInt(1, unitId);

				ResultSet rsSensor = psSensor.executeQuery();

				while (rsSensor.next()) {
					String sensor = rsSensor.getString("sensor");
					int sensorId = rsSensor.getInt("id");

					NetworkGraphNode sensorNode = new NetworkGraphNode(
							Type.SENSOR, sensorId, "Sensor: " + sensor);
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
	 * TODO: combine all getMap into single dynamic one
	 * 
	 * @param unitId
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
			NetworkGraphNode rootNode = new NetworkGraphNode(Type.ROOT, 0,
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
					"Unit: " + unitNumber);
			networkMap.add(new NetworkNode(rootNode, unitNode));

			psSensor = connection
					.prepareStatement("select id, sensor from sensor_assignment where unit = ?");
			psSensor.setInt(1, unitId);

			ResultSet rsSensor = psSensor.executeQuery();

			while (rsSensor.next()) {
				String sensor = rsSensor.getString("sensor");
				int sensorId = rsSensor.getInt("id");

				NetworkGraphNode sensorNode = new NetworkGraphNode(Type.SENSOR,
						sensorId, "Sensor: " + sensor);
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

	public CompanyNetworkMap getCompanyMapForCompany(int companyId)
			throws Exception {

		List<NetworkNode> networkMap = new ArrayList<NetworkNode>();

		CompanyNetworkMap companyMap = new CompanyNetworkMap();

		Connection connection = null;
		PreparedStatement psCompany = null;
		PreparedStatement psComplex = null;
		PreparedStatement psUnit = null;
		PreparedStatement psSensor = null;

		try {

			List<NetworkGraphNode> sensors = new ArrayList<NetworkGraphNode>();

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
			NetworkGraphNode rootNode = new NetworkGraphNode(Type.ROOT, 0,
					"Root");
			NetworkGraphNode companyNode = new NetworkGraphNode(Type.COMPANY,
					companyId, "Company: " + companyName);
			networkMap.add(new NetworkNode(rootNode, companyNode));

			psComplex = connection
					.prepareStatement("select id, complex_name from complex where company = ?");
			psComplex.setInt(1, companyId);

			ResultSet rsComplex = psComplex.executeQuery();
			while (rsComplex.next()) {

				String complexName = rsComplex.getString("complex_name");
				int complexId = rsComplex.getInt("id");

				NetworkGraphNode complexNode = new NetworkGraphNode(
						Type.COMPLEX, complexId, "Complex: " + complexName);
				networkMap.add(new NetworkNode(companyNode, complexNode));

				psUnit = connection
						.prepareStatement("select id, unit_number from unit where complex = ?");
				psUnit.setInt(1, complexId);
				ResultSet rsUnit = psUnit.executeQuery();
				while (rsUnit.next()) {
					String unitNumber = rsUnit.getString("unit_number");
					int unitId = rsUnit.getInt("id");
					NetworkGraphNode unitNode = new NetworkGraphNode(Type.UNIT,
							unitId, "Unit: " + unitNumber);
					networkMap.add(new NetworkNode(complexNode, unitNode));

					psSensor = connection
							.prepareStatement("select id, sensor from sensor_assignment where unit = ?");
					psSensor.setInt(1, unitId);

					ResultSet rsSensor = psSensor.executeQuery();

					while (rsSensor.next()) {
						String sensor = rsSensor.getString("sensor");
						int sensorId = rsSensor.getInt("id");

						NetworkGraphNode sensorNode = new NetworkGraphNode(
								Type.SENSOR, sensorId, "Sensor: " + sensor);
						networkMap.add(new NetworkNode(unitNode, sensorNode));

						sensors.add(sensorNode);
					}
				}
			}

			fixupSensorLastValues(connection, sensors);

			companyMap.setNetworkNodes(networkMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, psCompany, connection);
			SqlUtilities.releaseResources(null, psComplex, null);
			SqlUtilities.releaseResources(null, psUnit, null);
			SqlUtilities.releaseResources(null, psSensor, null);
		}

		return companyMap;
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
			String sql = "SELECT sa.id as sensor_id, src,hopcnt,bat,rssi,dur "
					+ "from events e  "
					+ " JOIN sensor_assignment sa on right(concat('00000000', to_hex(CAST(coalesce(sa.sensor, '0') AS integer))), 8) = e.src "
					+ " where sa.id in "
					+ inList 
					+ " order by sa.id, ts desc";
				
			ps = connection.prepareStatement(sql);

			List<Integer> seen = new ArrayList<Integer>();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				int sensorId = rs.getInt("sensor_id");
				
				// is this one already record
				if(seen.size() > 0) {
					if(seen.get(seen.size()-1) == sensorId) {
						continue;  // ready in list
					}
				}
				
				seen.add(sensorId);
				
				int hop = rs.getInt("hopcnt");
				int bat = rs.getInt("bat");
				int rssi = rs.getInt("rssi");
				int dur = rs.getInt("dur");

				boolean found = true;
				for (NetworkGraphNode n : sensors) {
					if (n.getId() == sensorId) {
						String subLabel = "h: " + hop + ",b:" + bat + ",r: "
								+ rssi + ",d:" + dur;
						n.setSubLabel(subLabel);
						break;
					}
				}
				
			}
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
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
					+ "complex_name=?," + "address=?," + "city=?," + "state=?,"
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
			ps.setString(2, sensor.getRole());
			ps.setString(3, sensor.getSensor());
			ps.setInt(4, sensor.getId());

			int cnt = ps.executeUpdate();
			String msg = cnt != 1 ? "Record not updated" : "";

			return new RecordOperation(CrudType.UPDATE, sensor.getId(), msg);

		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}
	}

}
