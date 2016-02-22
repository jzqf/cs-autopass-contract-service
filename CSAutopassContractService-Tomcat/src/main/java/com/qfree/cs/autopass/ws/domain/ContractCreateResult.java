package com.qfree.cs.autopass.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ContractCreateResult")
@XmlType(propOrder = { "clientNumber", "errorCode", "errorMessage" })
public class ContractCreateResult {

	private static final int NOERRORCODEASSIGNED_ERRORCODE = -2;
	// We do not initialize the error message to "" because if we do that, an
	// empty XML element will be returned to the webservice client if no error
	// occurs. Currently, the accepted behaviour is that *no* error message XML
	// element be returned at all for this case (just the error code = 0).
	//
	// 2014.07.01: Kjetil asked for this to be changed so that an empty string
	// is returned instead.
	private static final String NOERRORCODEASSIGNED_ERRORMESSAGE = "";	// null;

	private String clientNumber;
	private int	errorCode;
	private String errorMessage;

	public ContractCreateResult() {
		// This is the error code returned if no other error code assigned. This
		// should never occur. If we ever do see this error code, we must track
		// down why it is being returned and then fix the logic.
		this.errorCode = NOERRORCODEASSIGNED_ERRORCODE;
		this.errorMessage = NOERRORCODEASSIGNED_ERRORMESSAGE;
	}

	@XmlElement(name = "ClientNumber", required = false)
	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	@XmlElement(name = "ErrorCode", required = false)
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement(name = "ErrorMessage", required = false)
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(this.getClass().getName() + " object {" + NEW_LINE);
		result.append(" ClientNumber: " + clientNumber + NEW_LINE);
		result.append(" ErrorCode: " + errorCode + NEW_LINE);
		result.append(" ErrorMessage: " + errorMessage + NEW_LINE);
		result.append("}");

		return result.toString();
	}

}
