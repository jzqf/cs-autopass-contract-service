
package com.qfree.cs.autopass.ws;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
 * 					defines the published service. It represents the part of the
 * 					URL for accessing the web service that comes after the 
 * 					context root of the application. The default is to use the 
 * 					name of the service's implementation class. Not allowed on 
 * 					the SEI.
 *
 * portName:		Specifies the name of the endpoint at which the service is 
 * 					published. This property is mapped to the name attribute of 
 * 					the wsdl:port element that specifies the endpoint details for 
 * 					a published service. Not allowed on the SEI
 */
@WebService(
		serviceName = "ContractService",
		portName = "ContractServicePort",
		endpointInterface = "com.qfree.cs.autopass.ws.ContractServiceSEI")
public class ContractService implements ContractServiceSEI {
   
	private static final Logger logger = LoggerFactory.getLogger(ContractService.class);

	private static final int DBACCESSPROBLEM_ERRORCODE = 101;
	private static final String DBACCESSPROBLEM_ERRORMESSAGE = "Tjeneste er utilgjengelig. Prøv senere.";

	private static final int MAXCONNECTIONS_ERRORCODE = 102;
	private static final String MAXCONNECTIONS_ERRORMESSAGE = "Maksimalt antall samtidige tilkoblinger overskredet. Prøv senere.";

	// "concurrentCalls_semaphore" is used to limit the number of simultaneous 
	// web service calls to a maximum that is set in the configuration 
	// properties file for this application. 
	//
	// These are declared volatile because I will set them later and I want all
	// threads to see the values. concurrentCalls_semaphore is set to null here
	// as a flag to indicate that the semaphore has not yet been created, I need
	// to load values from this application's configuration properties file 
	// before I can create it.
	private static volatile int concurrentCalls_permits = 0;
	private static volatile long concurrentCalls_timeoutsecs = 0;
	private static volatile Semaphore concurrentCalls_semaphore = null;  // to control number of concurrent database connections

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

		Database db = new Database();	// eventually, inject this via Spring above as a singleton
		ContractCreateTestResult response = new ContractCreateTestResult();

		if (concurrentCalls_semaphore != null) {
			logger.debug("***** Before: concurrentCalls_semaphore.availablePermits() = {}",
					concurrentCalls_semaphore.availablePermits());
		}

		// Check if permit is available. This mechanism limits the number of
		// simultaneous web service calls to a maximum that is set in the 
		// configuration properties file for this application. If "true" is 
		// returned here, then number of permits will be reduced by one and 
		// another database connection will be allowed.
		if (this.attemptAccess()) {

			logger.info("Call allowed.");

			Connection dbConnection = null;

			try {

				String connectionString = db.getConnectionString();
				dbConnection = db.getConnection(connectionString);
				logger.info("Setting catalog to ServerCommon");
				dbConnection.setCatalog("ServerCommon");

				Map result = db.contractCreateTest(
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

				//				logger.info("\n*************************************************************\n"
				//						+ "Thread sleeping for 2 seconds to test semaphore mechanism...");
				//				Thread.sleep(2000);
				//				logger.info("...Thread woken after 2 second sleep\n"
				//						+ "*************************************************************");

			} catch (Exception e) {
				response.setErrorCode(DBACCESSPROBLEM_ERRORCODE);
				response.setErrorMessage(DBACCESSPROBLEM_ERRORMESSAGE);
				logger.error("An exception was thrown accessing the database:", e);
			}

			finally {
				try {
					dbConnection.close();
				} catch (Exception e) {
					/* ignored */
				}
				this.releaseAccess();  // release semaphore permit to increase number of available simultaneous web service calls by one
			}

		} else {
			response.setErrorCode(MAXCONNECTIONS_ERRORCODE);
			response.setErrorMessage(MAXCONNECTIONS_ERRORMESSAGE);
			logger.warn("Call disallowed. Maximum concurrent call limit reached: {}", concurrentCalls_permits);
		}

		if (concurrentCalls_semaphore != null) {
			logger.debug("***** After:  concurrentCalls_semaphore.availablePermits() = {}",
					concurrentCalls_semaphore.availablePermits());
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
		ContractCreateResult response = new ContractCreateResult();

		if (concurrentCalls_semaphore != null) {
			logger.debug("***** Before: concurrentCalls_semaphore.availablePermits() = {}",
					concurrentCalls_semaphore.availablePermits());
		}

		// Check if permit is available. This mechanism limits the number of
		// simultaneous web service calls to a maximum that is set in the 
		// configuration properties file for this application. If "true" is 
		// returned here, then number of permits will be reduced by one and 
		// another database connection will be allowed.
		if (this.attemptAccess()) {

			logger.info("Call allowed.");

			Connection dbConnection = null;

			try {

				String connectionString = db.getConnectionString();
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

				//				logger.info("response = {}", response.toString());
				//
				//				logger.info("\n*************************************************************\n"
				//						+ "Thread sleeping for 2 seconds to test semaphore mechanism...");
				//				Thread.sleep(2000);
				//				logger.info("...Thread woken after 2 second sleep\n"
				//						+ "*************************************************************");

			} catch (Exception e) {
				response.setErrorCode(DBACCESSPROBLEM_ERRORCODE);
				response.setErrorMessage(DBACCESSPROBLEM_ERRORMESSAGE);
				logger.error("An exception was thrown accessing the database:", e);
			}

			finally {
				try {
					dbConnection.close();
				} catch (Exception e) {
					/* ignored */
				}
				this.releaseAccess();  // release semaphore permit to increase number of available simultaneous web service calls by one
			}

		} else {
			response.setErrorCode(MAXCONNECTIONS_ERRORCODE);
			response.setErrorMessage(MAXCONNECTIONS_ERRORMESSAGE);
			logger.warn("Call disallowed. Maximum concurrent call limit reached: {}", concurrentCalls_permits);
		}

		if (concurrentCalls_semaphore != null) {
			logger.debug("***** After:  concurrentCalls_semaphore.availablePermits() = {}",
					concurrentCalls_semaphore.availablePermits());
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
		ServiceTestResult response = new ServiceTestResult();

		if (concurrentCalls_semaphore != null) {
			logger.debug("***** Before: concurrentCalls_semaphore.availablePermits() = {}",
					concurrentCalls_semaphore.availablePermits());
		}

		// Check if permit is available. This mechanism limits the number of
		// simultaneous web service calls to a maximum that is set in the 
		// configuration properties file for this application. If "true" is 
		// returned here, then number of permits will be reduced by one and 
		// another database connection will be allowed.
		if (this.attemptAccess()) {

			logger.info("Call allowed.");

			Connection dbConnection = null;

			try {

				String connectionString = db.getConnectionString();
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

				//				logger.info("\n*************************************************************\n"
				//						+ "Thread sleeping for 2 seconds to test semaphore mechanism...");
				//				Thread.sleep(2000);
				//				logger.info("...Thread woken after 2 second sleep\n"
				//						+ "*************************************************************");

			} catch (Exception e) {
				response.setErrorCode(DBACCESSPROBLEM_ERRORCODE);
				response.setErrorMessage(DBACCESSPROBLEM_ERRORMESSAGE);
				logger.error("An exception was thrown accessing the database:", e);
			}

			finally {
				try {
					dbConnection.close();
				} catch (Exception e) {
					/* ignored */
				}
				this.releaseAccess();  // release semaphore permit to increase number of available simultaneous web service calls by one
			}

		} else {
			response.setErrorCode(MAXCONNECTIONS_ERRORCODE);
			response.setErrorMessage(MAXCONNECTIONS_ERRORMESSAGE);
			logger.warn("Call disallowed. Maximum concurrent call limit reached: {}", concurrentCalls_permits);
		}

		if (concurrentCalls_semaphore != null) {
			logger.debug("***** After:  concurrentCalls_semaphore.availablePermits() = {}",
					concurrentCalls_semaphore.availablePermits());
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
		PaymentMethodGetResult response = new PaymentMethodGetResult();

		Connection dbConnection = null;

		try {

			String connectionString = db.getConnectionString();
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
		PaymentMethodUpdateResult response = new PaymentMethodUpdateResult();

		Connection dbConnection = null;

		try {

			String connectionString = db.getConnectionString();
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
				dbConnection.close();
			} catch (Exception e) { /* ignored */
			}
		}
		
		return response;
	}

	/**
	 * 
	 * @return true if a permit was acquired to allow the current web service call to proceed.
	 */
	private boolean attemptAccess() {

		// If the semaphore has not yet been created, we load the semaphore details 
		// from a properties file and then create the semaphore.
		if (concurrentCalls_semaphore == null) {
			Properties configProps = new Properties();
			concurrentCalls_permits = 2;
			concurrentCalls_timeoutsecs = 0;
			try (InputStream in = this.getClass().getResourceAsStream("/config.properties")) {
				configProps.load(in);
				concurrentCalls_permits = Integer.parseInt(configProps.getProperty("db.concurrent-call.maxcalls"));
				concurrentCalls_timeoutsecs = Long.parseLong(configProps.getProperty("db.concurrent-call.waitsecs"));
			} catch (IOException e) {
				logger.error("An exception was thrown loading config.properties for creating semaphore:", e);
			}
			logger.info(
					"Creating semaphore to control conncurrent calls:\nconcurrentCalls_permits = {}\nconcurrentCalls_timeoutsecs = {}",
					concurrentCalls_permits, concurrentCalls_timeoutsecs);
			concurrentCalls_semaphore = new Semaphore(concurrentCalls_permits, true);  // to control number of concurrent database connections
		}

		boolean acquired = false;
		try {
			acquired = concurrentCalls_semaphore.tryAcquire(concurrentCalls_timeoutsecs, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			/* do nothing */
		}
		if (!acquired) {
			logger.warn("Unable to acquire Semaphore for web service call. Maximum concurrent calls allowed = {}",
					concurrentCalls_permits);
		}
		return acquired;
	}

	/**
	 * 
	 * @return always true in the current version.
	 */
	public boolean releaseAccess() {
		concurrentCalls_semaphore.release();
		return true;
	}
}
