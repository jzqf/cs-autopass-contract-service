
package com.qfree.cs.autopass.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.cs.autopass.ws.domain.ContractCreateResult;
import com.qfree.cs.autopass.ws.domain.ContractCreateTestResult;
import com.qfree.cs.autopass.ws.domain.PaymentMethodGetResult;
import com.qfree.cs.autopass.ws.domain.PaymentMethodUpdateResult;
import com.qfree.cs.autopass.ws.domain.ServiceTestResult;
import com.qfree.cs.autopass.ws.service.ContractService;
import com.qfree.cs.autopass.ws.util.WsUtils;
import com.sun.xml.ws.developer.SchemaValidation;

/*
 * serviceName:		Specifies the name of the published service. This property is 
 * 					mapped to the name attribute of the wsdl:service element that 
 * 					defines the published service. It represents the part of the
 * 					URL for accessing the web service that comes after the 
 * 					context root of the application. The default is to use the 
 * 					name of the service's implementation class. Not allowed on 
 * 					the SEI - it must be used on the concrete web service class.
 *
 * portName:		Specifies the name of the endpoint at which the service is 
 * 					published. This property is mapped to the name attribute of 
 * 					the wsdl:port element that specifies the endpoint details for 
 * 					a published service. Not allowed on the SEI - it must be 
 * 					used on the concrete web service class.
 */
@WebService(
		serviceName = "ContractService",
		portName = "ContractServicePort",
		endpointInterface = "com.qfree.cs.autopass.ws.ContractWsSEI")
/* 
 * Without the @SchemaValidation annotiation, the JAX-WS Metro RI generates an 
 * XML schema for input and output parameters, but it is not used.  Therefore,
 * this annotation is required if you want, for example, the "required" 
 * attribute ij @XmlElement(required = true) to have its intended effect.
 */
@SchemaValidation
public class ContractWs implements ContractWsSEI {
   
	private static final Logger logger = LoggerFactory.getLogger(ContractWs.class);

	// "concurrentCalls_semaphore" is used to limit the number of simultaneous 
	// web service calls to a maximum that is set in the configuration 
	// properties file for this application. 
	//
	// These are declared volatile because I will set them later and I want all
	// threads to see the values.
	private static volatile int concurrentCalls_permits;
	private static volatile long concurrentCalls_timeoutsecs;
	private static volatile Semaphore concurrentCalls_semaphore;  // to control number of concurrent database connections

	// Create the counting semaphore that will limit the number of simultaneous
	// database connections. It is created only once in this static 
	// initialization block.
	static {
		Properties configProps = new Properties();
		// In case the try block does not successfully set these static members:
		ContractWs.concurrentCalls_permits = 5;
		ContractWs.concurrentCalls_timeoutsecs = 10;
		try (InputStream in = ContractWs.class.getResourceAsStream("/config.properties")) {
			configProps.load(in);
			ContractWs.concurrentCalls_permits = Integer.parseInt(configProps
					.getProperty("db.concurrent-call.maxcalls").trim());
			ContractWs.concurrentCalls_timeoutsecs = Long.parseLong(configProps
					.getProperty("db.concurrent-call.waitsecs").trim());
		} catch (IOException e) {
			logger.error("An exception was thrown loading config.properties for creating semaphore:", e);
		} catch (Exception e) {
			logger.error("An exception was thrown loading config.properties for creating semaphore:", e);
		}
		logger.info(
				"Creating semaphore to control conncurrent calls:\nconcurrentCalls_permits = {}\nconcurrentCalls_timeoutsecs = {}",
				ContractWs.concurrentCalls_permits, ContractWs.concurrentCalls_timeoutsecs);
		ContractWs.concurrentCalls_semaphore = new Semaphore(ContractWs.concurrentCalls_permits, true);  // to control number of concurrent database connections
	}

	// Provides database-related services. Injected by Spring via its setter.
	private ContractService contractService;

	@Override
	public ContractService getContractService() {
		return this.contractService;
	}

	@Override
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;		// injected by Spring as a singleton
	}

	@Override
	public ContractCreateTestResult contractCreateTest(
			String username,
			String password,
			String obuID,
			String licencePlate,
			Integer licencePlateCountryID) {

		logger.info("Input parameters:\n" +
				" Username = {}\n" +
				" Password = {}\n" +
				" OBUID = {}\n" +
				" LicencePlate = {}\n" +
				" LicencePlateCountryID = {}",
				new Object[] { username, password, obuID, licencePlate, licencePlateCountryID });
		//		new Object[] { username, password, obuID, licencePlate, new Integer(licencePlateCountryID) });

		//		ContractService contractService = getContractService();	// pre-Spring code - keep for now

		ContractCreateTestResult response = new ContractCreateTestResult();

		logger.info("***** Before: concurrentCalls_semaphore.availablePermits() = {}",
				concurrentCalls_semaphore.availablePermits());

		// Check if permit is available. This mechanism limits the number of
		// simultaneous web service calls to a maximum that is set in the 
		// configuration properties file for this application. If "true" is 
		// returned here, then number of permits will be reduced by one and 
		// another database connection will be allowed.
		if (this.attemptAccess()) {

			logger.info("Call allowed.");

			try {

				Map<String, Object> result = contractService.contractCreateTest(
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
				response.setErrorCode(WsUtils.DBACCESSPROBLEM_ERRORCODE);
				response.setErrorMessage(WsUtils.DBACCESSPROBLEM_ERRORMESSAGE);
				logger.error("An exception was thrown accessing the database:", e);
			} finally {
				this.releaseAccess();  // release semaphore permit to increase number of available simultaneous web service calls by one
			}

		} else {
			response.setErrorCode(WsUtils.MAXCONNECTIONS_ERRORCODE);
			response.setErrorMessage(WsUtils.MAXCONNECTIONS_ERRORMESSAGE);
			logger.warn("Call disallowed. Maximum concurrent call limit reached: {}", concurrentCalls_permits);
		}

		logger.info("***** After:  concurrentCalls_semaphore.availablePermits() = {}",
				concurrentCalls_semaphore.availablePermits());

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

		//		ContractService contractService = getContractService();	// pre-Spring code - keep for now

		ContractCreateResult response = new ContractCreateResult();

		logger.info("***** Before: concurrentCalls_semaphore.availablePermits() = {}",
				concurrentCalls_semaphore.availablePermits());

		// Check if permit is available. This mechanism limits the number of
		// simultaneous web service calls to a maximum that is set in the 
		// configuration properties file for this application. If "true" is 
		// returned here, then number of permits will be reduced by one and 
		// another database connection will be allowed.
		if (this.attemptAccess()) {

			logger.info("Call allowed.");

			try {

				Map<String, Object> result = contractService.contractCreate(
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

				//
				//				logger.info("\n*************************************************************\n"
				//						+ "Thread sleeping for 2 seconds to test semaphore mechanism...");
				//				Thread.sleep(2000);
				//				logger.info("...Thread woken after 2 second sleep\n"
				//						+ "*************************************************************");

			} catch (Exception e) {
				response.setErrorCode(WsUtils.DBACCESSPROBLEM_ERRORCODE);
				response.setErrorMessage(WsUtils.DBACCESSPROBLEM_ERRORMESSAGE);
				logger.error("An exception was thrown accessing the database:", e);
			} finally {
				this.releaseAccess();  // release semaphore permit to increase number of available simultaneous web service calls by one
			}

		} else {
			response.setErrorCode(WsUtils.MAXCONNECTIONS_ERRORCODE);
			response.setErrorMessage(WsUtils.MAXCONNECTIONS_ERRORMESSAGE);
			logger.warn("Call disallowed. Maximum concurrent call limit reached: {}", concurrentCalls_permits);
		}

		logger.info("***** After:  concurrentCalls_semaphore.availablePermits() = {}",
				concurrentCalls_semaphore.availablePermits());

		return response;
	}

	@Override
	public ServiceTestResult serviceTest(String username, String password) {

		logger.info("Input parameters:\n" +
				" Username = {}\n" +
				" Password = {}",
				new Object[] { username, password });

		//		ContractService contractService = getContractService();	// pre-Spring code - keep for now

		ServiceTestResult response = new ServiceTestResult();

		logger.info("***** Before: concurrentCalls_semaphore.availablePermits() = {}",
				concurrentCalls_semaphore.availablePermits());

		// Check if permit is available. This mechanism limits the number of
		// simultaneous web service calls to a maximum that is set in the 
		// configuration properties file for this application. If "true" is 
		// returned here, then number of permits will be reduced by one and 
		// another database connection will be allowed.
		if (this.attemptAccess()) {

			logger.info("Call allowed.");

			try {

				Map<String, Object> result = contractService.ServiceTest(username, password);

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
				response.setErrorCode(WsUtils.DBACCESSPROBLEM_ERRORCODE);
				response.setErrorMessage(WsUtils.DBACCESSPROBLEM_ERRORMESSAGE);
				logger.error("An exception was thrown accessing the database:", e);
			} finally {
				this.releaseAccess();  // release semaphore permit to increase number of available simultaneous web service calls by one
			}

		} else {
			response.setErrorCode(WsUtils.MAXCONNECTIONS_ERRORCODE);
			response.setErrorMessage(WsUtils.MAXCONNECTIONS_ERRORMESSAGE);
			logger.warn("Call disallowed. Maximum concurrent call limit reached: {}", concurrentCalls_permits);
		}

		logger.info("***** After:  concurrentCalls_semaphore.availablePermits() = {}",
				concurrentCalls_semaphore.availablePermits());

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

		//		ContractService contractService = getContractService();	// pre-Spring code - keep for now

		PaymentMethodGetResult response = new PaymentMethodGetResult();

		try {
		 
			Map<String, Object> result = contractService.paymentMethodGet(
					clientNumber, accountNumber,
					invoiceNumber, systemActorID,
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
			logger.error("An exception was thrown accessing the database:", e);
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
		
		//		ContractService contractService = getContractService();	// pre-Spring code - keep for now

		PaymentMethodUpdateResult response = new PaymentMethodUpdateResult();

		try {
		 
			Map<String, Object> result = contractService.paymentMethodUpdate(
					clientNumber, accountNumber,
					invoiceNumber, paymentMethodID,
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
			logger.error("An exception was thrown accessing the database:", e);
		}

		return response;
	}

	/**
	 * 
	 * @return true if a permit was acquired to allow the current web service call to proceed.
	 */
	private boolean attemptAccess() {

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
	private boolean releaseAccess() {
		concurrentCalls_semaphore.release();
		return true;
	}
}
