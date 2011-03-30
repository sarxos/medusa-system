package com.sarxos.medusa.provider;

public class QuoteLackException extends ProviderException {

	private static final long serialVersionUID = 1L;

	public QuoteLackException() {
		super();
	}

	public QuoteLackException(String message, Throwable cause) {
		super(message, cause);
	}

	public QuoteLackException(String message) {
		super(message);
	}

	public QuoteLackException(Throwable cause) {
		super(cause);
	}
}
