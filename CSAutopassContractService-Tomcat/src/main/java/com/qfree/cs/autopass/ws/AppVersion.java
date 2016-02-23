package com.qfree.cs.autopass.ws;

public final class AppVersion {

	/*
	 * 1.0.0:  Original release
	 * 1.0.1:  Removed question mark characters from stored procedure arguments.
	 * 1.0.2:  "ContactCreateTest" now handles non-ISO-8859-1 characters.
	 */
	private static final String APP_VERSION = "1.0.2";

	public String getAppVersion() {
		return APP_VERSION;
	}

}
