package com.sarxos.medusa.plugin.dde;

/**
 * Yet another, some abstract DDE exception.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DDEException extends Exception {

	private static final long serialVersionUID = 1L;

	public DDEException() {
		super();
	}

	public DDEException(String message, Throwable cause) {
		super(message, cause);
	}

	public DDEException(String message) {
		super(message);
	}

	public DDEException(Throwable cause) {
		super(cause);
	}
}
