
package com.qfree.cs.autopass.ws.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase.jdbcx.SybDriver;

public class Database {

	private static final Logger logger = LoggerFactory.getLogger(Database.class);

	public void registerDriver() {
    	
		try {
		
			logger.info("Loading jConnect JDBC driver so it can be registered.");
			
			SybDriver sybDriver = (SybDriver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance();
			sybDriver.setVersion(com.sybase.jdbcx.SybDriver.VERSION_7);
			
			logger.info("jConnect version: {}.{}", sybDriver.getMajorVersion(), sybDriver.getMinorVersion());
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
		
		}
		catch (Exception e) {      
			logger.error("An exception was thrown:", e);
		}
	}
	    
	public void deregisterDriver() {
		
		try {
		
			logger.info("Loading jConnect JDBC driver so it can be deregistered.");
			
			SybDriver sybDriver = (SybDriver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance();
			sybDriver.setVersion(com.sybase.jdbcx.SybDriver.VERSION_7);
			
			logger.info("Deregistering jConnect JDBC driver.");
			DriverManager.deregisterDriver(sybDriver);
		    
		}
		catch (Exception e) {            
			logger.error("An exception was thrown:", e);
		}
	}
    
	public Connection getConnection(String connectionString) {
	    
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
		}
		catch (Exception e) {            
			logger.error("An exception was thrown:", e);
		}
		
		return dbConnection;
	}

	public Map paymentMethodUpdate(
			Connection dbConnection,
			int ClientNumber,
			int Avtalenummer,
			String InvoiceNumber,
			int PaymentMethodID,
			int SystemActorID,
			String username,
			String password) {

		Map result = new HashMap();
		CallableStatement cs = null;
		// ResultSet rs = null;

        try {

			logger.info("Setting catalog to ServerCommon");
            dbConnection.setCatalog("ServerCommon");        
			logger.info("Attempting to execute qp_WSC_PaymentMethodUpdate: username[{}] password[{}]", username,
					password);
            cs = dbConnection.prepareCall("{ call qp_WSC_PaymentMethodUpdate }, ?, ?, ?, ?, ?, ?, ?, ?, ?");
            
            if(ClientNumber >= 0) {
                cs.setInt("@ip_ClientNumber", ClientNumber);          
            }
            else {
                cs.setNull("@ip_ClientNumber", Types.NUMERIC);
            }
            
            if(Avtalenummer >= 0) {
                cs.setInt("@ip_AccountNumber", Avtalenummer); 
            }
            else {
                cs.setNull("@ip_AccountNumber", Types.NUMERIC);
            }
            
            if(SystemActorID >= 0) {
                cs.setInt("@ip_SystemActorID", SystemActorID);                         
            }
            else {
                cs.setNull("@ip_SystemActorID", Types.NUMERIC);
            }
            
            if(SystemActorID >= 0) {
                cs.setInt("@ip_PaymentMethodID", PaymentMethodID);                         
            }
            else {
                cs.setNull("@ip_PaymentMethodID", Types.TINYINT);
            }
            
            cs.setString("@ip_Username", username);
            cs.setString("@ip_Password", password);
            cs.setString("@ip_InvoiceNumber", InvoiceNumber);     
            
            cs.registerOutParameter("@op_ErrorCode", Types.INTEGER);
            cs.registerOutParameter("@op_ErrorMessage", Types.VARCHAR, 255);
            
            cs.execute();
            
            result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
            result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));
        }
        catch (Exception e) {            
			logger.error("An exception was thrown:", e);
        }
        finally {
			// if (rs != null) {
			// try { rs.close(); } catch (Exception e) { /* ignored */ }
			// }
			if (cs != null) {
				try {
					cs.close();
				} catch (Exception e) { /* ignored */
				}
			}
        }           
        
        return result;
	}

	public Map paymentMethodGet(
			Connection dbConnection,
			int ClientNumber,
			int Avtalenummer,
			String InvoiceNumber,
			int SystemActorID,
			String username,
			String password) {

        Map result = new HashMap();
        CallableStatement cs = null;
        //ResultSet rs = null;
        
        try {

    		logger.info("Setting catalog to ServerCommon");
    		dbConnection.setCatalog("ServerCommon");        
    		logger.info("Attempting to execute qp_WSC_PaymentMethodGet: username[{}] password[{}]", username, password);
            cs = dbConnection.prepareCall("{call qp_WSC_PaymentMethodGet(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setString("@ip_Username", username);
            cs.setString("@ip_Password", password);
            
            if(ClientNumber >= 0) {
                cs.setInt("@ip_ClientNumber", ClientNumber);                  
            }
            else {
                cs.setNull("@ip_ClientNumber", Types.NUMERIC);
            }
            
            if(Avtalenummer >= 0) {
                cs.setInt("@ip_AccountNumber", Avtalenummer); 
            }
            else {
                cs.setNull("@ip_AccountNumber", Types.NUMERIC);
            }
            cs.setString("@ip_InvoiceNumber", InvoiceNumber);
            
            if(SystemActorID >= 0) {
                cs.setInt("@ip_SystemActorID", SystemActorID);                  
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
        }
        catch (Exception e) {            
			logger.error("An exception was thrown:", e);
        }
        finally {
//        	if (rs != null) {
//    			try { rs.close(); } catch (Exception e) { /* ignored */ }
//    		}
			if (cs != null) {
				try {
					cs.close();
				} catch (Exception e) { /* ignored */
				}
			}
        }           
        
        return result;
    }    
}
