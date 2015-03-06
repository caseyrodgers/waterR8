package com.waterR8.model;

public class GatewayCommand {

	public enum Command{NO_COMMAND, INITIATE_BEACON};
	
	private Command command;
	private Gateway gateway;
	public GatewayCommand() {}
	
	public GatewayCommand(Gateway gateway, int command) {
		this.gateway = gateway;
		this.command = Command.values()[command];
	}
	
	public GatewayCommand(Gateway gateway, Command command) {
		this(gateway, command.ordinal());
	}

	public Command getCommand() {
		return command;
	}

	public void Command(Command command) {
		this.command = command;
	}

	public Gateway getGateway() {
		return gateway;
	}

	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}

	public void setCommand(Command command) {
		this.command = command;
	}
}
