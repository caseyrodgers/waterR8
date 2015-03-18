package com.waterR8.server;

import javax.servlet.http.HttpServlet;

import com.waterR8.server.watch.EventWatcher;

/** Do any setup needed to run the WaterR8 admin server
 * 
 * @author casey
 *
 */
public class InitializationServlet extends HttpServlet {
	@Override
	public void init() {
		try {
			new EventWatcher();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

