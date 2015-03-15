package com.waterR8.server.watch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.waterR8.EventCheckException;
import com.waterR8.model.EventCheck;
import com.waterR8.util.SqlUtilities;

public class EventCheckSanityCheck implements EventCheck {

	public String getName() {
		return this.getClass().getName();
	}
	
	public String doCheck(Connection conn) throws EventCheckException {
		PreparedStatement ps=null;
		try {
			String sql = "select max(ts) from events";
			ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			rs.next();
			Timestamp ts = rs.getTimestamp(1);
			
			long lastTime = ts.getTime();
			
			long timeNow = System.currentTimeMillis();
			
			long timeDiff = timeNow - lastTime;
			
			if(timeDiff > (1000*60*60)) {
				return "No event for one hour";
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return e.getMessage();
		}
		finally {
			SqlUtilities.releaseResources(null,null, conn);
		}

		return null; // all good
	}

}
