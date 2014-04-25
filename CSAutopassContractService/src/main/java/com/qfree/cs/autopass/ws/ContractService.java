
package com.qfree.cs.autopass.ws;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.cs.autopass.ws.db.Database;
import com.qfree.cs.autopass.ws.domain.ContractCreateResult;
import com.qfree.cs.autopass.ws.domain.ContractCreateTestResult;
import com.qfree.cs.autopass.ws.domain.PaymentMethodGetResult;
import com.qfree.cs.autopass.ws.domain.PaymentMethodUpdateResult;
import com.qfree.cs.autopass.ws.domain.ServiceTestResult;
import com.qfree.cs.autopass.ws.util.WsUtils;

/*
 * serviceName:		Specifies the name of the published service. This property is 
 * 					mapped to the name attribute of the wsdl:service element that 
 * 					defines the published service. The default is to use the name
 * 					of the service's implementation class. Not allowed on the SEI.
 *
 * portName:		Specifies the name of the endpoint at which the service is 
 * 					published. This property is mapped to the name attribute of 
 * 					the wsdl:port element that specifies the endpoint details for 
 * 					a published service. Not allowed on the SEI
 */
@WebService(
		serviceName = "ContractService-WebService_serviceName",
		portName = "ContractServicePort-WebService_portName",
		endpointInterface = "com.qfree.cs.autopass.ws.ContractServiceSEI")
public class ContractService implements ContractServiceSEI {
   
	private static final Logger logger = LoggerFactory.getLogger(ContractService.class);

	@Override
	public ContractCreateTestResult contractCreateTest(
			String username,
			String password,
			String obuID,
			String licencePlate,
			int licencePlateCountryID) {

		logger.info("Input parameters:\n" +
				" Username = {}\n" +
				" Password = {}\n" +
				" OBUID = {}\n" +
				" LicencePlate = {}\n" +
				" LicencePlateCountryID = {}",
				new Object[] { username, password, obuID, licencePlate, new Integer(licencePlateCountryID) });

		Database db = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		ContractCreateTestResult response = new ContractCreateTestResult();

		try {

			db.registerDriver();
			dbConnection = db.getConnection(connectionString);
			logger.info("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			Map result;
			result = db.contractCreateTest(
					dbConnection,
					username,
					password,
					obuID,
					licencePlate,
					licencePlateCountryID);

			if (result.get("ErrorCode").toString().equals("0")) {
				response.setErrorCode(0);
				if (!WsUtils.sybaseStringIsEmpty(result.get("ErrorMessage"))) {
					response.setErrorMessage(result.get("ErrorMessage").toString());
				}
			}
			else {
				response.setErrorCode(Integer.parseInt(result.get("ErrorCode").toString()));
				response.setErrorMessage(result.get("ErrorMessage").toString());
			}

			logger.info("response = {}", response.toString());

		} catch (Exception e) {
			logger.error("An exception was thrown:", e);
		}

		finally {
			try {
				db.deregisterDriver();
			} catch (Exception e) {
				/* ignored */
			}
			try {
				dbConnection.close();
			} catch (Exception e) {
				/* ignored */
			}
		}

		return response;
	}

	@Override
	public ContractCreateResult contractCreate(
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
			int licencePlateCountryID) {

		logger.info("Input parameters:\n" +
				" Username = {}\n" +
				" Password = {}\n" +
				" ClientTypeID = {}\n" +
				" FirstName = {}\n" +
				" LastName = {}\n" +
				" BirthDate = {}\n" +
				" Company = {}\n" +
				" CompanyNumber = {}\n" +
				" Address1 = {}\n" +
				" Address2 = {}\n" +
				" PostCode = {}\n" +
				" PostOffice = {}\n" +
				" CountryID = {}\n" +
				" EMail = {}\n" +
				" Phone = {}\n" +
				" ValidFrom = {}\n" +
				" OBUID = {}\n" +
				" VehicleClassID = {}\n" +
				" LicencePlate = {}\n" +
				" LicencePlateCountryID = {}",
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

		Database db = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		ContractCreateResult response = new ContractCreateResult();

		try {

			db.registerDriver();
			dbConnection = db.getConnection(connectionString);
			logger.info("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			Map result;
			result = db.contractCreate(
					dbConnection,
					username,
					password,
					clientTypeID,
					firstName,
					lastName,
					birthDate,
					company,
					companyNumber,
					address1,
					address2,
					postCode,
					postOffice,
					countryID,
					eMail,
					phone,
					validFrom,
					obuID,
					vehicleClassID,
					licencePlate,
					licencePlateCountryID);

			if (result.get("ErrorCode").toString().equals("0")) {
				response.setClientNumber(result.get("ClientNumber").toString());
				response.setErrorCode(0);
				if (!WsUtils.sybaseStringIsEmpty(result.get("ErrorMessage"))) {
					response.setErrorMessage(result.get("ErrorMessage").toString());
				}
			}
			else {
				response.setErrorCode(Integer.parseInt(result.get("ErrorCode").toString()));
				response.setErrorMessage(result.get("ErrorMessage").toString());
			}

			logger.info("response = {}", response.toString());

		} catch (Exception e) {
			logger.error("An exception was thrown:", e);
		}

		finally {
			try {
				db.deregisterDriver();
			} catch (Exception e) {
				/* ignored */
			}
			try {
				dbConnection.close();
			} catch (Exception e) {
				/* ignored */
			}
		}

		return response;
	}

	@Override
	public ServiceTestResult serviceTest(String username, String password) {

		logger.info("Input parameters:\n" +
				" Username = {}\n" +
				" Password = {}",
				new Object[] { username, password });

		Database db = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		ServiceTestResult response = new ServiceTestResult();

		try {

			db.registerDriver();
			dbConnection = db.getConnection(connectionString);
			logger.info("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			Map result;
			result = db.ServiceTest(dbConnection, username, password);

			if (result.get("ErrorCode").toString().equals("0")) {
				response.setErrorCode(0);
				if (!WsUtils.sybaseStringIsEmpty(result.get("ErrorMessage"))) {
					response.setErrorMessage(result.get("ErrorMessage").toString());
				}
			}
			else {
				response.setErrorCode(Integer.parseInt(result.get("ErrorCode").toString()));
				response.setErrorMessage(result.get("ErrorMessage").toString());
			}

			logger.info("response = {}", response.toString());

		} catch (Exception e) {
			logger.error("An exception was thrown:", e);
		}

		finally {
			try {
				db.deregisterDriver();
			} catch (Exception e) {
				/* ignored */
			}
			try {
				dbConnection.close();
			} catch (Exception e) {
				/* ignored */
			}
		}

		return response;
	}

	@Override
	public PaymentMethodGetResult paymentMethodGet(
			String username,			// stralfors
			String password,			// kF8szBp1lV7Q4SZg
			int systemActorID,			// 23
			int clientNumber,			// 79000001
			int accountNumber,			// 1
			String invoiceNumber) {

		logger.info("Input parameters:");
		logger.info("ClientNumber[{}]", clientNumber);
		logger.info("Avtalenummer[{}]", accountNumber);
		logger.info("SystemActorID[{}]", systemActorID);

		Database db = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		PaymentMethodGetResult response = new PaymentMethodGetResult();

		try {

			db.registerDriver();
			dbConnection = db.getConnection(connectionString);
			logger.info("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");
		 
			Map result;
			result = db.paymentMethodGet(dbConnection, clientNumber, accountNumber, invoiceNumber, systemActorID,
					username, password);

			if (result.get("ErrorCode").toString().equals("0")) {
				response.setPaymentMethodID(Integer.parseInt(result.get("PaymentMethodID").toString()));
				response.setPaymentMethodName(result.get("PaymentMethod").toString());
				response.setErrorCode(0);
			}
			else {
				response.setErrorCode(Integer.parseInt(result.get("ErrorCode").toString()));
				response.setErrorMessage(result.get("ErrorMessage").toString());
			}

			logger.info("response = {}", response.toString());
		}
		catch(Exception e) {
			logger.error("An exception was thrown:", e);
		}
		
		finally {
			try {
				db.deregisterDriver();
			} catch (Exception e) { /* ignored */
			}
			try {
				dbConnection.close();
			} catch (Exception e) { /* ignored */
			}
		}
		
		return response;
	}

	@Override
	public PaymentMethodUpdateResult paymentMethodUpdate(
			String username,
			String password,
			int systemActorID,
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int paymentMethodID) {

		logger.info("Input parameters:");
		logger.info("ClientNumber[{}]", clientNumber);
		logger.info("Avtalenummer[{}]", accountNumber);
		logger.info("SystemActorID[{}]", systemActorID);
		logger.info("PaymentMethodID[{}]", paymentMethodID);
		
		Database db = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		PaymentMethodUpdateResult response = new PaymentMethodUpdateResult();
		
		try {

			db.registerDriver();
			dbConnection = db.getConnection(connectionString);
			logger.info("Changing to ServerCommon");
			dbConnection.setCatalog("ServerCommon");
		 
			Map result;
			result = db.paymentMethodUpdate(dbConnection, clientNumber, accountNumber, invoiceNumber, paymentMethodID,
					systemActorID, username, password);

			if (result.get("ErrorCode").toString().equals("0")) {
				response.setErrorCode(0);
			}
			else {
				response.setErrorCode(Integer.parseInt(result.get("ErrorCode").toString()));
				response.setErrorMessage(result.get("ErrorMessage").toString());
			}
			logger.info("response = {}", response.toString());
		}
		catch(Exception e) {
			logger.error("An exception was thrown:", e);
		}
		
		finally {
			try {
				db.deregisterDriver();
			} catch (Exception e) { /* ignored */
			}
			try {
				dbConnection.close();
			} catch (Exception e) { /* ignored */
			}
		}
		
		return response;
	}

	private String getConnectionString() {

		Properties configProps = new Properties(); 
		
		String server = null;
		String port = null;
		String dbPassword = null;
		String dbUsername = null;
		
		try (InputStream in = this.getClass().getResourceAsStream("/PaymentMethod.properties")) {
			configProps.load(in);
			server = configProps.getProperty("db.server");
			port = configProps.getProperty("db.port");
			dbPassword = configProps.getProperty("db.password");
			dbUsername = configProps.getProperty("db.username");
		} catch (IOException e) {
			logger.error("An exception was thrown loading PaymentMethod.properties:", e);
		}
		
		//		server = "csnt02.csautopass.no";
		//		port = "5000";
		//		passwordDB = "qfreet02";
		//		usernameDB = "adam";
		
		logger.info("server = {}", server);
		logger.info("port = {}", port);
		logger.info("dbPassword = {}", dbPassword);
		logger.info("dbUsername = {}", dbUsername);

		String connectionString = "jdbc:sybase:Tds:" + server + ":" + port + "?USER=" + dbUsername + "&PASSWORD=" + dbPassword;
		
		logger.info("connectionString = {}", connectionString);
		
		return connectionString;

	}
}
