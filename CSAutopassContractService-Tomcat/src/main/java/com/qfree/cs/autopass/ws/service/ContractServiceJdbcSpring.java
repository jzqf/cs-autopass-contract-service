
package com.qfree.cs.autopass.ws.service;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.qfree.cs.autopass.ws.util.WsUtils;

public class ContractServiceJdbcSpring implements ContractService {

	private static final Logger logger = LoggerFactory.getLogger(ContractServiceJdbcSpring.class);

	private static final int VALIDATION_ERRORCODE = 100;
	private static final String VALIDATION_ERRORMESSAGE = "Input parameter valideringsfeil";

	//	@Inject
	//	private JdbcTemplate jdbcTemplate;

	private SimpleJdbcCall procContractCreateTest;
	private SimpleJdbcCall procContractCreate;
	private SimpleJdbcCall procServiceTest;
	private SimpleJdbcCall procPaymentMethodGet;
	private SimpleJdbcCall procPaymentMethodUpdate;

	/**
	 * This constructor is used in ContractWS if we are not running in a Spring
	 * container and we need to create a ContractServiceJdbcRaw object 
	 * explicitly there.
	 */
	public ContractServiceJdbcSpring() {
		super();
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
	 * @see com.qfree.cs.autopass.ws.service.ContractService#contractCreateTest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public Map<String, Object> contractCreateTest(
			String username,
			String password,
			String obuID,
			String licencePlate,
			int licencePlateCountryID) throws SQLException {

		Map<String, Object> result = new HashMap<>();

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
		Map<String, Object> out = procContractCreateTest.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));

		return result;
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#contractCreate(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, int)
	 */
	@Override
	public Map<String, Object> contractCreate(
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

		Map<String, Object> result = new HashMap<>();

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
		Map<String, Object> out = procContractCreate.execute(in);	// run stored procedure
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
	public Map<String, Object> ServiceTest(String username, String password) throws SQLException {

		Map<String, Object> result = new HashMap<>();

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
		Map<String, Object> out = procServiceTest.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));

		return result;
	}

	/* (non-Javadoc)
	 * @see com.qfree.cs.autopass.ws.service.ContractService#paymentMethodGet(int, int, java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> paymentMethodGet(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int systemActorID,
			String username,
			String password) throws SQLException {

		Map<String, Object> result = new HashMap<>();

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
		Map<String, Object> out = procPaymentMethodGet.execute(in);	// run stored procedure
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
	public Map<String, Object> paymentMethodUpdate(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int paymentMethodID,
			int systemActorID,
			String username,
			String password) throws SQLException {

		Map<String, Object> result = new HashMap<>();

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
		Map<String, Object> out = procPaymentMethodUpdate.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", out.get("op_ErrorMessage"));

		return result;
	}

}
