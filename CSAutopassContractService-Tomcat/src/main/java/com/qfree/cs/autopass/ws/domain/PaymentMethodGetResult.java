package com.qfree.cs.autopass.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PaymentMethodGetResult")
@XmlType(propOrder = { "paymentMethodID", "paymentMethodName", "errorCode", "errorMessage" })
public class PaymentMethodGetResult {

	private static final int NOERRORCODEASSIGNED_ERRORCODE = -2;
	// We do not initialize the error message to "" because if we do that, an
	// empty XML element will be returned to the webservice client if no error
	// occurs. Currently, the accepted behaviour is that *no* error message XML
	// element be returned at all for this case (just the error code = 0).
	private static final String NOERRORCODEASSIGNED_ERRORMESSAGE = null;

	private int	paymentMethodID;
	private String paymentMethodName;
	private int	errorCode;
	private String errorMessage;

	public PaymentMethodGetResult() {
		// This is the error code returned if no other error code assigned. This
		// should never occur. If we ever do see this error code, we must track
		// down why it is being returned and then fix the logic.
		this.errorCode = NOERRORCODEASSIGNED_ERRORCODE;
		this.errorMessage = NOERRORCODEASSIGNED_ERRORMESSAGE;
	}

    @XmlElement(name="PaymentMethodID", required=false)
	public int getPaymentMethodID() {
		return paymentMethodID;
	}

	public void setPaymentMethodID(int paymentMethodID) {
		this.paymentMethodID = paymentMethodID;
	}

	@XmlElement(name = "PaymentMethodName", required = false)
	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

    @XmlElement(name="ErrorCode", required=true)
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

    @XmlElement(name="ErrorMessage", required=false)
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "PaymentMethodGetResponse -> paymentMethodID: " + Integer.toString(paymentMethodID) +
				", paymentMethodName: " + paymentMethodName + ", errorCode: " +
				Integer.toString(errorCode) + ", errorMessage: " + errorMessage;
    }
}