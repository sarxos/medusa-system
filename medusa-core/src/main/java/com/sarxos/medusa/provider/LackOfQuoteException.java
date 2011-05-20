package com.sarxos.medusa.provider;

public class LackOfQuoteException extends ProviderException {

	private static final long serialVersionUID = 1L;

	public LackOfQuoteException() {
		super();
	}

	public LackOfQuoteException(String message, Throwable cause) {
		super(message, cause);
	}

	public LackOfQuoteException(String message) {
		super(message);
	}

	public LackOfQuoteException(Throwable cause) {
		super(cause);
	}
}
