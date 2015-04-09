	package com.waterR8.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.waterR8.model.Complex;
import com.waterR8.model.Gateway;
import com.waterR8.model.GatewayCommand;
import com.waterR8.model.GatewayCommandResponse;
import com.waterR8.model.OperationStatus;
import com.waterR8.model.RecordOperation;
import com.waterR8.model.RecordOperation.CrudType;
import com.waterR8.server.ConnectionPool;
import com.waterR8.util.SqlUtilities;

public class GatewayDao {

	static private GatewayDao __instance;
	static public GatewayDao getInstance() {
		if(__instance == null) {
			__instance = new GatewayDao();
		}
		return __instance;
	}
	
	private GatewayDao() {}

	

	public List<Gateway> getAvailableGateways(int complexId) throws Exception {
		List<Gateway> gateways = new ArrayList<Gateway>();
		
		Connection conn=null;
		PreparedStatement ps = null;
		try {
			conn = ConnectionPool.getConnection();
			String sql = 
					 "select g.*, c.id as complex_id " +
					 "from gateway g " +
					 "  LEFT JOIN complex c on c.gateway_sn = g.sn " +
					 "where sn is not null " +
					 "and    c.id is null ";
			
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Gateway gateway = new Gateway(rs.getInt("sn"), rs.getString("mac_addr"), rs.getString("ip"));
				gateways.add(gateway);
			}
			return gateways;
		}
		finally {
			SqlUtilities.releaseResources(null,  ps,  conn);
		}
	}


	/** Register a new GatewayCommand
	 * 
	 *  
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public GatewayCommandResponse makeCommandRequest(GatewayCommand command) throws Exception {
		
		
		if(command.getGateway() == null) {
			throw new Exception("Gateway must be specified");
		}
		
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn = ConnectionPool.getConnection();
			
			
			/** delete old message that have not been
			 * processed in 1 hour
			 */
			String sqlDel = 
					"delete from gw_message " +
                    " where request_time < now() - interval '1 hours'";
			conn.createStatement().executeUpdate(sqlDel);
			
			String sql = "insert into gw_message(gateway, action, request_time)values(?,?,now())";
			ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			ps.setInt(1, command.getGateway().getSn());
			ps.setInt(2, command.getCommand().ordinal());
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();

			int requestId = -1;
			if (rs.next()) {
				requestId = rs.getInt(1);
			} else {
				throw new Exception(
						"Could not retreive new request_id : " + requestId);
			}

			return new GatewayCommandResponse(command, requestId, new Date());
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			SqlUtilities.releaseResources(null,  ps,  conn);
		}
	}

	
	public OperationStatus removeCommandRequest(int requestId) throws Exception {
		
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn = ConnectionPool.getConnection();
			
			String sql = "delete from gw_message where id = ?";
			ps = conn.prepareStatement(sql);
			
			ps.setInt(1, requestId);
			int cnt = ps.executeUpdate();

			return new OperationStatus(cnt==1?"OK":"FAILED");
		}
		finally {
			SqlUtilities.releaseResources(null,  ps,  conn);
		}
	
		
		
	}

	/** send command request to all gateways defined in complex
	 * 
	 * returns a list of responses
	 * 
	 * 
	 * @param complexId
	 * @param commandId
	 * @return
	 * @throws Exception
	 */
	public GatewayCommandResponse makeCommandRequest(int complexId, int commandId) throws Exception {
		Connection conn=null;
		try {
			conn = ConnectionPool.getConnection();
			Complex complex = CompanyDao.getInstance().getComplex(conn, complexId);
			
			GatewayCommand command = new GatewayCommand(complex.getGateway(), commandId);
			GatewayCommandResponse r = makeCommandRequest(command);
			return r;
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}

	private List<Gateway> getComplexGateways(int complexId) throws Exception {
		
		List<Gateway> gateways = new ArrayList<Gateway>();
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			String sql = 
					" select g.id, g.mac_addr, g.ip " +
					" from complex_gateway cg " +
					"  join gateway g on g.id = cg.gateway " +
					" where cg.complex = ? ";
			
			conn = ConnectionPool.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setInt(1,  complexId);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String macAddr = rs.getString("mac_addr");
				String ipAddr = rs.getString("ip");
				
				gateways.add(new Gateway(id, macAddr, ipAddr));
			}
			return gateways;
		}
		finally {
			SqlUtilities.releaseResources(null, ps, conn);
		}
	}

	/** Set the serialnumber in the gateway table
	 * 
	 * @param complexId
	 * @param gateway
	 * @return
	 * @throws Exception
	 */
	public RecordOperation updateComplexGateway(int complexId, Gateway gateway) throws Exception {
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn = ConnectionPool.getConnection();
			
			if(gateway.getSn() > 0) {
				String sql =
						"update complex set gateway_sn = ? where id = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1,  gateway.getSn());
				ps.setInt(2,  complexId);
			}
			else {
				String sql =
						"update complex set gateway_sn = null where id = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1,  complexId);
			}
			int updated = ps.executeUpdate();
			String message= updated==1?null:"not updated";
			return new RecordOperation(CrudType.UPDATE, complexId, message);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
	
	
}
