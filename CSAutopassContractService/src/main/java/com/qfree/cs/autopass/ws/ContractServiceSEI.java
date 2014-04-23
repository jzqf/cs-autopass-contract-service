package com.qfree.cs.autopass.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import com.qfree.cs.autopass.ws.domain.PaymentMethodGet;

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
		name = "ContractService-WebService_name",
		targetNamespace = "http://WebService_targetNamespace.ws.autopass.qfree.com")
public interface ContractServiceSEI {

	@WebMethod(operationName = "PaymentMethodGet")
	public PaymentMethodGet paymentMethodGet(
			@XmlElement(required = true) @WebParam(name = "Username") String Username,
			@XmlElement(required = true) @WebParam(name = "Password") String Password,
			@XmlElement(required = true) @WebParam(name = "SystemActorID") int SystemActorID,
			@XmlElement(required = false) @WebParam(name = "ClientNumber") int ClientNumber,
			@XmlElement(required = false) @WebParam(name = "AccountNumber") int AccountNumber,
			@XmlElement(required = false) @WebParam(name = "InvoiceNumber") String InvoiceNumber);

}
