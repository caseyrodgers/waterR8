package com.waterR8.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.waterR8.model.Company;
import com.waterR8.model.CompanyDetails;
import com.waterR8.model.Complex;
import com.waterR8.model.ComplexDetails;
import com.waterR8.model.RecordOperation;
import com.waterR8.model.Sensor;
import com.waterR8.model.SensorDetails;
import com.waterR8.model.SensorEvent;
import com.waterR8.model.Unit;
import com.waterR8.model.UnitDetails;
import com.waterR8.model.RecordOperation.CrudType;
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
			return details;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception getting user home status", e);
			throw new Exception("Error getting user's home status", e);
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}
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
		return new Complex(rs.getInt("id"),
				rs.getInt("company"), rs.getString("complex_name"),
				rs.getString("address"), rs.getString("city"),
				rs.getString("state"), rs.getString("zip"),
				rs.getString("phone"), rs.getString("email"),
				rs.getInt("building_count"),
				rs.getString("construction_type"),
				rs.getString("floor_type"), rs.getInt("lot_size"),
				rs.getInt("floors"));		
	}

	public ComplexDetails getComplexDetails(int id) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			ComplexDetails details = new ComplexDetails(getComplex(connection,id), getUnits(connection, id));
			details.setCompany(getCompany(details.getComplex().getCompany()));
			return details;
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}
	}


	private List<Unit> getUnits(Connection connection, int complexId) throws Exception {
		List<Unit> units = new ArrayList<Unit>();
		PreparedStatement ps=null;
		try {
			ps = connection.prepareStatement("select * from unit where complex = ? order by unit_number");
			ps.setInt(1, complexId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				units.add(getUnitRecord(rs));
			}
			return units;
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);	
		}
	}

	private Unit getUnitRecord(ResultSet rs) throws Exception {
		return new Unit(rs.getInt("id"),  rs.getInt("complex"),  rs.getString("unit_number"),  
				rs.getString("type"),  rs.getInt("beds"),  rs.getInt("tenants"));		
	}

	private Complex getComplex(Connection connection, int id) throws Exception {
		PreparedStatement ps=null;
		try {
			String sql = "select * from complex where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				throw new Exception("Could not load complex '" + id + "'");
			}
			return getComplexRecord(rs);
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);	
		}
		
		
	}

	public UnitDetails getUnitDetails(int id) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			UnitDetails details = new UnitDetails(getUnit(connection,id));
			details.getSensors().addAll(getSensors(connection, id));
			details.setCompany(getCompanyForUnit(connection, details.getUnit().getId()));
			
			return details;
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}
		
	}

	private Collection<? extends Sensor> getSensors(Connection connection,int id) throws Exception  {
		List<Sensor> sensors = new ArrayList<Sensor>();
		PreparedStatement ps=null;
		try {
			String sql = "select * from sensor_assignment where  id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				sensors.add(getSensorRecord(rs));
			}
			return sensors;
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);	
		}		
	}

	private Sensor getSensorRecord(ResultSet rs) throws Exception {
		return new Sensor(rs.getInt("id"),  rs.getInt("unit"),  rs.getString("role"),  rs.getString("sensor"));		
	}

	private Unit getUnit(Connection connection, int id) throws Exception  {
		PreparedStatement ps=null;
		try {
			ps = connection.prepareStatement("select * from unit where id = ?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				throw new Exception("Could not load unit '" + id + "'");
			}
			return getUnitRecord(rs);
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);	
		}
		
	}

	public SensorDetails getSensorDetail(int sensorId) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			SensorDetails details = new SensorDetails(getSensor(connection, sensorId));
			details.getEvents().addAll(getSensorEvents(connection, details.getSensor()));
			Unit unit = getUnit(connection, details.getSensor().getUnit());
			details.setCompany(getCompanyForUnit(connection, unit.getId()));
			
			return details;
		} finally {
			SqlUtilities.releaseResources(null, null, connection);
		}
	}

	private Company getCompanyForUnit(Connection connection, int unit) throws Exception  {
		PreparedStatement ps=null;
		try {
			String sql = 
					 " select  c.* " +
					" from unit u  " +
					" JOIN complex x on x.id = u.complex " +  
					" JOIN company c on c.id = x.company " + 
					" where u.id = ? " +
					" order by u.unit_number ";
			
			ps = connection.prepareStatement(sql);
			ps.setInt(1, unit);
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				throw new Exception("Could not company for unit '" + unit + "'");
			}
			return getCompanyRecord(rs);
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);	
		}		
	}

	private Company getCompanyRecord(ResultSet rs) throws Exception {
		return new Company(rs.getInt("id"), rs.getString("company_name"),
				rs.getString("owner"), rs.getString("address"),
				rs.getString("city"), rs.getString("state"),
				rs.getString("zip"));		
	}

	private Collection<? extends SensorEvent> getSensorEvents(Connection connection, Sensor sensor) throws Exception {
		List<SensorEvent> events = new ArrayList<SensorEvent>();
		try {
			int sensorIdInDecimal = Integer.parseInt(sensor.getSensor());
			String sensorSerialInHex = Integer.toHexString(sensorIdInDecimal);
			
			
			// make it 8 chars long
			sensorSerialInHex = ("00000000" + sensorSerialInHex);
			sensorSerialInHex = sensorSerialInHex.substring(sensorSerialInHex.length() -8);
			
			
			// look for events with this src;
			PreparedStatement ps = null;
			
			try {
				String sql = "select * from events where src = ? order by ts desc limit 100";
				ps = connection.prepareStatement(sql);
				ps.setString(1, sensorSerialInHex);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					events.add(new SensorEvent(rs.getInt("id"), rs.getString("json")));
				}
				return events;
			}
			finally {
				SqlUtilities.releaseResources(null,  ps,  null);
			}
		}
		catch(Exception e) {
			throw new Exception("Error extracting sensor serial number '" + sensor.getSensor() + "'", e);
		}
	}

	private Sensor getSensor(Connection connection, int id) throws Exception  {
		PreparedStatement ps=null;
		try {
			String sql = "select * from sensor_assignment where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				throw new Exception("Could not load sensor '" + id + "'");
			}
			return getSensorRecord(rs);
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);	
		}		
	}

	public RecordOperation addCompany(Company company) throws Exception {
		Connection connection = null;
		PreparedStatement ps=null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "insert into company(company_name, address, city, state, zip)values(?,?,?,?,?)";
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, company.getCompanyName());
			ps.setString(2, company.getAddress());
			ps.setString(3, company.getCity());
			ps.setString(4, company.getState());
			ps.setString(5, company.getZip());
			
			int cnt = ps.executeUpdate();
			if(cnt != 1) {
				throw new Exception("Company record could not be added: " + company);
			}
			ResultSet rs = ps.getGeneratedKeys();
			
			int companyId=-1;
			if (rs.next()) {
				companyId = rs.getInt(1);
		    } else {
		    	throw new Exception("Could not retreive new company id : " + company);
		    }
			return new RecordOperation(CrudType.CREATE, companyId, null);
			
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}		
	}

	public RecordOperation deleteCompany(int id) throws Exception {
		assert(id > 0);
		
		Connection connection = null;
		PreparedStatement ps=null;
		try {
			connection = ConnectionPool.getConnection();
			String sql = "delete from company where id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1,  id);
			
			int cnt = ps.executeUpdate();
			String msg=cnt!=1?"Record not deleted":"";
			return new RecordOperation(CrudType.DELETE, id, msg);
			
		} finally {
			SqlUtilities.releaseResources(null, ps, connection);
		}		

	}

}
