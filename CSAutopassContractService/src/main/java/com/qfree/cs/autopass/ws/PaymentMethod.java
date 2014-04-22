
package com.qfree.cs.autopass.ws;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.cs.autopass.ws.db.Database;
import com.qfree.cs.autopass.ws.domain.PaymentMethodGet;
import com.qfree.cs.autopass.ws.domain.PaymentMethodUpdate;

@WebService(serviceName = "PaymentMethod")
public class PaymentMethod {
   
	private static final Logger logger = LoggerFactory.getLogger(PaymentMethod.class);

    /**
     * Web service operation
     */
	@WebMethod(operationName = "PaymentMethodGet")
	public PaymentMethodGet paymentMethodGet(
    		@XmlElement(required=true) @WebParam(name = "Username") String Username,				// stralfors
    		@XmlElement(required=true) @WebParam(name = "Password") String Password,				// kF8szBp1lV7Q4SZg
    		@XmlElement(required=true) @WebParam(name = "SystemActorID") int SystemActorID,			// 23
    		@XmlElement(required=false) @WebParam(name = "ClientNumber") int ClientNumber,			// 79000001
    		@XmlElement(required=false) @WebParam(name = "AccountNumber") int AccountNumber,		// 1
    		@XmlElement(required=false) @WebParam(name = "InvoiceNumber") String InvoiceNumber) {
        
	    logger.info("Input parameters:");
		logger.info("ClientNumber[{}]", ClientNumber);
		logger.info("Avtalenummer[{}]", AccountNumber);
		logger.info("SystemActorID[{}]", SystemActorID);

//        Database db = new Database();
		Database data = new Database();       
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		PaymentMethodGet response = new PaymentMethodGet();
		        
		try {
		    
		    data.registerDriver();
		    dbConnection = data.getConnection(connectionString);
		    logger.info("Setting catalog to ServerCommon");
		    dbConnection.setCatalog("ServerCommon");
		 
		    Map result;
		    result = data.paymentMethodGet(dbConnection, ClientNumber, AccountNumber, InvoiceNumber, SystemActorID, Username, Password);
		                  
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
		    try { data.deregisterDriver(); } catch (Exception e) { /* ignored */ }
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
		
		//     Database db = new Database();
		Database data = new Database();
		Connection dbConnection = null;
		String connectionString = getConnectionString();
		PaymentMethodUpdate response = new PaymentMethodUpdate();
		
		try {
		    
		    data.registerDriver();
		    dbConnection = data.getConnection(connectionString);
		    logger.info("Changing to ServerCommon");
		    dbConnection.setCatalog("ServerCommon");
		 
		    Map result;
		    result = data.paymentMethodUpdate(dbConnection, ClientNumber, AccountNumber, InvoiceNumber, PaymentMethodID, SystemActorID, Username, Password);
		                  
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
		    try { data.deregisterDriver(); } catch (Exception e) { /* ignored */ }
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
