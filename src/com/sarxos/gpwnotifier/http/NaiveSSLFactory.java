package com.sarxos.gpwnotifier.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;


/**
 * Create naive SSLSocket factories which will authorize any
 * TSL/SSL host.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class NaiveSSLFactory {

	/**
	 * @return Return naive SSL socket factory (authorize any SSL/TSL host)
	 */
	public static SSLSocketFactory createNaiveSSLSocketFactory() {
		X509TrustManager manager = new NaiveX509TrustManager();
		SSLContext sslcontext = null;
		try {
			TrustManager[] managers = new TrustManager[] {manager}; 
			sslcontext = SSLContext.getInstance("SSL"); 
			sslcontext.init(null, managers, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		SSLSocketFactory factory = new SSLSocketFactory(sslcontext); 
		factory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return factory;
	}
}
