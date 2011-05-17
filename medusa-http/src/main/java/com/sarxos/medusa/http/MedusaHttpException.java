package com.sarxos.medusa.http;

public class MedusaHttpException extends Exception {

	private static final long serialVersionUID = 1L;

	public MedusaHttpException() {
		super();
	}

	public MedusaHttpException(String message, Throwable cause) {
		super(message, cause);
	}

	public MedusaHttpException(String message) {
		super(message);
	}

	public MedusaHttpException(Throwable cause) {
		super(cause);
	}
}
