
//package com.qfree.cs.autopass.ws.domain
package Main;

import javax.xml.bind.annotation.*;

@XmlType(name="PaymentMethodGetResult", propOrder={"paymentMethodID", "paymentMethodName", "errorCode", "errorMessage"})
public class PaymentMethodGet {
	
    @XmlElement(name="PaymentMethodID", required=false)
    public int paymentMethodID;
    @XmlElement(name="PaymentMethodName", required=false)
    public String paymentMethodName;
    @XmlElement(name="ErrorCode", required=true)
    public int errorCode;
    @XmlElement(name="ErrorMessage", required=false)
    public String errorMessage;
    
    PaymentMethodGet() {
        
    }
    
    public String toString() {
        return "PaymentMethodGetResponse -> paymentMethodID: " + Integer.toString(paymentMethodID) + ", paymentMethodName: " + paymentMethodName + ", errorCode: " + Integer.toString(errorCode) + ", errorMessage: " + errorMessage;
    }
}