
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

	public Map contractCreateTest(
			Connection dbConnection,
			String username,
			String password,
			String obuID,
			String licencePlate,
			int licencePlateCountryID) {

		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		CallableStatement cs = null;
		//ResultSet rs = null;

		try {

			logger.info("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

			logger.info("Attempting to execute qp_WSC_ContractCreateTest: with input parameters:\n" +
					" @ip_Username = {}\n" +
					" @ip_Password = {}\n" +
					" @ip_OBUID = {}\n" +
					" @ip_LicencePlate = {}\n" +
					" @ip_LicencePlateCountryID = {}",
					new Object[] { username, password, obuID, licencePlate, new Integer(licencePlateCountryID) });

			// Here, we need one "?" for each input AND output parameter of the stored procedure.
			cs = dbConnection.prepareCall("{call qp_WSC_ContractCreateTest(?, ?, ?, ?, ?, ?, ?)}");

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

			cs.execute();

			result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
			result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));

		} catch (Exception e) {
			logger.error("An exception was thrown executing or processing results from qp_WSC_ContractCreateTest:", e);
		} finally {
			//        	if (rs != null) {
			//    			try { rs.close(); } catch (Exception e) { /* ignored */ }
			//    		}
			if (cs != null) {
				try {
					cs.close();
				} catch (Exception e) {
					/* ignored */
				}
			}
		}

		return result;
	}

	public Map contractCreate(
			Connection dbConnection,
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

		Map result = new HashMap();

		// In case an exception is thrown and we do not get so far below to set 
		// the result error code.
		result.put("ErrorCode", -1);
		result.put("ErrorMessage", "");

		CallableStatement cs = null;
		//ResultSet rs = null;

		try {

			logger.info("Setting catalog to ServerCommon");
			dbConnection.setCatalog("ServerCommon");

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

			// Here, we need one "?" for each input AND output parameter of the stored procedure.
			cs = dbConnection.prepareCall(
					"{call qp_WSC_ContractCreate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			cs.setString("@ip_Username", username);
			cs.setString("@ip_Password", password);
			if (vehicleClassID >= 0) {
				cs.setInt("@ip_ClientTypeID", clientTypeID);
			}
			else {
				cs.setNull("@ip_ClientTypeID", Types.NUMERIC);
			}
			cs.setString("@ip_FirstName", firstName);
			cs.setString("@ip_LastName", lastName);
			cs.setString("@ip_BirthDate", birthDate);	//// DATE!!! ???????????????????????????????????????????????????
			cs.setString("@ip_Company", company);
			cs.setString("@ip_CompanyNumber", companyNumber);
			cs.setString("@ip_Address1", address1);
			cs.setString("@ip_Address2", address2);
			cs.setString("@ip_PostCode", postCode);
			cs.setString("@ip_PostOffice", postOffice);
			if (vehicleClassID >= 0) {
				cs.setInt("@ip_CountryID", countryID);
			}
			else {
				cs.setNull("@ip_CountryID", Types.NUMERIC);
			}
			cs.setString("@ip_EMail", eMail);
			cs.setString("@ip_Phone", phone);
			cs.setString("@ip_ValidFrom", validFrom);	//// DATETIME!!! ???????????????????????????????????????????????????
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
			cs.registerOutParameter("@op_ClientNumber", Types.NUMERIC, 12);		// @op_ClientNumber has Sybase type numeric(12)
			cs.registerOutParameter("@op_ErrorCode", Types.INTEGER);
			cs.registerOutParameter("@op_ErrorMessage", Types.VARCHAR, 255);

			logger.info("Executing stored procedure qp_WSC_ContractCreate on server...");
			cs.execute();
			logger.info("...Done. No exception thrown.");

			result.put("ClientNumber", Long.toString(cs.getLong("@op_ClientNumber")));		// @op_ClientNumber has Sybase type numeric(12)
			result.put("ErrorCode", cs.getInt("@op_ErrorCode"));
			result.put("ErrorMessage", cs.getString("@op_ErrorMessage"));

		} catch (Exception e) {
			logger.error("An exception was thrown executing or processing results from qp_WSC_ContractCreate:", e);
		} finally {
			//        	if (rs != null) {
			//    			try { rs.close(); } catch (Exception e) { /* ignored */ }
			//    		}
			if (cs != null) {
				try {
					cs.close();
				} catch (Exception e) {
					/* ignored */
				}
			}
		}

		return result;
	}

	public Map paymentMethodGet(
			Connection dbConnection,
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int systemActorID,
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

	public Map paymentMethodUpdate(
			Connection dbConnection,
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int paymentMethodID,
			int systemActorID,
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
}
