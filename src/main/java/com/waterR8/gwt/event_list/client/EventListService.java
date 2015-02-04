package com.waterR8.gwt.event_list.client;

import java.util.List;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.waterR8.gwt.event_list.client.model.WaterEvent;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("eventListServer")
public interface EventListService extends RemoteService {
	List<WaterEvent> getEventData();
	Integer deleteAllEventData();
}
