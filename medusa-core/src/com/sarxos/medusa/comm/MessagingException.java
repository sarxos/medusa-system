package com.sarxos.medusa.comm;


public class MessagingException extends Exception {

	private static final long serialVersionUID = -2229988185917534476L;

	public MessagingException() {
		super();
	}

	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessagingException(String message) {
		super(message);
	}

	public MessagingException(Throwable cause) {
		super(cause);
	}
}
