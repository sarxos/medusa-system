package com.sarxos.medusa.data.persistence;

/**
 * Persistence exception.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class PersistenceException extends Exception {

	private static final long serialVersionUID = 1389242346839345906L;

	public PersistenceException() {
		super();
	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(Throwable cause) {
		super(cause);
	}
}
