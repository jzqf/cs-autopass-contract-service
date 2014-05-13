
package com.qfree.cs.autopass.ws.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.qfree.cs.autopass.ws.config.AppConfigParams;
import com.qfree.cs.autopass.ws.util.WsUtils;
import com.sybase.jdbcx.SybDriver;

public class ContractServiceJdbcSpring implements ContractService {

	private static final Logger logger = LoggerFactory.getLogger(ContractServiceJdbcSpring.class);

	private static final int VALIDATION_ERRORCODE = 100;
	private static final String VALIDATION_ERRORMESSAGE = "Input parameter valideringsfeil";

	@Inject
	private JdbcTemplate jdbcTemplate;

	private SimpleJdbcCall procContractCreateTest;
	private SimpleJdbcCall procContractCreate;
	private SimpleJdbcCall procServiceTest;
	private SimpleJdbcCall procPaymentMethodGet;
	private SimpleJdbcCall procPaymentMethodUpdate;

	private AppConfigParams appConfigParams;

	/**
	 * This constructor is used in ContractWS if we are not running in a Spring
	 * container and we need to create a ContractServiceJdbcRaw object 
	 * explicitly there.
	 */
	public ContractServiceJdbcSpring() {
		super();
	}

	/**
	 * This constructor is used to create a ContractServiceJdbcRaw bean in 
	 * RootConfig if we are running in a Spring container.
	 * 
	 * @param appConfigParams
	 */
	public ContractServiceJdbcSpring(AppConfigParams appConfigParams) {
		super();
		this.appConfigParams = appConfigParams;
	}
	
	public ContractServiceJdbcSpring(
			SimpleJdbcCall procContractCreateTest,
			SimpleJdbcCall procContractCreate,
			SimpleJdbcCall procServiceTest,
			SimpleJdbcCall procPaymentMethodGet,
			SimpleJdbcCall procPaymentMethodUpdate) {
		super();
		this.procContractCreateTest = procContractCreateTest;
		this.procContractCreate = procContractCreate;
		this.procServiceTest = procServiceTest;
		this.procPaymentMethodGet = procPaymentMethodGet;
		this.procPaymentMethodUpdate = procPaymentMethodUpdate;
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#getAppConfigParams()
	 */
	private AppConfigParams getAppConfigParams() {
		return this.appConfigParams;	// will be injected by Spring
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#setAppConfigParams(com.qfree.cs.autopass.ws.config.AppConfigParams)
	 */
	private void setAppConfigParams(AppConfigParams appConfigParams) {
		this.appConfigParams = appConfigParams;
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#contractCreateTest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
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

		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue("ip_Username", username)
				.addValue("ip_Password", password)
				.addValue("ip_OBUID", obuID)
				.addValue("ip_LicencePlate", licencePlate)
				.addValue("ip_LicencePlateCountryID", (licencePlateCountryID >= 0) ? licencePlateCountryID : null);

		// I add entries for the output parameters in the input parameter
		// map. It works without this, but warnings are logged that these
		// parameters do not appear in the input parameter map.
		in.addValue("op_ErrorCode", null).addValue("op_ErrorMessage", null);

		logger.debug("Calling qp_WSC_ContractCreateTest with input parameters:\n{}", in.getValues());
		Map out = procContractCreateTest.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));

		return result;
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#contractCreate(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, int)
	 */
	@Override
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

		Date sqlBirthDate;
		try {
			sqlBirthDate = WsUtils.parseStringToSqlDate(birthDate, "yyyyMMdd");
		} catch (ParseException e) {
			result.put("ErrorCode", VALIDATION_ERRORCODE);
			result.put("ErrorMessage", VALIDATION_ERRORMESSAGE + ":  BirthDate = " + birthDate);
			return result;
		}

		Timestamp sqlValidFrom;
		try {
			sqlValidFrom = WsUtils.parseStringToSqlTimestamp(validFrom, "yyyyMMdd");
		} catch (ParseException e) {
			result.put("ErrorCode", VALIDATION_ERRORCODE);
			result.put("ErrorMessage", VALIDATION_ERRORMESSAGE + ":  ValidFrom = " + validFrom);
			return result;
		}

		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue("ip_Username", username)
				.addValue("ip_Password", password)
				.addValue("ip_ClientTypeID", (clientTypeID >= 0) ? clientTypeID : null)
				.addValue("ip_FirstName", firstName)
				.addValue("ip_LastName", lastName)
				.addValue("ip_BirthDate", sqlBirthDate)
				.addValue("ip_Company", company)
				.addValue("ip_CompanyNumber", companyNumber)
				.addValue("ip_Address1", address1)
				.addValue("ip_Address2", address2)
				.addValue("ip_PostCode", postCode)
				.addValue("ip_PostOffice", postOffice)
				.addValue("ip_CountryID", (countryID >= 0) ? countryID : null)
				.addValue("ip_EMail", eMail)
				.addValue("ip_Phone", phone)
				.addValue("ip_ValidFrom", sqlValidFrom)
				.addValue("ip_OBUID", obuID)
				.addValue("ip_VehicleClassID", (vehicleClassID >= 0) ? vehicleClassID : null)
				.addValue("ip_LicencePlate", licencePlate)
				.addValue("ip_LicencePlateCountryID", (licencePlateCountryID >= 0) ? licencePlateCountryID : null);

		// I add entries for the output parameters in the input parameter
		// map. It works without this, but warnings are logged that these
		// parameters do not appear in the input parameter map.
		in.addValue("op_ClientNumber", null).addValue("op_ErrorCode", null).addValue("op_ErrorMessage", null);

		logger.debug("Calling qp_WSC_ContractCreate with input parameters:\n{}", in.getValues());
		Map out = procContractCreate.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		//		result.put("ClientNumber", Long.toString(out.get("op_ClientNumber")));
		result.put("ClientNumber", out.get("op_ClientNumber"));		// @op_ClientNumber has Sybase type numeric(12)
		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#ServiceTest(java.lang.String, java.lang.String)
	 */
	@Override
	public Map ServiceTest(String username, String password) throws SQLException {
		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue("ip_Username", username)
				.addValue("ip_Password", password);

		// I add entries for the output parameters in the input parameter
		// map. It works without this, but warnings are logged that these
		// parameters do not appear in the input parameter map.
		in.addValue("op_ErrorCode", null).addValue("op_ErrorMessage", null);

		logger.debug("Calling qp_WSC_ServiceTest with input parameters:\n{}", in.getValues());
		Map out = procServiceTest.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));

		return result;
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#paymentMethodGet(int, int, java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Map paymentMethodGet(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int systemActorID,
			String username,
			String password) throws SQLException {

		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue("ip_Username", username)
				.addValue("ip_Password", password)
				.addValue("ip_ClientNumber", (clientNumber >= 0) ? clientNumber : null)
				.addValue("ip_AccountNumber", (accountNumber >= 0) ? accountNumber : null)
				.addValue("ip_SystemActorID", (systemActorID >= 0) ? systemActorID : null)
				.addValue("ip_InvoiceNumber", invoiceNumber);

		// I add entries for the output parameters in the input parameter
		// map. It works without this, but warnings are logged that these
		// parameters do not appear in the input parameter map.
		in.addValue("op_PaymentMethodID", null).addValue("op_PaymentMethod", null)
				.addValue("op_ErrorMessage", null).addValue("op_ErrorMessage", null);

		logger.debug("Calling procPaymentMethodGet with input parameters:\n{}", in.getValues());
		Map out = procPaymentMethodGet.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		result.put("PaymentMethodID", out.get("op_PaymentMethodID"));
		result.put("PaymentMethod", out.get("op_PaymentMethod"));
		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));

		return result;

	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#paymentMethodUpdate(int, int, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Map paymentMethodUpdate(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int paymentMethodID,
			int systemActorID,
			String username,
			String password) throws SQLException {

		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue("ip_ClientNumber", (clientNumber >= 0) ? clientNumber : null)
				.addValue("ip_AccountNumber", (accountNumber >= 0) ? accountNumber : null)
				.addValue("ip_SystemActorID", (systemActorID >= 0) ? systemActorID : null)
				.addValue("ip_PaymentMethodID", (paymentMethodID >= 0) ? paymentMethodID : null)
				.addValue("ip_Username", username)
				.addValue("ip_Password", password)
				.addValue("ip_InvoiceNumber", invoiceNumber);

		// I add entries for the output parameters in the input parameter
		// map. It works without this, but warnings are logged that these
		// parameters do not appear in the input parameter map.
		in.addValue("op_ErrorCode", null).addValue("op_ErrorMessage", null);

		logger.debug("Calling procPaymentMethodUpdate with input parameters:\n{}", in.getValues());
		Map out = procPaymentMethodUpdate.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));

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

		logger.debug("connectionString = {}", connectionString);

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
