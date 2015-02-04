package com.waterR8.gwt.event_list.client.ui;

import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.Handler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.waterR8.gwt.event_list.client.model.WaterEvent;

public class EventTable extends SimplePanel {
	
	public EventTable(final List<WaterEvent> dataRecords) {
		// Create a CellTable.
		CellTable<WaterEvent> table = new CellTable<WaterEvent>(25,new ProvidesKey<WaterEvent>() {
			public Object getKey(WaterEvent item) {
				return item.getId();
			}
		});
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		
		// Add a number cell to show device id
		TextColumn<WaterEvent> recCol = new TextColumn<WaterEvent>() {
			@Override
			public String getValue(WaterEvent object) {
				return object.getId() + "";
			}
		};
		table.addColumn(recCol, "Rec ID");
		
		
		// Add a date column to show the time stamp.
		DateCell dateCell = new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM));
		Column<WaterEvent, Date> dateColumn = new Column<WaterEvent, Date>(dateCell) {
			@Override
			public Date getValue(WaterEvent object) {
				return object.getTimeStamp();
			}
		};
		table.addColumn(dateColumn, "Time Stamp");
		
		
		TextColumn<WaterEvent> devIdColumn = new TextColumn<WaterEvent>() {
			@Override
			public String getValue(WaterEvent object) {
				return "" + object.getUniqueDevId();
			}
		};
		table.addColumn(devIdColumn, "Device ID");

		
		TextColumn<WaterEvent> eventIdColumn = new TextColumn<WaterEvent>() {
			@Override
			public String getValue(WaterEvent object) {
				return "" + object.getEventId();
			}
		};
		table.addColumn(eventIdColumn, "Event ID");
		
		
		// Add a number cell to show device id
		NumberCell durationCell = new NumberCell();
		Column<WaterEvent, Number> durationCol = new Column<WaterEvent, Number>(durationCell) {
			@Override
			public Number getValue(WaterEvent object) {
				return object.getDuration();
			}
		};
		table.addColumn(durationCol, "Duration");

		// Add a number cell to show device id
		NumberCell batVoltCell = new NumberCell(NumberFormat.getFormat("0.000V"));
		Column<WaterEvent, Number> batVoltCol = new Column<WaterEvent, Number>(batVoltCell) {
			@Override
			public Number getValue(WaterEvent object) {
				return object.getBatteryVolts();
			}
		};
		table.addColumn(batVoltCol, "Battery Voltage");

		NumberCell rfRssiCell = new NumberCell();
		Column<WaterEvent, Number> rfRssiCol = new Column<WaterEvent, Number>(rfRssiCell) {
			@Override
			public Number getValue(WaterEvent object) {
				return object.getRfRssi();
			}
		};
		table.addColumn(rfRssiCol, "RF RSSI");
		

		table.getColumn(0).setSortable(true);
		
		    		
		table.addColumnSortHandler(new Handler() {
			public void onColumnSort(ColumnSortEvent event) {
				System.out.println("Sorting ..");
			}
		});
		
		

		// Add a selection model to handle user selection.
		final SingleSelectionModel<WaterEvent> selectionModel = new SingleSelectionModel<WaterEvent>();
		table.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						WaterEvent selected = selectionModel.getSelectedObject();
						if (selected != null) {
							Window.alert("You selected: " + selected.toString());
						}
					}
				});
		
		
		
		// Associate an async data provider to the table
	    // XXX: Use AsyncCallback in the method onRangeChanged
	    // to actaully get the data from the server side
	    AsyncDataProvider<WaterEvent> provider = new AsyncDataProvider<WaterEvent>() {
	      @Override
	      protected void onRangeChanged(HasData<WaterEvent> display) {
	        int start = display.getVisibleRange().getStart();
	        int end = start + display.getVisibleRange().getLength();
	        
	        end = end >= dataRecords.size() ? dataRecords.size() : end;
	        
	        List<WaterEvent> sub = dataRecords.subList(start, end);
	        updateRowData(start, sub);
	      }
	    };
	    provider.addDataDisplay(table);
	    provider.updateRowCount(dataRecords.size(), true);

	    
	    
	    

		// Set the total row count. This isn't strictly necessary,
		// but it affects paging calculations, so its good habit to
		// keep the row count up to date.
		table.setRowCount(dataRecords.size(), true);

		// Push the data into the widget.
		table.setRowData(0, dataRecords);

		VerticalPanel panel = new VerticalPanel();
		panel.setBorderWidth(1);
		panel.add(table);

		SimplePager sPage = new SimplePager();
		sPage.setDisplay(table);
		panel.add(sPage);
		
		// Add the widgets to the root panel.
		setWidget(panel);
	}

}
