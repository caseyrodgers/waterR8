package com.waterR8.model;

import java.util.Date;
import java.util.logging.Logger;

public class GatewayCommandResponse  {

	static Logger _logger = Logger.getLogger(GatewayCommandResponse.class.getName());

	GatewayCommand command;
	private Date requestTime;
	int requestId;
	
	public GatewayCommandResponse(GatewayCommand command, int requestId, Date requestTime) {
		this.command = command;
		this.requestId = requestId;
		this.requestTime = requestTime;
	}
	
	public void commandCompleted() {
		_logger.info("Command has completed: " + command);
	}
	
	public void commandHadErrors(String error) {
		_logger.warning("Command had errors: " + command + ", " + error);
	}

	
	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public GatewayCommand getCommand() {
		return command;
	}

	public void setCommand(GatewayCommand command) {
		this.command = command;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

}
