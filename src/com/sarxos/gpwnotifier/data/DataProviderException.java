package com.sarxos.gpwnotifier.data;


public class DataProviderException extends Exception {

	private static final long serialVersionUID = 2147500750172014553L;

	public DataProviderException() {
		super();
	}

	public DataProviderException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataProviderException(String message) {
		super(message);
	}

	public DataProviderException(Throwable cause) {
		super(cause);
	}
}
