package com.qfree.cs.autopass.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlElement;

import com.qfree.cs.autopass.ws.domain.ContractCreateResult;
import com.qfree.cs.autopass.ws.domain.ContractCreateTestResult;
import com.qfree.cs.autopass.ws.domain.PaymentMethodGetResult;
import com.qfree.cs.autopass.ws.domain.PaymentMethodUpdateResult;
import com.qfree.cs.autopass.ws.domain.ServiceTestResult;
import com.qfree.cs.autopass.ws.service.ContractService;

/**
 * This is the SEI (Service Endpoint Interface) for the web service.
 * 
 * This is the SEI at the server/endpoint end of the service. The client
 * application will also have a separate SEI which will be implemented in 
 * whichever language is used there (not necessarily Java).
 * 
 * @author jeffreyz
 *
 */
/*
 * name:			Specifies the name of the service interface. This property 
 * 					is mapped to the name attribute of the wsdl:portType element 
 * 					that defines the service's interface in a WSDL contract.
 *
 * targetNamespace:	Specifies the target namespace under which the service is 
 * 					defined. If this property is not specified, the target 
 * 					namespace is derived from the package name.
 */
@WebService(
		name = "ContractServicePortType",
		targetNamespace = "http://ws.autopass.qfree.com")
/*
 * When RPC style is used instead of DOCUMENT, no schema gets generated for 
 * types. Instead of referring to the schema in the WSDL document within the 
 * <types> element, the types are defined inline in the WSDL document after an 
 * *empty* <types/> element. The advantage of using DOCUMENT style is that the 
 * input and output values can be validated against the generated schema (which 
 * might include, e.g., minOccurs="..." & maxOccurs="..." attributes).
 */
@SOAPBinding(style = Style.DOCUMENT)
public interface ContractWsSEI {

	@WebMethod(operationName = "ContractCreateTest", exclude = false)
	public ContractCreateTestResult contractCreateTest(
			@XmlElement(required = true) @WebParam(name = "Username") String username,
			@XmlElement(required = true) @WebParam(name = "Password") String password,
			@XmlElement(required = true) @WebParam(name = "OBUID") String obuID,
			@XmlElement(required = false) @WebParam(name = "LicencePlate") String licencePlate,
			@XmlElement(required = false) @WebParam(name = "LicencePlateCountryID") int licencePlateCountryID);

	/**
	 * Web service operation to create a new Client agreement.
	 * 
	 * @param username
	 * @param password
	 * @param clientTypeID
	 * @param firstName
	 * @param lastName
	 * @param birthDate
	 * @param company
	 * @param companyNumber
	 * @param address1
	 * @param address2
	 * @param postCode
	 * @param postOffice
	 * @param countryID
	 * @param eMail
	 * @param phone
	 * @param validFrom
	 * @param obuID
	 * @param vehicleClassID
	 * @param licencePlate
	 * @param licencePlateCountryID
	 * @return
	 */
	@WebMethod(operationName = "ContractCreate", exclude = false)
	public ContractCreateResult contractCreate(
			@XmlElement(required = true) @WebParam(name = "Username") String username,
			@XmlElement(required = true) @WebParam(name = "Password") String password,
			@XmlElement(required = true) @WebParam(name = "ClientTypeID") int clientTypeID,
			@XmlElement(required = true) @WebParam(name = "FirstName") String firstName,
			@XmlElement(required = true) @WebParam(name = "LastName") String lastName,
			@XmlElement(required = false) @WebParam(name = "BirthDate") String birthDate,
			@XmlElement(required = false) @WebParam(name = "Company") String company,
			@XmlElement(required = false) @WebParam(name = "CompanyNumber") String companyNumber,
			@XmlElement(required = true) @WebParam(name = "Address1") String address1,
			@XmlElement(required = false) @WebParam(name = "Address2") String address2,
			@XmlElement(required = true) @WebParam(name = "PostCode") String postCode,
			@XmlElement(required = true) @WebParam(name = "PostOffice") String postOffice,
			@XmlElement(required = true) @WebParam(name = "CountryID") int countryID,
			@XmlElement(required = false) @WebParam(name = "EMail") String eMail,
			@XmlElement(required = false) @WebParam(name = "Phone") String phone,
			@XmlElement(required = true) @WebParam(name = "ValidFrom") String validFrom,
			@XmlElement(required = true) @WebParam(name = "OBUID") String obuID,
			@XmlElement(required = true) @WebParam(name = "VehicleClassID") int vehicleClassID,
			@XmlElement(required = true) @WebParam(name = "LicencePlate") String licencePlate,
			@XmlElement(required = true) @WebParam(name = "LicencePlateCountryID") int licencePlateCountryID);

	/**
	 * WS operation to test if the "CreateClient" WS is available.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@WebMethod(operationName = "ServiceTest", exclude = false)
	public ServiceTestResult serviceTest(
			@XmlElement(required = true) @WebParam(name = "Username") String username,
			@XmlElement(required = true) @WebParam(name = "Password") String password);

	/**
	 * This is Roy's old WS operation,converted to use my new platform.
	 * 
	 * This operation is not exposed (exclude=false). It was used only for
	 * testing during development, but I left it in, in case it  might be useful
	 * in the future.
	 * 
	 * @param username
	 * @param password
	 * @param systemActorID
	 * @param clientNumber
	 * @param accountNumber
	 * @param invoiceNumber
	 * @return
	 */
	@WebMethod(operationName = "PaymentMethodGet", exclude = false)
	public PaymentMethodGetResult paymentMethodGet(
			@XmlElement(required = true) @WebParam(name = "Username") String username,
			@XmlElement(required = true) @WebParam(name = "Password") String password,
			@XmlElement(required = true) @WebParam(name = "SystemActorID") int systemActorID,
			@XmlElement(required = false) @WebParam(name = "ClientNumber") int clientNumber,
			@XmlElement(required = false) @WebParam(name = "AccountNumber") int accountNumber,
			@XmlElement(required = false) @WebParam(name = "InvoiceNumber") String invoiceNumber);

	/**
	 * This is Roy's old WS operation,converted to use my new platform.
	 * 
	 * This operation is not exposed (exclude=false). It was used only for
	 * testing during development, but I left it in, in case it  might be useful
	 * in the future.
	 * 
	 * @param username
	 * @param password
	 * @param systemActorID
	 * @param clientNumber
	 * @param accountNumber
	 * @param invoiceNumber
	 * @param paymentMethodID
	 * @return
	 */
	@WebMethod(operationName = "PaymentMethodUpdate", exclude = false)
	public PaymentMethodUpdateResult paymentMethodUpdate(
			@XmlElement(required = true) @WebParam(name = "Username") String username,
			@XmlElement(required = true) @WebParam(name = "Password") String password,
			@XmlElement(required = true) @WebParam(name = "SystemActorID") int systemActorID,
			@XmlElement(required = false) @WebParam(name = "ClientNumber") int clientNumber,
			@XmlElement(required = false) @WebParam(name = "AccountNumber") int accountNumber,
			@XmlElement(required = false) @WebParam(name = "InvoiceNumber") String invoiceNumber,
			@XmlElement(required = true) @WebParam(name = "PaymentMethodID") int paymentMethodID);

	@WebMethod(exclude = true)
	public ContractService getContractService();

	@WebMethod(exclude = true)
	public void setContractService(ContractService contractService);

}
