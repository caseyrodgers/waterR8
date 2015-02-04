package com.waterR8.gwt.event_list.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.waterR8.gwt.event_list.client.model.WaterEvent;
import com.waterR8.gwt.event_list.client.ui.EventTable;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EventList implements EntryPoint {

	static {
		setupService();
	}

	SimplePanel _main = new SimplePanel();

	public void onModuleLoad() {
		_main.setWidget(new HTML("<b>Loading ...</b>"));
		RootPanel.get().add(_main);

		getDataFromServer();

	}
	
	private void clearServerDataAndRefresh() {
		getService().deleteAllEventData(new AsyncCallback<Integer>() {
			public void onSuccess(Integer count) {
				buildUi(new ArrayList<WaterEvent>());
			}

			public void onFailure(Throwable caught) {
				Window.alert("ERROR: " + caught);
			}
		});
	}

	private void getDataFromServer() {
		getService().getEventData(new AsyncCallback<List<WaterEvent>>() {
			public void onSuccess(List<WaterEvent> result) {
				buildUi(result);
			}

			public void onFailure(Throwable caught) {
				Window.alert("ERROR: " + caught);
			}
		});
	}

	protected void buildUi(List<WaterEvent> result) {
		DockPanel docPanel = new DockPanel();
		HorizontalPanel hPan = new HorizontalPanel();
		hPan.addStyleName("toolbar");
		hPan.add(new Button("Refresh", new ClickHandler() {
			public void onClick(ClickEvent event) {
				getDataFromServer();
			}
		}));

		hPan.add(new Button("Reset server table", new ClickHandler() {

			public void onClick(ClickEvent event) {
				resetServer();
			}
		}));

		docPanel.add(new HTML("<img src='/images/waterR8_logo.jpg'/>"), DockPanel.NORTH);
		docPanel.add(new HTML("<h1>Water Events</h1>"), DockPanel.NORTH);
		docPanel.add(hPan, DockPanel.NORTH);
		docPanel.add(new EventTable(result), DockPanel.CENTER);
		_main.setWidget(docPanel);
	}

	protected void resetServer() {
		boolean ans = Window
				.confirm("Are you sure you want to clear ALL records from the database?");
		if (ans) {
			clearServerDataAndRefresh();
		}
	}

	static EventListServiceAsync _rpcService;

	static EventListServiceAsync getService() {
		return _rpcService;
	}

	static private void setupService() {
		String point = GWT.getModuleBaseURL();
		_rpcService = (EventListServiceAsync) GWT
				.create(EventListService.class);
		GWT.log("RPC Service created: " + point);
	}
}
