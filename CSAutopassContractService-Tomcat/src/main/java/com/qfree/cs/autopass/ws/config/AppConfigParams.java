package com.qfree.cs.autopass.ws.config;

public class AppConfigParams {

	private String server;
	private String port;
	private String dbUsername;
	private String dbPassword;
	private int concurrentCalls_permits;
	private long concurrentCalls_timeoutsecs;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public int getConcurrentCalls_permits() {
		return concurrentCalls_permits;
	}

	public void setConcurrentCalls_permits(int concurrentCalls_permits) {
		this.concurrentCalls_permits = concurrentCalls_permits;
	}

	public long getConcurrentCalls_timeoutsecs() {
		return concurrentCalls_timeoutsecs;
	}

	public void setConcurrentCalls_timeoutsecs(long concurrentCalls_timeoutsecs) {
		this.concurrentCalls_timeoutsecs = concurrentCalls_timeoutsecs;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(this.getClass().getName() + " object {" + NEW_LINE);
		result.append(" server: " + server + NEW_LINE);
		result.append(" port: " + port + NEW_LINE);
		result.append(" dbUsername: " + dbUsername + NEW_LINE);
		result.append(" dbPassword: " + dbPassword + NEW_LINE);
		result.append(" concurrentCalls_permits: " + concurrentCalls_permits + NEW_LINE);
		result.append(" concurrentCalls_timeoutsecs: " + concurrentCalls_timeoutsecs + NEW_LINE);
		result.append("}");

		return result.toString();
	}

}
