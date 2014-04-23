package com.qfree.cs.autopass.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlElement;

import com.qfree.cs.autopass.ws.domain.PaymentMethodGet;
import com.qfree.cs.autopass.ws.domain.PaymentMethodUpdate;

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
		name = "ContractService-WebService_name",
		targetNamespace = "http://WebService_targetNamespace.ws.autopass.qfree.com")
/*
 * When RPC style is used instead of DOCUMENT, no schema gets generated for 
 * types. Instead of referring to the schema in the WSDL document within the 
 * <types> element, the types are defined inline in the WSDL document after an 
 * *empty* <types/> element. The advantage of using DOCUMENT style is that the 
 * input and output values can be validated against the generated schema (which 
 * might include, e.g., minOccurs="..." & maxOccurs="..." attributes).
 */
@SOAPBinding(style = Style.DOCUMENT)
public interface ContractServiceSEI {

	@WebMethod(operationName = "PaymentMethodGet")
	public PaymentMethodGet paymentMethodGet(
			@XmlElement(required = true) @WebParam(name = "Username") String Username,
			@XmlElement(required = true) @WebParam(name = "Password") String Password,
			@XmlElement(required = true) @WebParam(name = "SystemActorID") int SystemActorID,
			@XmlElement(required = false) @WebParam(name = "ClientNumber") int ClientNumber,
			@XmlElement(required = false) @WebParam(name = "AccountNumber") int AccountNumber,
			@XmlElement(required = false) @WebParam(name = "InvoiceNumber") String InvoiceNumber);

	@WebMethod(operationName = "PaymentMethodUpdate", exclude = true)
	public PaymentMethodUpdate paymentMethodUpdate(
			@XmlElement(required = true) @WebParam(name = "Username") String Username,
			@XmlElement(required = true) @WebParam(name = "Password") String Password,
			@XmlElement(required = true) @WebParam(name = "SystemActorID") int SystemActorID,
			@XmlElement(required = false) @WebParam(name = "ClientNumber") int ClientNumber,
			@XmlElement(required = false) @WebParam(name = "AccountNumber") int AccountNumber,
			@XmlElement(required = false) @WebParam(name = "InvoiceNumber") String InvoiceNumber,
			@XmlElement(required = true) @WebParam(name = "PaymentMethodID") int PaymentMethodID);

}
