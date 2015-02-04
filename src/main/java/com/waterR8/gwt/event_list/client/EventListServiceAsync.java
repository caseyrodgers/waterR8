package com.waterR8.gwt.event_list.client;

import java.util.List;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.waterR8.gwt.event_list.client.model.WaterEvent;

public interface EventListServiceAsync {
	void getEventData(AsyncCallback<List<WaterEvent>> callback);

	void deleteAllEventData(AsyncCallback<Integer> asyncCallback);
}
