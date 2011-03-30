package com.sarxos.medusa.data;

import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HTTP client used by Medusa.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MedusaHttpClient extends DefaultHttpClient {

	/**
	 * Logger. 
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MedusaHttpClient.class.getSimpleName());
	
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
	
	
	public MedusaHttpClient() {
		super();
		init();
	}

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

	/**
	 * Initialize client.
	 */
	private void init() {
		if (PROXY_HOST != null && PROXY_PORT != null) {
			proxy = new HttpHost(PROXY_HOST, Integer.parseInt(PROXY_PORT), "http");
			getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
	}
	
	public HttpHost getProxy() {
		return proxy;
	}
}
