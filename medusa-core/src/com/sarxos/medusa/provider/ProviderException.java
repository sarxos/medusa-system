package com.sarxos.medusa.provider;


public class ProviderException extends Exception {

	private static final long serialVersionUID = 2147500750172014553L;

	public ProviderException() {
		super();
	}

	public ProviderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProviderException(String message) {
		super(message);
	}

	public ProviderException(Throwable cause) {
		super(cause);
	}
}
