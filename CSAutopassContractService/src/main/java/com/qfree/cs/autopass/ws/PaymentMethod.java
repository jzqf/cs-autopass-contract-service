
package com.qfree.cs.autopass.ws;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.cs.autopass.ws.db.Database;
import com.qfree.cs.autopass.ws.domain.PaymentMethodGet;
import com.qfree.cs.autopass.ws.domain.PaymentMethodUpdate;

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
public class PaymentMethod implements ContractServiceSEI {
   
	private static final Logger logger = LoggerFactory.getLogger(PaymentMethod.class);

	@Override
	public PaymentMethodGet paymentMethodGet(
			String Username,			// stralfors
			String Password,			// kF8szBp1lV7Q4SZg
			int SystemActorID,			// 23
			int ClientNumber,			// 79000001
			int AccountNumber,			// 1
			String InvoiceNumber) {
        
	    logger.info("Input parameters:");
		logger.info("ClientNumber[{}]", ClientNumber);
		logger.info("Avtalenummer[{}]", AccountNumber);
		logger.info("SystemActorID[{}]", SystemActorID);

		Database db = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		PaymentMethodGet response = new PaymentMethodGet();
		        
		try {
		    
			db.registerDriver();
			dbConnection = db.getConnection(connectionString);
		    logger.info("Setting catalog to ServerCommon");
		    dbConnection.setCatalog("ServerCommon");
		 
		    Map result;
			result = db.paymentMethodGet(dbConnection, ClientNumber, AccountNumber, InvoiceNumber, SystemActorID,
					Username, Password);
		                  
		    if(result.get("ErrorCode").toString().equals("0")) {
		        response.paymentMethodID =  Integer.parseInt(result.get("PaymentMethodID").toString());
		        response.paymentMethodName = result.get("PaymentMethod").toString();
		        response.errorCode = 0;
		    }            
		    else {
		        response.errorCode = Integer.parseInt(result.get("ErrorCode").toString());
		        response.errorMessage = result.get("ErrorMessage").toString();
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
		    try { dbConnection.close(); } catch (Exception e) { /* ignored */ }
		}
		
		return response;
	}

	@WebMethod(operationName = "PaymentMethodUpdate")
	public PaymentMethodUpdate paymentMethodUpdate(
    		@XmlElement(required=true) @WebParam(name = "Username") String Username,
			@XmlElement(required=true) @WebParam(name = "Password") String Password,
			@XmlElement(required=true) @WebParam(name = "SystemActorID") int SystemActorID,
			@XmlElement(required=false) @WebParam(name = "ClientNumber") int ClientNumber,
			@XmlElement(required=false) @WebParam(name = "AccountNumber") int AccountNumber,
			@XmlElement(required=false) @WebParam(name = "InvoiceNumber") String InvoiceNumber,
			@XmlElement(required=true) @WebParam(name = "PaymentMethodID") int PaymentMethodID) {

		logger.info("Input parameters:");
		logger.info("ClientNumber[{}]", ClientNumber);
		logger.info("Avtalenummer[{}]", AccountNumber);
		logger.info("SystemActorID[{}]", SystemActorID);
		logger.info("PaymentMethodID[{}]", PaymentMethodID);
		
		Database db = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		PaymentMethodUpdate response = new PaymentMethodUpdate();
		
		try {
		    
			db.registerDriver();
			dbConnection = db.getConnection(connectionString);
		    logger.info("Changing to ServerCommon");
		    dbConnection.setCatalog("ServerCommon");
		 
		    Map result;
			result = db.paymentMethodUpdate(dbConnection, ClientNumber, AccountNumber, InvoiceNumber, PaymentMethodID,
					SystemActorID, Username, Password);
		                  
		    if(result.get("ErrorCode").toString().equals("0")) {
		        response.errorCode = 0;
		    }            
		    else {
		        response.errorCode = Integer.parseInt(result.get("ErrorCode").toString());
		        response.errorMessage = result.get("ErrorMessage").toString();
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
		    try { dbConnection.close(); } catch (Exception e) { /* ignored */ }
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
		
//        server = "csnt02.csautopass.no";
//        port = "5000";
//        passwordDB = "qfreet02";
//        usernameDB = "adam";
		
		logger.info("server = {}", server);
		logger.info("port = {}", port);
		logger.info("dbPassword = {}", dbPassword);
		logger.info("dbUsername = {}", dbUsername);
		    
		String connectionString = "jdbc:sybase:Tds:" + server + ":" + port + "?USER=" + dbUsername + "&PASSWORD=" + dbPassword;
		
		logger.info("connectionString = {}", connectionString);
		
		return connectionString;        
        
	}
}
