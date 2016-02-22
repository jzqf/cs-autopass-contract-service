package com.qfree.cs.autopass.ws.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.qfree.cs.autopass.ws.util.WsUtils;

@XmlRootElement(name = "PaymentMethodGetResult")
@XmlType(propOrder = { "paymentMethodID", "paymentMethodName", "errorCode", "errorMessage" })
public class PaymentMethodGetResult {

	private int	paymentMethodID;
	private String paymentMethodName;
	private int	errorCode;
	private String errorMessage;

	public PaymentMethodGetResult() {
		// This is the error code returned if no other error code assigned. This
		// should never occur. If we ever do see this error code, we must track
		// down why it is being returned and then fix the logic.
		this.errorCode = WsUtils.NOERRORCODEASSIGNED_ERRORCODE;
		this.errorMessage = WsUtils.NOERRORCODEASSIGNED_ERRORMESSAGE;
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