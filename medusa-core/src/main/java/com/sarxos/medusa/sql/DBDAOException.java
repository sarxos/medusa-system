package com.sarxos.medusa.sql;

public class DBDAOException extends Exception {

	private static final long serialVersionUID = -4377500566345671689L;

	public DBDAOException() {
		super();
	}

	public DBDAOException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBDAOException(String message) {
		super(message);
	}

	public DBDAOException(Throwable cause) {
		super(cause);
	}
}
