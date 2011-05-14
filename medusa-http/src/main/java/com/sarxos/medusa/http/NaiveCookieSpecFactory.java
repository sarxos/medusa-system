package com.sarxos.medusa.http;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;


/**
 * Naive cookie specification factory. It returns cookie specification which
 * accepts all cookies.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class NaiveCookieSpecFactory implements CookieSpecFactory {

	/**
	 * Used cookie specification.
	 */
	private final static CookieSpec SPEC = new BrowserCompatSpec() {

		@Override
		public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
			// do nothing - accept all cookies
		}
	};

	@Override
	public CookieSpec newInstance(HttpParams httpparams) {
		return SPEC;
	}
}
