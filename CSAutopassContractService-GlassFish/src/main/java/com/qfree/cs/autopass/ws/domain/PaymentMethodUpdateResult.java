
package com.qfree.cs.autopass.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PaymentMethodUpdateResult")
@XmlType(propOrder = { "errorCode", "errorMessage" })
public class PaymentMethodUpdateResult  {
	
	private int	errorCode;
	private String errorMessage;

    public PaymentMethodUpdateResult() {
    }

	@XmlElement(name = "ErrorCode", required = true)
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
		return "PaymentMethodUpdateResponse -> errorCode: " + Integer.toString(errorCode) +
				", errorMessage: " + errorMessage;
    }
}
