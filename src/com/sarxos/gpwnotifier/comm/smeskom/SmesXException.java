package com.sarxos.gpwnotifier.comm.smeskom;

import org.apache.http.Header;


public class SmesXException extends Exception {

	private static final long serialVersionUID = -2841690819419348522L;

	private Header[] headers = null;
	
	public SmesXException() {
		super();
	}

	public SmesXException(String message, Throwable cause, Header[] headers) {
		super(message, cause);
		this.headers = headers;
	}

	public SmesXException(String message, Header[] headers) {
		super(message);
		this.headers = headers;
	}

	public SmesXException(Throwable cause, Header[] headers) {
		super(cause);
		this.headers = headers;
	}

	public Header[] getHeaders() {
		return headers;
	}
}
