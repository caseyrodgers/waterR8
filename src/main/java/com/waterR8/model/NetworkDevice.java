package com.waterR8.model;


public class NetworkDevice {
	
	public static enum Role{
		SENSOR("Sensor"), REPEATER("Repeater"), COMPANY("Company"), COMPLEX("Complex"), UNIT("Unit");
	
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
		public static Role lookup(String role) {
			try {
				return valueOf(role.toUpperCase());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return Role.SENSOR;
		}
	}
	
	int id;
	int unit;
	Role role;
	String sensor;
	String sensorHex;
	private Object roleLabel;
	
	public NetworkDevice() {}
		
	protected NetworkDevice(Role role, int id,int unit, String sensor) {
		this.role = role;
		this.roleLabel = role.label;
		this.id = id;
		this.unit = unit;
		this.sensor = sensor;
	}

	public Object getRoleLabel() {
		return roleLabel;
	}

	public void setRoleLabel(Object roleLabel) {
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}
}
