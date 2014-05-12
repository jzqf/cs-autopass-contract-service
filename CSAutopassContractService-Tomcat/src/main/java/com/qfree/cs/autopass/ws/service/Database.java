
package com.qfree.cs.autopass.ws.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.cs.autopass.ws.config.AppConfigParams;
import com.qfree.cs.autopass.ws.util.WsUtils;
import com.sybase.jdbcx.SybDriver;

public class Database {

	private static final Logger logger = LoggerFactory.getLogger(Database.class);

	private static final int VALIDATION_ERRORCODE = 100;
	private static final String VALIDATION_ERRORMESSAGE = "Input parameter valideringsfeil";

	private static volatile AppConfigParams staticAppConfigParams;

	static {
		Properties configProps = new Properties();
		staticAppConfigParams = new AppConfigParams();

		try (InputStream in = Database.class.getResourceAsStream("/config.properties")) {
			configProps.load(in);
			staticAppConfigParams.setServer(configProps.getProperty("db.server"));
			staticAppConfigParams.setPort(configProps.getProperty("db.port"));
			staticAppConfigParams.setDbUsername(configProps.getProperty("db.username"));
			staticAppConfigParams.setDbPassword(configProps.getProperty("db.password"));
			staticAppConfigParams.setConcurrentCalls_permits(Integer.parseInt(configProps
					.getProperty("db.concurrent-call.maxcalls")));
			staticAppConfigParams.setConcurrentCalls_timeoutsecs(Long.parseLong(configProps
					.getProperty("db.concurrent-call.waitsecs")));
		} catch (IOException e) {
			logger.error("An exception was thrown loading config.properties:", e);
		}

		logger.info("Loaded config.properties: {}", staticAppConfigParams);
	}

	// This static initialization block ensures that the JDBC driver is loaded 
	// and registered only once.
	static {
		try {

			logger.info("Loading jConnect JDBC driver so it can be registered.");
			final SybDriver sybDriver = (SybDriver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance();

			logger.info("jConnect version: {}.{}", sybDriver.getMajorVersion(), sybDriver.getMinorVersion());
			sybDriver.setVersion(com.sybase.jdbcx.SybDriver.VERSION_7);	// probably not necessary

			logger.info("Registering jConnect JDBC driver.");
			DriverManager.registerDriver(sybDriver);

			// Test to check how Java exceptions are logged:
			/*try {
				String s = null;
				if (s.equals("anything")) {			
				}
			} 
			catch(Exception e) {      
				logger.error("An exception was thrown on purpose (s is null):", e);
			}*/

		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException e) {
			logger.error("An exception was thrown registering the JDBC driver. Rethrowing...", e);
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	private AppConfigParams appConfigParams;

	public static AppConfigParams getStaticAppConfigParams() {
		return staticAppConfigParams;
	}

	public static void setStaticAppConfigParams(AppConfigParams staticAppConfigParams) {
		Database.staticAppConfigParams = staticAppConfigParams;
	}

	public AppConfigParams getAppConfigParams() {
		if (appConfigParams != null) {
			return appConfigParams;		// will be injected by Spring when we use Spring; otherwise, it will be null 
		} else {
			return staticAppConfigParams;	// defined in a static initialization block above.
		}
	}

	public void setAppConfigParams(AppConfigParams appConfigParams) {
		this.appConfigParams = appConfigParams;
	}

	public Map contractCreateTest(
			String username,
			String password,
			String obuID,
			String licencePlate,
			int licencePlateCountryID) throws SQLException {

		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		//		try (Connection dbConnection = getConnection(getConnectionString())) {
		try (Connection dbConnection = java.sql.DriverManager.getConnection(getConnectionString())) {

			logger.debug("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			// Here, we need one "?" for each input AND output parameter of the stored procedure.
			// Any ResultSet objects opened for the statement will also be closed by this try block.
			try (CallableStatement cs = dbConnection
					.prepareCall("{call qp_WSC_ContractCreateTest(?, ?, ?, ?, ?, ?, ?)}")) {

				logger.info("Attempting to execute qp_WSC_ContractCreateTest: with input parameters:\n" +
						" @ip_Username = {}\n" +
						" @ip_Password = {}\n" +
						" @ip_OBUID = {}\n" +
						" @ip_LicencePlate = {}\n" +
						" @ip_LicencePlateCountryID = {}",
						new Object[] { username, password, obuID, licencePlate, new Integer(licencePlateCountryID) });

				cs.setString("@ip_Username", username);
				cs.setString("@ip_Password", password);
				cs.setString("@ip_OBUID", obuID);
				cs.setString("@ip_LicencePlate", licencePlate);
				if (licencePlateCountryID >= 0) {
					cs.setInt("@ip_LicencePlateCountryID", licencePlateCountryID);
				}
				else {
					cs.setNull("@ip_LicencePlateCountryID", Types.NUMERIC);
				}
				cs.registerOutParameter("@op_ErrorCode", Types.INTEGER);
				cs.registerOutParameter("@op_ErrorMessage", Types.VARCHAR, 255);

				logger.debug("Executing stored procedure qp_WSC_ContractCreateTest on server...");
				cs.execute();
				logger.debug("...Done. No exception thrown.");

				result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
				result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));

			} catch (SQLException e) {
				logger.error(
						"An exception was thrown preparing, executing or processing results from qp_WSC_ContractCreateTest. Rethrowing...",
						e);
				throw e;
			}

		} catch (SQLException e) {
			logger.error(
					"An exception was creating or using the conection for qp_WSC_ContractCreateTest. Rethrowing...", e);
			throw e;
		}

		return result;
	}

	public Map contractCreate(
			String username,
			String password,
			int clientTypeID,
			String firstName,
			String lastName,
			String birthDate,
			String company,
			String companyNumber,
			String address1,
			String address2,
			String postCode,
			String postOffice,
			int countryID,
			String eMail,
			String phone,
			String validFrom,
			String obuID,
			int vehicleClassID,
			String licencePlate,
			int licencePlateCountryID) throws SQLException {

		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		//		try (Connection dbConnection = getConnection(getConnectionString())) {
		try (Connection dbConnection = java.sql.DriverManager.getConnection(getConnectionString())) {

			logger.debug("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			// Here, we need one "?" for each input AND output parameter of the stored procedure.
			// Any ResultSet objects opened for the statement will also be closed by this try block.
			try (CallableStatement cs = dbConnection
					.prepareCall(
					"{call qp_WSC_ContractCreate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

				logger.info("Attempting to execute qp_WSC_ContractCreate: with input parameters:\n" +
						" @ip_Username = {}\n" +
						" @ip_Password = {}\n" +
						" @ip_ClientTypeID = {}\n" +
						" @ip_FirstName = {}\n" +
						" @ip_LastName = {}\n" +
						" @ip_BirthDate = {}\n" +
						" @ip_Company = {}\n" +
						" @ip_CompanyNumber = {}\n" +
						" @ip_Address1 = {}\n" +
						" @ip_Address2 = {}\n" +
						" @ip_PostCode = {}\n" +
						" @ip_PostOffice = {}\n" +
						" @ip_CountryID = {}\n" +
						" @ip_EMail = {}\n" +
						" @ip_Phone = {}\n" +
						" @ip_ValidFrom = {}\n" +
						" @ip_OBUID = {}\n" +
						" @ip_VehicleClassID tinyint = {}\n" +
						" @ip_LicencePlate = {}\n" +
						" @ip_LicencePlateCountryID = {}",
						new Object[] {
								username,
								password,
								new Integer(clientTypeID),
								firstName,
								lastName,
								birthDate,
								company,
								companyNumber,
								address1,
								address2,
								postCode,
								postOffice,
								new Integer(countryID),
								eMail,
								phone,
								validFrom,
								obuID,
								new Integer(vehicleClassID),
								licencePlate,
								new Integer(licencePlateCountryID) });

				cs.setString("@ip_Username", username);
				cs.setString("@ip_Password", password);
				if (clientTypeID >= 0) {
					cs.setInt("@ip_ClientTypeID", clientTypeID);
				}
				else {
					cs.setNull("@ip_ClientTypeID", Types.NUMERIC);
				}
				cs.setString("@ip_FirstName", firstName);
				cs.setString("@ip_LastName", lastName);
				try {
					cs.setDate("@ip_BirthDate", WsUtils.parseStringToSqlDate(birthDate, "yyyyMMdd"));
				} catch (ParseException e) {
					result.put("ErrorCode", VALIDATION_ERRORCODE);
					result.put("ErrorMessage", VALIDATION_ERRORMESSAGE + ":  BirthDate = " + birthDate);
					return result;
				}
				//cs.setString("@ip_BirthDate", birthDate);
				cs.setString("@ip_Company", company);
				cs.setString("@ip_CompanyNumber", companyNumber);
				cs.setString("@ip_Address1", address1);
				cs.setString("@ip_Address2", address2);
				cs.setString("@ip_PostCode", postCode);
				cs.setString("@ip_PostOffice", postOffice);
				if (countryID >= 0) {
					cs.setInt("@ip_CountryID", countryID);
				}
				else {
					cs.setNull("@ip_CountryID", Types.NUMERIC);
				}
				cs.setString("@ip_EMail", eMail);
				cs.setString("@ip_Phone", phone);

				// This supports multiple datetime formats. The does not seem to be any simple
				// way to implement this with a single format.
				try {
					cs.setTimestamp("@ip_ValidFrom", WsUtils.parseStringToSqlTimestamp(validFrom, "yyyyMMdd"));
				} catch (ParseException e) {
					result.put("ErrorCode", VALIDATION_ERRORCODE);
					result.put("ErrorMessage", VALIDATION_ERRORMESSAGE + ":  ValidFrom = " + validFrom);
					return result;
				}
				//cs.setString("@ip_ValidFrom", validFrom);

				cs.setString("@ip_OBUID", obuID);
				if (vehicleClassID >= 0) {
					cs.setInt("@ip_VehicleClassID", vehicleClassID);
				}
				else {
					cs.setNull("@ip_VehicleClassID", Types.NUMERIC);
				}
				cs.setString("@ip_LicencePlate", licencePlate);
				if (licencePlateCountryID >= 0) {
					cs.setInt("@ip_LicencePlateCountryID", licencePlateCountryID);
				}
				else {
					cs.setNull("@ip_LicencePlateCountryID", Types.NUMERIC);
				}

				cs.registerOutParameter("@op_ClientNumber", Types.NUMERIC);		// @op_ClientNumber has Sybase type numeric(12)
				cs.registerOutParameter("@op_ErrorCode", Types.INTEGER);
				cs.registerOutParameter("@op_ErrorMessage", Types.VARCHAR, 255);

				logger.debug("Executing stored procedure qp_WSC_ContractCreate on server...");
				cs.execute();
				logger.debug("...Done. No exception thrown.");

				result.put("ClientNumber", Long.toString(cs.getLong("@op_ClientNumber")));		// @op_ClientNumber has Sybase type numeric(12)
				result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
				result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));

			} catch (SQLException e) {
				logger.error(
						"An exception was thrown preparing, executing or processing results from qp_WSC_ContractCreate. Rethrowing...",
						e);
				throw e;
			}

		} catch (SQLException e) {
			logger.error(
					"An exception was creating or using the conection for qp_WSC_ContractCreate. Rethrowing...", e);
			throw e;
		}

		return result;
	}

	public Map ServiceTest(String username, String password) throws SQLException {
		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		//		try (Connection dbConnection = getConnection(getConnectionString())) {
		try (Connection dbConnection = java.sql.DriverManager.getConnection(getConnectionString())) {

			logger.debug("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			// Here, we need one "?" for each input AND output parameter of the stored procedure.
			// Any ResultSet objects opened for the statement will also be closed by this try block.
			try (CallableStatement cs = dbConnection.prepareCall("{call qp_WSC_ServiceTest(?, ?, ?, ?,)}");) {

				logger.info("Attempting to execute qp_WSC_ServiceTest: with input parameters:\n" +
						" @ip_Username = {}\n" +
						" @ip_Password = {}",
						new Object[] { username, password });

				cs.setString("@ip_Username", username);
				cs.setString("@ip_Password", password);

				cs.registerOutParameter("@op_ErrorCode", Types.INTEGER);
				cs.registerOutParameter("@op_ErrorMessage", Types.VARCHAR, 255);

				logger.debug("Executing stored procedure qp_WSC_ServiceTest on server...");
				cs.execute();
				logger.debug("...Done. No exception thrown.");

				result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
				result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));

			} catch (SQLException e) {
				logger.error(
						"An exception was thrown preparing, executing or processing results from qp_WSC_ServiceTest. Rethrowing",
						e);
				throw e;
			}

		} catch (SQLException e) {
			logger.error(
					"An exception was creating or using the conection for qp_WSC_ServiceTest. Rethrowing...", e);
			throw e;
		}

		return result;
	}

	public Map paymentMethodGet(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int systemActorID,
			String username,
			String password) throws SQLException {

		Map result = new HashMap();

		//		try (Connection dbConnection = getConnection(getConnectionString())) {
		try (Connection dbConnection = java.sql.DriverManager.getConnection(getConnectionString())) {

			logger.debug("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			logger.info("creating stateent to execute qp_WSC_PaymentMethodGet: username[{}] password[{}]", username,
					password);
			try (CallableStatement cs = dbConnection
					.prepareCall("{call qp_WSC_PaymentMethodGet(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

				cs.setString("@ip_Username", username);
				cs.setString("@ip_Password", password);

				if (clientNumber >= 0) {
					cs.setInt("@ip_ClientNumber", clientNumber);
				}
				else {
					cs.setNull("@ip_ClientNumber", Types.NUMERIC);
				}

				if (accountNumber >= 0) {
					cs.setInt("@ip_AccountNumber", accountNumber);
				}
				else {
					cs.setNull("@ip_AccountNumber", Types.NUMERIC);
				}
				cs.setString("@ip_InvoiceNumber", invoiceNumber);

				if (systemActorID >= 0) {
					cs.setInt("@ip_SystemActorID", systemActorID);
				}
				else {
					cs.setNull("@ip_SystemActorID", Types.NUMERIC);
				}

				cs.registerOutParameter("@op_PaymentMethodID", Types.TINYINT);
				cs.registerOutParameter("@op_PaymentMethod", Types.VARCHAR, 50);
				cs.registerOutParameter("@op_ErrorCode", Types.INTEGER);
				cs.registerOutParameter("@op_ErrorMessage", Types.VARCHAR, 255);

				cs.execute();

				result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
				result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));
				result.put("PaymentMethod", cs.getString("@op_PaymentMethod"));
				result.put("PaymentMethodID", cs.getInt("@op_PaymentMethodID"));

			} catch (SQLException e) {
				logger.error(
						"An exception was thrown preparing, executing or processing results from qp_WSC_PaymentMethodGet. Rethrowing",
						e);
				throw e;
            }
        
		} catch (SQLException e) {
			logger.error(
					"An exception was creating or using the conection for qp_WSC_PaymentMethodGet. Rethrowing...", e);
			throw e;
		}

        return result;
	}

	public Map paymentMethodUpdate(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int paymentMethodID,
			int systemActorID,
			String username,
			String password) throws SQLException {

		Map result = new HashMap();

		//		try (Connection dbConnection = getConnection(getConnectionString())) {
		try (Connection dbConnection = java.sql.DriverManager.getConnection(getConnectionString())) {

			logger.debug("Changing to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			logger.info("Creating statement to execute qp_WSC_PaymentMethodUpdate: username[{}] password[{}]",
					username,
					password);
			try (CallableStatement cs = dbConnection
					.prepareCall("{ call qp_WSC_PaymentMethodUpdate }, ?, ?, ?, ?, ?, ?, ?, ?, ?")) {
            
			if (clientNumber >= 0) {
				cs.setInt("@ip_ClientNumber", clientNumber);
            }
            else {
                cs.setNull("@ip_ClientNumber", Types.NUMERIC);
            }
            
			if (accountNumber >= 0) {
				cs.setInt("@ip_AccountNumber", accountNumber);
            }
            else {
                cs.setNull("@ip_AccountNumber", Types.NUMERIC);
            }
            
			if (systemActorID >= 0) {
				cs.setInt("@ip_SystemActorID", systemActorID);
            }
            else {
                cs.setNull("@ip_SystemActorID", Types.NUMERIC);
            }
            
			if (paymentMethodID >= 0) {
				cs.setInt("@ip_PaymentMethodID", paymentMethodID);
			}
			else {
				cs.setNull("@ip_PaymentMethodID", Types.TINYINT);
			}

			cs.setString("@ip_Username", username);
			cs.setString("@ip_Password", password);
			cs.setString("@ip_InvoiceNumber", invoiceNumber);

            cs.registerOutParameter("@op_ErrorCode", Types.INTEGER);
            cs.registerOutParameter("@op_ErrorMessage", Types.VARCHAR, 255);
            
            cs.execute();
            
            result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
			result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));

			} catch (SQLException e) {
				logger.error(
						"An exception was thrown preparing, executing or processing results from qp_WSC_PaymentMethodUpdate. Rethrowing",
						e);
				throw e;
        }
        
		} catch (SQLException e) {
			logger.error(
					"An exception was creating or using the conection for qp_WSC_PaymentMethodUpdate. Rethrowing...", e);
			throw e;
		}

        return result;
	}

	private String getConnectionString() {

		logger.debug("Executing getAppConfigParams() to get the application configuration parameters.");
		AppConfigParams appConfigParams = getAppConfigParams();

		logger.debug("appConfigParams: {}", appConfigParams);

		//		Properties configProps = new Properties();
		//
		//		String server = null;
		//		String port = null;
		//		String dbUsername = null;
		//		String dbPassword = null;
		//
		//		try (InputStream in = this.getClass().getResourceAsStream("/config.properties")) {
		//			configProps.load(in);
		//			server = configProps.getProperty("db.server");
		//			port = configProps.getProperty("db.port");
		//			dbUsername = configProps.getProperty("db.username");
		//			dbPassword = configProps.getProperty("db.password");
		//		} catch (IOException e) {
		//			logger.error("An exception was thrown loading config.properties:", e);
		//		}
		//
		//		//		server = "csnt02.csautopass.no";
		//		//		port = "5000";
		//		//		usernameDB = "adam";
		//		//		passwordDB = "qfreet02";
		//
		//		logger.info("Loaded config.properties:\n server = {}\n port = {}\n dbUsername = {}\n dbPassword = {}",
		//				new Object[] { server, port, dbUsername, dbPassword });
		//
		//		String connectionString = "jdbc:sybase:Tds:" + server + ":" + port + "?USER=" + dbUsername + "&PASSWORD="
		//				+ dbPassword;

		String connectionString =
				"jdbc:sybase:Tds:" + appConfigParams.getServer() + ":" + appConfigParams.getPort()
						+ "?USER=" + appConfigParams.getDbUsername()
						+ "&PASSWORD=" + appConfigParams.getDbPassword();

		logger.info("connectionString = {}", connectionString);

		return connectionString;

	}

	/**
	 * This method is not currently used because I have inlined this code, but I
	 * will keep this method for now in case it is useful in the future.
	 * 
	 * @param connectionString
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection(String connectionString) throws SQLException {

		Connection dbConnection = null;

		try {
			logger.info("Establishing a database connection for connection string: {}.", connectionString);
			// Instead of including the user name and password in the connection
			// string, as we do here, it is also possible to pass a second
			// parameter that is a Properties object that contains those 
			// details, and possibly additional details such as "proxy".  See
			// pg 11 of the jConnect 7.0 Programmers Reference PDF file for more
			//details.
			dbConnection = java.sql.DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			logger.error("An exception was thrown getting a connection. Rethrowing...", e);
			throw e;
		}

		return dbConnection;
	}

	/**
	 * Not used - this is from Roy's old code.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void registerDriver() throws IllegalAccessException, InstantiationException, ClassNotFoundException,
			SQLException {
		try {

			logger.info("Loading jConnect JDBC driver so it can be registered.");

			SybDriver sybDriver = (SybDriver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance();
			sybDriver.setVersion(com.sybase.jdbcx.SybDriver.VERSION_7);

			logger.info("jConnect version: {}.{}", sybDriver.getMajorVersion(), sybDriver.getMinorVersion());
			logger.info("Registering jConnect JDBC driver.");

			DriverManager.registerDriver(sybDriver);

		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException e) {
			logger.error("An exception was thrown registering the JDBC driver. Rethrowing...", e);
			throw e;
		}
	}

	/**
	 * Not used - this is from Roy's old code.
	 */
	private void deregisterDriver() {
		if (true) {
			return;
		}
		try {

			logger.info("Loading jConnect JDBC driver so it can be deregistered.");

			SybDriver sybDriver = (SybDriver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance();
			sybDriver.setVersion(com.sybase.jdbcx.SybDriver.VERSION_7);

			logger.info("Deregistering jConnect JDBC driver.");
			DriverManager.deregisterDriver(sybDriver);

		} catch (Exception e) {
			logger.error("An exception was thrown deregistering the JDBC driver:", e);
		}
	}

}
