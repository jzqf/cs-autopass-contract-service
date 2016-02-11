
package com.qfree.cs.autopass.ws.service;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
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

	private static final CharsetEncoder iso8859Encoder = Charset.forName("ISO-8859-1").newEncoder()
			.onUnmappableCharacter(CodingErrorAction.IGNORE);
	//.onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("X".getBytes());

	private static final int CLIENTTYPE_ID_PERSONAL = 2;
	private static final int CLIENTTYPE_ID_COMPANY = 4;

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
			Integer licencePlateCountryID) throws SQLException {

		Map<String, Object> result = new HashMap<>();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		// Validation:
		//
		// We must explicitly check for empty strings because "required = true"
		// in JAB @XmlElement annotations does not enforce that strings are not
		// empty.

		if (username == null || username.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Username er påkrevd");
			return result;
		}

		if (password == null || password.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Password er påkrevd");
			return result;
		}

		if (obuID == null || obuID.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  OBUID er påkrevd");
			return result;
		}

		if (licencePlate != null && !licencePlate.isEmpty()) {
			if (licencePlateCountryID == null || licencePlateCountryID.intValue() == 0) {
				result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
				result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE
						+ ":  LicencePlateCountryID er påkrevd hvis LicencePlate angitt");
				return result;
			}
		}

		if (licencePlateCountryID != null && licencePlateCountryID.intValue() != 0) {
			if (licencePlate == null || licencePlate.isEmpty()) {
				result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
				result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE
						+ ":  LicencePlate er påkrevd hvis LicencePlateCountryID angitt");
				return result;
			}
		}

		if (licencePlate != null && licencePlate.isEmpty()) {
			licencePlate = null;	// to avoid error in Sybase stored procedure
		}

		//		if (licencePlateCountryID != null) {
		//			logger.debug("licencePlateCountryID = {}", licencePlateCountryID);
		//		} else {
		//			logger.debug("licencePlateCountryID is null");
		//		}

		MapSqlParameterSource in = new MapSqlParameterSource()
				.addValue("ip_Username", username)
				.addValue("ip_Password", password)
				.addValue("ip_OBUID", obuID)
				.addValue("ip_LicencePlate", licencePlate)
				.addValue("ip_LicencePlateCountryID", licencePlateCountryID);
		//		.addValue("ip_LicencePlateCountryID", (licencePlateCountryID >= 0) ? licencePlateCountryID : null);

		// I add entries for the output parameters in the input parameter
		// map. It works without this, but warnings are logged that these
		// parameters do not appear in the input parameter map.
		in.addValue("op_ErrorCode", null).addValue("op_ErrorMessage", null);

		logger.debug("Calling qp_WSC_ContractCreateTest with input parameters:\n{}", in.getValues());
		Map<String, Object> out = procContractCreateTest.execute(in);	// run stored procedure
		logger.debug("Stored procedure output : {}", out);

		logger.debug("ErrorMessage stripped of stored proc name: {}",
				WsUtils.stripSybaseProcName(out.get("op_ErrorMessage")));

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", WsUtils.stripSybaseProcName(out.get("op_ErrorMessage")));

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

		//	/*
		//	 *  Validation:
		//	 *  
		//	 *  The stored procedure ServerCommon.dbo.qp_WSC_ContractCreate returns
		//	 *  an error code if nearly any of the parameters contain a question 
		//	 *  mark character "?". This is due to the following code block:
		//	 *  
		//	 *      if (   @ip_Address1 like '%?%'
		//	 *          or @ip_Address2 like '%?%'
		//	 *          or @ip_FirstName like '%?%'
		//	 *          or @ip_LastName like '%?%'
		//	 *          or @ip_Company like '%?%'
		//	 *          or @ip_CompanyNumber like '%?%'
		//	 *          or @ip_PostCode like '%?%'
		//	 *          or @ip_PostOffice like '%?%'
		//	 *          or @ip_EMail like '%?%'
		//	 *          or @ip_Phone like '%?%'
		//	 *          or @ip_OBUID like '%?%'
		//	 *          or @ip_LicencePlate like '%?%' )
		//	 *      begin
		//	 *      
		//	 *          select @tmp_RetCode = @tmp_BaseErrorNumber + 900 --Unhandled character in string. Only iso8859-1 characters are allowed. (128)
		//	 *          goto ExitDoor
		//	 *      
		//	 *      end
		//	 */
		//	if (address1 != null && address1.contains("?")) {
		//		logger.warn("Question mark character found in 'address1'. It will be stripped. Original text = {}",
		//				address1);
		//		address1 = address1.replace("?", "");
		//	}
		//	if (address2 != null && address2.contains("?")) {
		//		logger.warn("Question mark character found in 'address2'. It will be stripped. Original text = {}",
		//				address2);
		//		address2 = address2.replace("?", "");
		//	}
		//	if (firstName != null && firstName.contains("?")) {
		//		logger.warn("Question mark character found in 'firstName'. It will be stripped. Original text = {}",
		//				firstName);
		//		firstName = firstName.replace("?", "");
		//	}
		//	if (lastName != null && lastName.contains("?")) {
		//		logger.warn("Question mark character found in 'lastName'. It will be stripped. Original text = {}",
		//				lastName);
		//		lastName = lastName.replace("?", "");
		//	}
		//	if (company != null && company.contains("?")) {
		//		logger.warn("Question mark character found in 'company'. It will be stripped. Original text = {}",
		//				company);
		//		company = company.replace("?", "");
		//	}
		//	if (companyNumber != null && companyNumber.contains("?")) {
		//		logger.warn("Question mark character found in 'companyNumber'. It will be stripped. Original text = {}",
		//				companyNumber);
		//		companyNumber = companyNumber.replace("?", "");
		//	}
		//	if (postCode != null && postCode.contains("?")) {
		//		logger.warn("Question mark character found in 'postCode'. It will be stripped. Original text = {}",
		//				postCode);
		//		postCode = postCode.replace("?", "");
		//	}
		//	if (postOffice != null && postOffice.contains("?")) {
		//		logger.warn("Question mark character found in 'postOffice'. It will be stripped. Original text = {}",
		//				postOffice);
		//		postOffice = postOffice.replace("?", "");
		//	}
		//	if (eMail != null && eMail.contains("?")) {
		//		logger.warn("Question mark character found in 'eMail'. It will be stripped. Original text = {}",
		//				eMail);
		//		eMail = eMail.replace("?", "");
		//	}
		//	if (phone != null && phone.contains("?")) {
		//		logger.warn("Question mark character found in 'phone'. It will be stripped. Original text = {}",
		//				phone);
		//		phone = phone.replace("?", "");
		//	}
		//	if (obuID != null && obuID.contains("?")) {
		//		logger.warn("Question mark character found in 'obuID'. It will be stripped. Original text = {}",
		//				obuID);
		//		obuID = postOffice.replace("?", "");
		//	}
		//	if (licencePlate != null && licencePlate.contains("?")) {
		//		logger.warn("Question mark character found in 'licencePlate'. It will be stripped. Original text = {}",
		//				licencePlate);
		//		licencePlate = licencePlate.replace("?", "");
		//	}

		/*
		 *  More validation:
		 *  
		 *  We must also explicitly check for empty strings because 
		 *  "required = true" in JAB @XmlElement annotations does not enforce
		 *  that strings are not empty.
		 */
		if (username == null || username.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Username er påkrevd");
			return result;
		}

		if (password == null || password.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Password er påkrevd");
			return result;
		}

		if (firstName == null || firstName.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  FirstName er påkrevd");
			return result;
		}

		if (lastName == null || lastName.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  LastName er påkrevd");
			return result;
		}

		Date sqlBirthDate = null;
		if (clientTypeID == CLIENTTYPE_ID_PERSONAL) {
			if (birthDate == null || birthDate.isEmpty()) {
				result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
				result.put("ErrorMessage",
						WsUtils.VALIDATION_ERRORMESSAGE + ":  BirthDate er påkrevd for privatkunder");
				return result;
			}
			try {
				sqlBirthDate = WsUtils.parseStringToSqlDate(birthDate, "yyyyMMdd");
			} catch (ParseException e) {
				result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
				result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  BirthDate = " + birthDate);
				return result;
			}
		}

		if (clientTypeID == CLIENTTYPE_ID_COMPANY) {
			if (company == null || company.isEmpty()) {
				result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
				result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Company er påkrevd for firmakunder");
				return result;
			}
			if (companyNumber == null || companyNumber.isEmpty()) {
				result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
				result.put("ErrorMessage",
						WsUtils.VALIDATION_ERRORMESSAGE + ":  CompanyNumber er påkrevd for firmakunder");
				return result;
			}
			//		} else {
			//			company = null;
			//			companyNumber = null;
		}

		if (address1 == null || address1.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Address1 er påkrevd");
			return result;
		}

		if (address2 != null && address2.isEmpty()) {
			address2 = null;	// qp_WSC_ContractCreate requires null for this case
		}

		if (postCode == null || postCode.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  PostCode er påkrevd");
			return result;
		}

		if (postOffice == null || postOffice.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  PostOffice er påkrevd");
			return result;
		}

		if (eMail != null && eMail.isEmpty()) {
			eMail = null;	// qp_WSC_ContractCreate requires null for this case
		}

		if (phone != null && phone.isEmpty()) {
			phone = null;	// qp_WSC_ContractCreate requires null for this case
		}

		if (validFrom == null || validFrom.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  ValidFrom er påkrevd");
			return result;
		}
		Timestamp sqlValidFrom = null;
		try {
			sqlValidFrom = WsUtils.parseStringToSqlTimestamp(validFrom, "yyyyMMdd");
		} catch (ParseException e) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  ValidFrom = " + validFrom);
			return result;
		}

		if (obuID == null || obuID.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  OBUID er påkrevd");
			return result;
		}

		if (vehicleClassID != 1 && vehicleClassID != 2) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  VehicleClassID = " + vehicleClassID
					+ ". Må være 1 eller 2.");
			return result;
		}

		if (licencePlate == null || licencePlate.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  LicencePlate er påkrevd");
			return result;
		}

		/*
		 * More validation:
		 * 
		 * The Sybase database which this application connects to stores data
		 * using the ISO-8859-1 character set. Java Strings, of course, use a
		 * Unicode encoding (some sort of UTF-16, I believe), so that have not
		 * problem representing any conceivable characters that it receives
		 * via this application's web service endpoints. However, if a string is
		 * passed to the database that contains characters that cannot be 
		 * represented in the character encoding used bythe database, the JDBC
		 * driver throws an exception of the form:
		 * 
		 *   org.springframework.jdbc.UncategorizedSQLException: CallableStatementCallback; 
		 *   uncategorized SQLException for SQL [{call qp_WSC_ContractCreate(?, ?, ?, ?, ?, 
		 *   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}]; SQL state [ZZZZZ]; 
		 *   error code [2402]; Error converting characters into server's character set. 
		 *   Some character(s) could not be converted.
		 *   
		 *     ...
		 * 
		 * To avoid this exception, we have perhaps 3 choices:
		 * 
		 * 1. Detect this situation and then return an error without attempting
		 *    to run the stored procedure.
		 * 
		 * 2. Replace offending characters with another character which *can* be
		 *    represented in the server's character set. We cannot use "?"
		 *    because of how the stored procedure qp_WSC_ContractCreate checks
		 *    for question mark characters in its arguments and then returns an
		 *    error itself if it detects one. Perhaps a good choice would be the
		 *    empty string "", i.e., we just *remove* such characters.
		 * 
		 * 3. We run the stored procedure qp_WSC_ContractCreate in a try block
		 *    and then return an error if an exception is thrown, using the
		 *    exception's message text as the error message.
		 * 
		 * We have chosen to use approach #1:
		 */

		/*
		 * username & password ARE ALSO SUBJECT TO THIS PROBLEM!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */
		if (username != null && !iso8859Encoder.canEncode(username)) {
			logger.warn("username = {}, iso8859Encoder.canEncode(username) = {}", username,
					iso8859Encoder.canEncode(username));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (password != null && !iso8859Encoder.canEncode(password)) {
			logger.warn("password = {}, iso8859Encoder.canEncode(password) = {}", password,
					iso8859Encoder.canEncode(password));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (firstName != null && !iso8859Encoder.canEncode(firstName)) {
			logger.warn("firstName = {}, iso8859Encoder.canEncode(firstName) = {}", firstName,
					iso8859Encoder.canEncode(firstName));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (lastName != null && !iso8859Encoder.canEncode(lastName)) {
			logger.warn("lastName = {}, iso8859Encoder.canEncode(lastName) = {}", lastName,
					iso8859Encoder.canEncode(lastName));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (company != null && !iso8859Encoder.canEncode(company)) {
			logger.warn("company = {}, iso8859Encoder.canEncode(company) = {}", company,
					iso8859Encoder.canEncode(company));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (companyNumber != null && !iso8859Encoder.canEncode(companyNumber)) {
			logger.warn("companyNumber = {}, iso8859Encoder.canEncode(companyNumber) = {}", companyNumber,
					iso8859Encoder.canEncode(companyNumber));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (address1 != null && !iso8859Encoder.canEncode(address1)) {
			logger.warn("address1 = {}, iso8859Encoder.canEncode(address1) = {}", address1,
					iso8859Encoder.canEncode(address1));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (address2 != null && !iso8859Encoder.canEncode(address2)) {
			logger.warn("address2 = {}, iso8859Encoder.canEncode(address2) = {}", address2,
					iso8859Encoder.canEncode(address2));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (postCode != null && !iso8859Encoder.canEncode(postCode)) {
			logger.warn("postCode = {}, iso8859Encoder.canEncode(postCode) = {}", postCode,
					iso8859Encoder.canEncode(postCode));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (postOffice != null && !iso8859Encoder.canEncode(postOffice)) {
			logger.warn("postOffice = {}, iso8859Encoder.canEncode(postOffice) = {}", postOffice,
					iso8859Encoder.canEncode(postOffice));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (eMail != null && !iso8859Encoder.canEncode(eMail)) {
			logger.warn("eMail = {}, iso8859Encoder.canEncode(eMail) = {}", eMail,
					iso8859Encoder.canEncode(eMail));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (phone != null && !iso8859Encoder.canEncode(phone)) {
			logger.warn("phone = {}, iso8859Encoder.canEncode(phone) = {}", phone,
					iso8859Encoder.canEncode(phone));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (obuID != null && !iso8859Encoder.canEncode(obuID)) {
			logger.warn("obuID = {}, iso8859Encoder.canEncode(obuID) = {}", obuID,
					iso8859Encoder.canEncode(obuID));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
			return result;
		}
		if (licencePlate != null && !iso8859Encoder.canEncode(licencePlate)) {
			logger.warn("licencePlate = {}, iso8859Encoder.canEncode(licencePlate) = {}", licencePlate,
					iso8859Encoder.canEncode(licencePlate));
			result.put("ErrorCode", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORCODE);
			result.put("ErrorMessage", WsUtils.CANNOT_ENCODE_CHARACTER_ERRORMESSAGE);
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
		Map<String, Object> out = null;
		//	try {
		out = procContractCreate.execute(in); // run stored procedure
		//	} catch (Exception e) {
		//		logger.warn("firstName = {}, iso8859Encoder.canEncode(firstName) = {}", firstName,
		//				iso8859Encoder.canEncode(firstName));
		//		/*
		//		 * Work through the chain of exceptions to reach the "root" cause.
		//		 */
		//		Throwable cause = e;
		//		Throwable rootCause = cause;
		//		while (cause != null) {
		//			rootCause = cause;
		//			//	logger.debug("cause.getMessage() = {}", cause.getMessage());
		//			//	logger.debug("cause.getClass() = {}", cause.getClass());
		//			cause = cause.getCause();
		//		}
		//		result.put("ErrorCode", DATABASE_ERROR_ERRORCODE);
		//		result.put("ErrorMessage", DATABASE_ERROR_ERRORMESSAGE + rootCause.getMessage());
		//		//result.put("ErrorMessage", DATABASE_ERROR_ERRORMESSAGE + e.getMessage());
		//		return result;
		//	}
		logger.debug("Stored procedure output : {}", out);

		logger.debug("ErrorMessage stripped of stored proc name: {}",
				WsUtils.stripSybaseProcName(out.get("op_ErrorMessage")));

		//		result.put("ClientNumber", Long.toString(out.get("op_ClientNumber")));
		result.put("ClientNumber", out.get("op_ClientNumber"));		// @op_ClientNumber has Sybase type numeric(12)
		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", WsUtils.stripSybaseProcName(out.get("op_ErrorMessage")));
		
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

		// Validation:
		//
		// We must explicitly check for empty strings because "required = true"
		// in JAB @XmlElement annotations does not enforce that strings are not
		// empty.

		if (username == null || username.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Username er påkrevd");
			return result;
		}

		if (password == null || password.isEmpty()) {
			result.put("ErrorCode", WsUtils.VALIDATION_ERRORCODE);
			result.put("ErrorMessage", WsUtils.VALIDATION_ERRORMESSAGE + ":  Password er påkrevd");
			return result;
		}

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

		logger.debug("ErrorMessage stripped of stored proc name: {}",
				WsUtils.stripSybaseProcName(out.get("op_ErrorMessage")));

		result.put("ErrorCode", out.get("op_ErrorCode"));
		result.put("ErrorMessage", WsUtils.stripSybaseProcName(out.get("op_ErrorMessage")));

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
