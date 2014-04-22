
//package com.qfree.cs.autopass.ws.domain
package Main;

import javax.xml.bind.annotation.*;

@XmlType(name="PaymentMethodUpdateResult", propOrder={"errorCode", "errorMessage"})
public class PaymentMethodUpdate  {
	
    @XmlElement(name="ErrorCode", required=true)
    public int errorCode;
    @XmlElement(name="ErrorMessage", required=false)
    public String errorMessage;

    PaymentMethodUpdate() {
    
    }
    
    public String toString() {
        return "PaymentMethodUpdateResponse -> errorCode: " + Integer.toString(errorCode) + ", errorMessage: " + errorMessage;
    }
}
