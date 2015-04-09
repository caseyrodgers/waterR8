package com.waterR8.server.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.waterR8.model.ComplexContact;
import com.waterR8.model.SensorDetails;
import com.waterR8.model.SensorEvent;
import com.waterR8.model.SensorEvent.EventType;
import com.waterR8.model.SensorRecord;
import com.waterR8.server.ConnectionPool;
import com.waterR8.server.dao.CompanyDao;
import com.waterR8.server.mail.MailManager;
import com.waterR8.util.SqlUtilities;

/** Monitor sensor events and notify
 *  registered users when one occurrs
 *  
 * @author casey
 *
 */
public class SensorEventWatcher {
	
	private static SensorEventWatcher __instance;
	static public SensorEventWatcher getInstance() {
		if(__instance == null) {
			__instance = new SensorEventWatcher();
		}
		return __instance;
	}
	
	private static Logger __logger = Logger.getLogger(SensorEventWatcher.class.getName()); 
	public SensorEventWatcher() {
		startWatch(1000 * 10);
	}

	boolean done;
	private void startWatch(final int interval) {
		__logger.info("Starting watcher with interval '" + interval + "'");
		Thread thread = new Thread() {
			public void run() {
				while(!done) {
				   	__logger.info("Checking for events: " + new Date());
				   	
				   	Connection conn=null;
				   	try {
				   		conn = ConnectionPool.getConnection();
				   		findAllUndeliveredEvents(conn);
				   	}
				   	catch(Exception e) {
				   		__logger.log(Level.SEVERE,  "Error finding events",  e);
				   	}
				   	finally {
				   		SqlUtilities.releaseResources(null, null, conn);
				   	}
				   	
				   	try {
				   		Thread.sleep(interval);
				   	}
				   	catch(Exception e) {
				   		__logger.log(Level.SEVERE, "Error watching for events",  e);
				   	}
				}
			}
		};
		thread.start();
	}
	
	/** Lookup events that have not already fired
	 * where 'not fired' means an email has not been 
	 * sent.
	 * 
	 * So, every event that is 'fired' means that event's
	 * id is added to the event_fired table.  
	 * 
	 * So, any event found in the 'event_fired' table will be 
	 * assumed to have already been fired and it's related 
	 * complex contacts will have been notified.
	 * @param conn 
	 * 
	 */
	protected void findAllUndeliveredEvents(Connection conn) throws Exception {
		List<SensorEvent> eventsToBeSent = new ArrayList<SensorEvent>();
		for(SensorEventCheck check: checks) {
			eventsToBeSent.addAll(lookupEventsNotFiredFor(conn, check));
		}
		
		if(eventsToBeSent.size() > 0) {
			int cntDelivered = deliverEventsToComplexContacts(conn, eventsToBeSent);
			if(cntDelivered == 0) {
				__logger.warning("Events not delivered: " + eventsToBeSent.size());
			}
		}
		else {
			__logger.info("no events found to deliver");
		}
		
	}
	
	private int deliverEventsToComplexContacts(Connection conn, List<SensorEvent> eventsToBeSent) throws Exception {
		int cntDelivered=0;
		for(SensorEvent event: eventsToBeSent) {
			List<ComplexContact> contacts = getUnnotifiedContactsAssociatedWithEvent(conn, event);
			if(contacts.size() == 0) {
				__logger.finest("No contacts found for sensor event: " + event);
			}
			else {
				deliverEventToContacts(conn, event, contacts);
				cntDelivered++;
			}
		}
		return cntDelivered;
	}

	private void deliverEventToContacts(Connection conn, SensorEvent event,List<ComplexContact> contacts) throws Exception {
		for(ComplexContact contact: contacts) {
			__logger.info("Sending event notification '" + event.getData().getId() + "' to: " + contact.getEmailAddress());
			
			String emailSubject = "WaterR8 Event Alert: " + new Date();
			String emailText = "The following sensor had a duration of 1 hour or more: ";
			emailText += "\n\n";
			
			SensorDetails sd = CompanyDao.getInstance().getSensorDetail(event.getData().getSrc());
			emailText += "Unit Number: " + sd.getUnit().getUnitNumber() + "\n";
			emailText += "Sensor Serial Number: " + sd.getSensor().getSensor();
			emailText += "Duration: " + event.getData().getDur();
			
			MailManager.getInstance().sendEmail("caseyrodgers@gmail.com","admin@waterr8.com",emailSubject, emailText);
			
			markEventAsBeingSentToContact(conn, event, contact);
		}
	}

	private void markEventAsBeingSentToContact(Connection conn,SensorEvent event, ComplexContact contact) throws Exception {
		PreparedStatement ps=null;
		try {
			String sql="insert into sensor_alert_sent(contact, event, sent_date)values(?,?,now())";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, contact.getId());
			ps.setInt(2, event.getData().getId());
			if(ps.executeUpdate() != 1) {
				throw new Exception("Could not mark contact as being sent for unknown reasons!");
			}
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	private List<ComplexContact> getUnnotifiedContactsAssociatedWithEvent(Connection conn, SensorEvent event) throws Exception {
		List<ComplexContact> contacts = new ArrayList<ComplexContact>();
		String sql = 
            "select ct.* " +
            " from   contacts ct  " + 
            " JOIN complex c on c.id = ct.complex  " +
            " JOIN unit u on u.complex = c.id  " +
            " JOIN sensor_assignment sa on sa.unit = u.id  " +
            " where sa.sn = ? " +
            " and ct.id not in ( " +
            " 		  select contact " +
            " 		  from sensor_alert_sent " +
            " 		  where event = ? " +
            " 		  ) " +
            " order by full_name ";
		PreparedStatement ps=null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1,  event.getData().getSrc());
			ps.setInt(2, event.getData().getId());
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				contacts.add(CompanyDao.getInstance().getComplexContactRecord(rs));
			}
			
			return contacts;
		}
		finally {
		   SqlUtilities.releaseResources(null,  ps,  null); 
		}
		
		
	}

	private List<SensorEvent> lookupEventsNotFiredFor(Connection conn,SensorEventCheck check) throws Exception {
		return check.lookForEvents(conn);
	}

	interface SensorEventCheck {
		/** look for events not yet fired
		 * 
		 * @param conn
		 * @return
		 */
		List<SensorEvent> lookForEvents(Connection conn) throws Exception;
	}
	
	
	/** look for events that have a long duration
	 * 
	 * @author casey
	 *
	 */
	class SensorEventCheck_LongDuration implements SensorEventCheck {

		int MAX_DURATION=3599;
		public List<SensorEvent> lookForEvents(Connection conn) throws Exception {
			List<SensorEvent> events = new ArrayList<SensorEvent>();
			
			String sql = 
					"select e.* " +
			        " from events e " + 
					" where type = 2 " + 
			        " and dur > " + MAX_DURATION ;
			Statement st=null;
			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(sql);
				while(rs.next()) {
					SensorRecord sr = CompanyDao.getInstance().getSensorEventRecord(rs);
					events.add(new SensorEvent(EventType.LONG_DURATION, sr));
				}
			}
			finally {
				SqlUtilities.releaseResources(null,  st,  null);
			}
			return 	events;
		}
	}
	SensorEventCheck checks[] = {new SensorEventCheck_LongDuration()};

	public void cancelThread() {
		this.done = true;
	}
	
	
	static public void main(String as[]) {
		try {
			new SensorEventWatcher();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
