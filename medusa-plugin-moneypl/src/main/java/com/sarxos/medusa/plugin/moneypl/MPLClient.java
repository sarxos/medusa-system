package com.sarxos.medusa.plugin.moneypl;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MPLClient extends DefaultHttpClient {

	public static class NaiveCookieSpecFactory implements CookieSpecFactory {

		@Override
		public CookieSpec newInstance(HttpParams httpparams) {
			return new BrowserCompatSpec() {

				@Override
				public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
					// do nothing
				}
			};
		}

	}

	public static class MateRedirectStrategy extends DefaultRedirectStrategy {

		@Override
		public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
			// ugly W/A for HTTP Client issue
			HttpUriRequest redirect = super.getRedirect(request, response, context);
			URI uri = redirect.getURI();
			String host = uri.getHost();
			((RequestWrapper) request).getOriginal().setHeader("Host", host);
			return redirect;
		}

	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MPLClient.class.getSimpleName());

	/**
	 * Proxy host.
	 */
	private static String PROXY_HOST = (String) System.getProperties().get("http.proxyHost");

	/**
	 * Proxy port number.
	 */
	private static String PROXY_PORT = (String) System.getProperties().get("http.proxyPort");

	static {
		if (PROXY_HOST != null && PROXY_PORT != null) {
			LOG.info("Setting proxy '" + PROXY_HOST + ":" + PROXY_PORT + "'");
		}
	}

	/**
	 * HTTP proxy.
	 */
	private HttpHost proxy = null;

	public MPLClient() {
		super();
		init();
	}

	/**
	 * Initialize client.
	 */
	private void init() {
		if (PROXY_HOST != null && PROXY_PORT != null) {
			proxy = new HttpHost(PROXY_HOST, Integer.parseInt(PROXY_PORT), "http");
			getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		getParams().setBooleanParameter("http.protocol.allow-circular-redirects", false);
		getCookieSpecs().register("naive", new NaiveCookieSpecFactory());
		getParams().setParameter(ClientPNames.COOKIE_POLICY, "naive");
		setRedirectStrategy(new MateRedirectStrategy());
	}

	/**
	 * @return Return proxy used by this client or null if no proxy is used
	 */
	public HttpHost getProxy() {
		return proxy;
	}

	public HttpUriRequest affect(HttpUriRequest req) {

		req.setHeader("Host", "www.mystock.pl");
		req.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
		req.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		req.setHeader("Accept-Language", "pl,en-us;q=0.7,es;q=0.3");
		req.setHeader("Accept-Charset", "ISO-8859-2,utf-8;q=0.7,*;q=0.7");
		req.setHeader("Keep-Alive", "115");
		req.setHeader("Accept-Encoding", "gzip,deflate");
		// req.setHeader("Connection", "keep-alive");

		if (req instanceof HttpPost) {
			req.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		}
		if (getProxy() != null) {
			req.setHeader("Proxy-Connection", "keep-alive");
		}

		return req;
	}

	public void runVoid(HttpUriRequest req) {
		affect(req);
		HttpResponse response = null;
		Header location = null;
		try {
			response = execute(req);
			location = response.getFirstHeader("Location");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.getEntity().getContent().close();
					System.out.println("Close");
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
