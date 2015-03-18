package com.waterR8.server.watch;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.waterR8.model.EventCheck;
import com.waterR8.server.ConnectionPool;
import com.waterR8.util.SqlUtilities;
import com.waterR8.util.WaterR8Properties;


/** Class to monitor DB events looking for anomalies
 *  and notifiy any relevant contacts when problems occur.
 *  
 * @author casey
 *
 */
public class EventWatcher {

	static Logger __logger = Logger.getLogger(EventWatcher.class.getName());
	
	private static EventWatcher __instance;
	public static EventWatcher getLastInstance() throws Exception {
		if(__instance == null) {
			__instance = new EventWatcher();
		}
		return __instance;
	}
	
	public EventWatcher() throws Exception {
		
		installStandardChecks();
		startWatcherThread();
	}

	
	private void installStandardChecks() {
		registeredChecks.add(new EventCheckSanityCheck());
	}


	MyThread _watcherThread;

	private List<EventCheck> registeredChecks = new ArrayList<EventCheck>();
	
	
	private void startWatcherThread() throws Exception {
		if(_watcherThread != null) {
			_watcherThread.setStop(true);
		}
		int interval = Integer.parseInt(WaterR8Properties.getInstance().getProperty("events.check.interval", Integer.toString(1000*60*10)));
		_watcherThread = new MyThread(interval) {
			
			@Override
			public void doRun() {
				
				__logger.info("Checking for waterR8 database events");
				
				doDatabaseChecks();
			}
		};

		_watcherThread.start();
	}
	

	/** do checks, first check that throws
	 *  an exception stop the process and no
	 *  other check is performed.
	 */
	private void doDatabaseChecks() {
		Connection conn=null;
		try {
			conn = ConnectionPool.getConnection();
			for(EventCheck check: registeredChecks) {
				__logger.info("Running check: " + check.getName());
				String response = check.doCheck(conn);
				if(response != null) {
					sendResponseToContacts(check.getName(),response);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			__logger.severe("Error checking running checks: " + e.getMessage());
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
		
	}

	private void sendResponseToContacts(String title, String response) {
		__logger.info(title + ": " + response);
	}
	

	abstract class MyThread extends Thread {
		boolean stopThread;
		private int interval;
		
		public MyThread(int interval) {
			this.interval = interval;
		}
		
		public void setStop(boolean stop) {
			this.stopThread = stop;
		}
		
		abstract public void doRun();
		
		@Override
		public void run() {
			while(!stopThread) {
				
				doRun();
				
				try {
					Thread.sleep(interval);
				}
				catch(Exception e) {
					e.printStackTrace();
					__logger.severe("Exception occurred in thread: " + this + ", " + e.getMessage());
				}
			}
			__logger.info("Thread has stopped: " + this);
		}
	}

	static public void main(String as[]) {
		
		try {
			new EventWatcher();
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

