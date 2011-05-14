package com.sarxos.medusa.http;

import static org.apache.http.HttpStatus.SC_MOVED_PERMANENTLY;
import static org.apache.http.HttpStatus.SC_MOVED_TEMPORARILY;
import static org.apache.http.HttpStatus.SC_SEE_OTHER;
import static org.apache.http.HttpStatus.SC_TEMPORARY_REDIRECT;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;


/**
 * Browser compatible redirect strategy.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see DefaultHttpClient#setRedirectStrategy(org.apache.http.client.RedirectStrategy)
 */
public class BrowserRedirectStrategy extends DefaultRedirectStrategy {

	/**
	 * Redirectable methods.
	 */
	private static final String[] REDIRECT_METHODS = new String[] {
		HttpGet.METHOD_NAME,
		HttpPost.METHOD_NAME,
		HttpHead.METHOD_NAME
	};

	@Override
	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {

		if (response == null) {
			throw new IllegalArgumentException("HTTP response may not be null");
		}
		if (request == null) {
			throw new IllegalArgumentException("HTTP request may not be null");
		}

		String method = request.getRequestLine().getMethod();

		int status = response.getStatusLine().getStatusCode();
		switch (status) {
			case SC_MOVED_TEMPORARILY:
				Header location = response.getFirstHeader("location");
				return isRedirectable(method) && location != null;
			case SC_MOVED_PERMANENTLY:
			case SC_TEMPORARY_REDIRECT:
				return isRedirectable(method);
			case SC_SEE_OTHER:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Check if given method can be redirected.
	 * 
	 * @param method - method name (e.g. GET, POST, HEAD)
	 * @return true in case if method can be redirected, false otherwise
	 */
	protected boolean isRedirectable(String method) {
		for (String m : REDIRECT_METHODS) {
			if (m.equalsIgnoreCase(method)) {
				return true;
			}
		}
		return false;
	}
}
