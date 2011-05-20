package com.sarxos.medusa.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Default HTTP client used by Medusa System.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MedusaHttpClient extends DefaultHttpClient {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MedusaHttpClient.class.getSimpleName());

	/**
	 * Key for the proxy host property.
	 */
	public static final String PROXY_HOST_KEY = "http.proxyHost";

	/**
	 * Key for the proxy port number property.
	 */
	public static final String PROXY_PORT_KEY = "http.proxyPort";

	/**
	 * HTTP proxy.
	 */
	private HttpHost proxy = null;

	public MedusaHttpClient(ClientConnectionManager conman, HttpParams params) {
		super(conman, params);
		init();
	}

	public MedusaHttpClient(ClientConnectionManager conman) {
		super(conman);
		init();
	}

	public MedusaHttpClient(HttpParams params) {
		super(params);
		init();
	}

	public MedusaHttpClient() {
		super();
		init();
	}

	/**
	 * Initialize client.
	 */
	private void init() {

		// set proxy
		String proxyHost = System.getProperty(PROXY_HOST_KEY);
		String proxyPort = System.getProperty(PROXY_PORT_KEY);
		if (proxyHost != null && proxyPort != null) {
			LOG.info("Setting proxy '" + proxyHost + ":" + proxyPort + "'");
			proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort), "http");
			getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		// do not allow circular redirects
		getParams().setBooleanParameter("http.protocol.allow-circular-redirects", false);

		// accept all cookies - even incorrect ones
		getCookieSpecs().register("naive", new NaiveCookieSpecFactory());
		getParams().setParameter(ClientPNames.COOKIE_POLICY, "naive");

		// redirect 30X all requests (GET, POST, HEAD)
		setRedirectStrategy(new BrowserRedirectStrategy());
	}

	/**
	 * @return Return proxy used by this client or null if no proxy is used
	 */
	public HttpHost getProxy() {
		return proxy;
	}

	public HttpUriRequest affect(HttpUriRequest req) {

		req.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
		req.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		req.setHeader("Accept-Language", "pl,en-us;q=0.7,es;q=0.3");
		req.setHeader("Accept-Charset", "ISO-8859-2,utf-8;q=0.7,*;q=0.7");
		req.setHeader("Keep-Alive", "115");
		req.setHeader("Accept-Encoding", "gzip,deflate");
		req.setHeader("Connection", "keep-alive");

		if (req instanceof HttpPost) {
			req.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		}
		if (getProxy() != null) {
			req.setHeader("Proxy-Connection", "keep-alive");
		}

		return req;
	}

	public void executeVoid(HttpUriRequest req) {
		affect(req);
		HttpResponse response = null;
		try {
			response = execute(req);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.getEntity().getContent().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Download file from specific URL.
	 * 
	 * @param from - file to download
	 * @param to - file to be stored locally
	 * @throws HttpException when something wrong happens
	 */
	public void download(String from, File to) throws HttpException {
		int attempts = 0;
		int max = 5;
		do {
			try {
				download0(from, to);
				return;
			} catch (HttpException e) {
				String msg = attempts < max - 1 ? " Trying one more time" : "Fatal";
				LOG.error("Invalid download attempt. " + msg, e);
			}
		} while (attempts++ < max);
	}

	private void download0(String url, File f) throws HttpException {

		if (LOG.isInfoEnabled()) {
			LOG.info("Downloading " + url + " to " + f.getPath());
		}

		try {

			FileUtils.touch(f);

			HttpResponse response = execute(new HttpGet(url));
			OutputStream os = FileUtils.openOutputStream(f);
			InputStream is = response.getEntity().getContent();

			IOUtils.copy(is, os);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);

		} catch (Exception e) {
			throw new HttpException(
					"Cannot download file from '" + url + "' to '" +
					f.getPath() + "'", e);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("URL " + url + " has been downloaded");
		}
	}

	public InputStream ungzip(HttpResponse response) throws MedusaHttpException {

		if (response == null) {
			throw new IllegalArgumentException("Response cannot be null");
		}

		HttpEntity entity = response.getEntity();
		HttpEntity decompressing = null;

		int size = 32768;

		Header header = response.getFirstHeader("Content-Length");

		if (header != null) {
			String val = header.getValue();
			try {
				int s = Integer.parseInt(val);
				if (s < 1024E+3) {
					size = s;
				}
			} catch (NumberFormatException e) {
				throw new MedusaHttpException("Cannot parse Content-Length '" + val + "'", e);
			}
		}

		header = response.getFirstHeader("Content-Encoding");
		if (header != null) {
			String val = header.getValue();
			if ("gzip".equals(val)) {
				decompressing = new GzipDecompressingEntity(entity);
			} else if ("deflat".equals(val)) {
				decompressing = new DeflateDecompressingEntity(entity);
			} else {
				throw new MedusaHttpException("Unsupported compression method '" + val + "'");
			}
		}

		int s = (int) decompressing.getContentLength();
		if (s > 0) {
			size = s;
		}

		byte[] bytes = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(size + 1);

		try {
			decompressing.writeTo(baos);
		} catch (Exception e) {
			throw new MedusaHttpException(e);
		} finally {

			bytes = baos.toByteArray();
			baos.reset();

			if (entity != null) {
				try {
					entity.getContent().close();
				} catch (Exception e) {
					throw new MedusaHttpException("Cannot close entity stream", e);
				}
			}
		}

		return new ByteArrayInputStream(bytes);
	}
}
