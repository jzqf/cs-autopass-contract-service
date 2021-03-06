package com.qfree.cs.autopass.ws.service;

import java.sql.SQLException;
import java.util.Map;

public interface ContractService {

	public abstract Map<String, Object> contractCreateTest(
			String username,
			String password,
			String obuID,
			String licencePlate,
			Integer licencePlateCountryID) throws SQLException;

	public abstract Map<String, Object> contractCreate(
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
			int licencePlateCountryID) throws SQLException;

	public abstract Map<String, Object> ServiceTest(String username, String password) throws SQLException;

	public abstract Map<String, Object> paymentMethodGet(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int systemActorID,
			String username,
			String password) throws SQLException;

	public abstract Map<String, Object> paymentMethodUpdate(
			int clientNumber,
			int accountNumber,
			String invoiceNumber,
			int paymentMethodID,
			int systemActorID,
			String username,
			String password) throws SQLException;

}