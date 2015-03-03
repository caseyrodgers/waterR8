package com.waterR8.model;


public class NetworkDevice {
	
	public static enum Role{
		UNKNONW("Unknown"), FLOW_TIMER("Flow Timer"), REPEATER("Repeater"), GATEWAY("Gateway"), COMPANY("Company"), COMPLEX("Complex"), UNIT("Unit");
	
		private String label;

		private Role(String name) {
			this.label = name;
		}
		
		public String getLabel() {
			return this.label;
		}

		/** return role type, or default of sensor;
		 * 
		 * @param role
		 * @return
		 */
		public static Role lookup(String roleLabel) {
			try {
				for(int i=0;i<values().length;i++) {
					Role r = values()[i];
					if(r.getLabel().equals(roleLabel)) {
						return r;
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return Role.FLOW_TIMER;
		}
	}
	
	int id;
	int unit;
	int role;
	String roleLabel;
	int sensor;
	String sensorHex;
	int eventCount;
	String lastEvent;
	
	public NetworkDevice() {}
		
	protected NetworkDevice(int role, int id,int unit, int sensor) {
		this.role = role;
		this.roleLabel = Role.values()[role].getLabel();
		this.id = id;
		this.unit = unit;
		this.sensor = sensor;
	}

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public String getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(String lastEvent) {
		this.lastEvent = lastEvent;
	}

	public String getRoleLabel() {
		return this.roleLabel;
	}
	
	public void setRoleLabel(String roleLabel) {
		this.roleLabel = roleLabel;
	}

	public String getSensorHex() {
		return sensorHex;
	}

	public void setSensorHex(String sensorHex) {
		this.sensorHex = sensorHex;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
		this.roleLabel = Role.values()[role].getLabel();
	}

	public int getSensor() {
		return sensor;
	}

	public void setSensor(int sensor) {
		this.sensor = sensor;
	}
}
