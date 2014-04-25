package com.qfree.cs.autopass.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ServiceTestResult")
@XmlType(propOrder = { "errorCode", "errorMessage" })
public class ServiceTestResult {

	private int	errorCode;
	private String errorMessage;

	public ServiceTestResult() {
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
		result.append(" ErrorCode: " + errorCode + NEW_LINE);
		result.append(" ErrorMessage: " + errorMessage + NEW_LINE);
		result.append("}");

		return result.toString();
	}

}
