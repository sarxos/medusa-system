package com.sarxos.gpwnotifier.http;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * Default HTTP client.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class HTTPClient extends DefaultHttpClient {

	/**
	 * Singleton instance.
	 */
	private static HTTPClient instance = null;
	
	/**
	 * @return Singleton instance.
	 */
	public static HTTPClient getInstance() {
		if (instance == null) {
			instance = create();
		}
		return instance;
	}
	
	/**
	 * @return New instance of HTTP client. 
	 */
	protected static HTTPClient create() {

		HTTPClient client = new HTTPClient();
		
		/* NOTE!
		 * Set up TSL/SSL naive settings.  
		 */
		
		SSLSocketFactory factory = NaiveSSLFactory.createNaiveSSLSocketFactory();
		ClientConnectionManager manager = client.getConnectionManager();

		// add https 443 by default 
		SchemeRegistry registry = manager.getSchemeRegistry();
		registry.register(new Scheme("https", factory, 443));
		
		/* NOTE!
		 * Set up proxy settings.
		 */

		String phost = (String)System.getProperties().get("http.proxyHost");
		String pport = (String)System.getProperties().get("http.proxyPort");

		if (phost != null && pport != null) {
			
			int port = -1;
			try {
				port = Integer.parseInt(pport);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Incorrect proxy port '" + pport + "'", e);
			}
			
			HttpHost proxy = new HttpHost(phost, port, "http");
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		
		
		return client;
	}
	
	/**
	 * Private.
	 */
	private HTTPClient() {
	}
	
	public HttpPost createPost(String uri) {
		HttpPost post = new HttpPost(uri); 
		post.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		return post;
	}
}
