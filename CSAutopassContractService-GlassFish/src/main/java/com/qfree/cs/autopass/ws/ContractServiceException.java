package com.qfree.cs.autopass.ws;

public class ContractServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	private String			errorDetails;

	public ContractServiceException(String reason, String errorDetails) {
		super(reason);
		this.errorDetails = errorDetails;
	}

	// One of the frameworks (JAX-WS?) expects this method to be present so that
	// if an exception is thrown, the consumer will see not only the main
	// exception message string passed in "reason", but also the content of
	// "errorDetails".
	public String getFaultInfo() {
		return errorDetails;
	}

}
