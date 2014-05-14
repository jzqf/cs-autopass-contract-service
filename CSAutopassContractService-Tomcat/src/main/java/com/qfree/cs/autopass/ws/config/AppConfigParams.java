package com.qfree.cs.autopass.ws.config;

public class AppConfigParams {

	private String jdbcDriverClass;
	private String jdbcUrl;
	private String dbUsername;
	private String dbPassword;
	private int concurrentCalls_permits;
	private long concurrentCalls_timeoutsecs;

	public String getJdbcDriverClass() {
		return jdbcDriverClass;
	}

	public void setJdbcDriverClass(String jdbcDriverClass) {
		this.jdbcDriverClass = jdbcDriverClass;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
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
		result.append(" dbUsername: " + dbUsername + NEW_LINE);
		result.append(" dbPassword: " + dbPassword + NEW_LINE);
		result.append(" concurrentCalls_permits: " + concurrentCalls_permits + NEW_LINE);
		result.append(" concurrentCalls_timeoutsecs: " + concurrentCalls_timeoutsecs + NEW_LINE);
		result.append("}");

		return result.toString();
	}

}
