package com.waterR8.gwt.event_list.server.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.waterR8.dao.DataRecordDao;
import com.waterR8.gwt.event_list.client.EventListService;
import com.waterR8.gwt.event_list.client.model.WaterEvent;

public class EventListServiceImpl extends RemoteServiceServlet implements EventListService {

	static Logger __logger = Logger.getLogger(EventListServiceImpl.class);
	
	public List<WaterEvent> getEventData() {
		try {
			__logger.debug("Reading event data from server");
			return DataRecordDao.getInstance().getDataRecords();
		}
		catch(Exception e) {
			__logger.error("Error reading events", e);
		}
		
		return null;
	}
	
	public Integer deleteAllEventData() {
		try {
			return new Integer(DataRecordDao.getInstance().deleteAllEventData());
		}
		catch(Exception e) {
			__logger.error("Error deleting event data", e);
		}
		return 0;
	}

}
