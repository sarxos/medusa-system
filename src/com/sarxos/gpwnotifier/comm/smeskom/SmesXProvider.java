package com.sarxos.gpwnotifier.comm.smeskom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import com.sarxos.gpwnotifier.comm.Message;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXRequest;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXResponse;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMSSend;


/**
 * 
 * @see http://www.howardism.org/Technical/Java/SelfSignedCerts.html
 * @author Bartosz Firyn (SarXos)
 */
public class SmesXProvider {

	public static final String DEFAULT_ENDPOINT = "smesx1.smeskom.pl"; 

	public static final int DEFAULT_PORT = 2200; 
	
	private String user = null;
	
	private String password = null;
	
	private String endpoint = DEFAULT_ENDPOINT;
	
	private int port = DEFAULT_PORT;
	
	private Marshaller marshaller = null;
	
	private Unmarshaller unmarshaller = null;
	
	private DefaultHttpClient client = null; 
	
	private String userAgent = "SarXos GPW Notifier";

	public SmesXProvider(String user, String password) {
		this(user, password, DEFAULT_ENDPOINT, DEFAULT_PORT);
	}
	
	public SmesXProvider(String user, String password, String endpoint, int port) {
		
		this.user = user;
		this.password = password;
		
		try {
			String pckg = SmesXRequest.class.getPackage().getName();
			JAXBContext jc = JAXBContext.newInstance(pckg);
			
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			unmarshaller = jc.createUnmarshaller();
			
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot create JAXB context", e);
		}

		String phost = (String)System.getProperties().get("http.proxyHost");
		String pport = (String)System.getProperties().get("http.proxyPort");

		client = new DefaultHttpClient();

		ClientConnectionManager ccm = null;
		
		try { 
			X509TrustManager trustManager = new X509TrustManager() { 
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {} 
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {} 
				public X509Certificate[] getAcceptedIssuers() { 
					return null; 
				} 
			}; 

			SSLContext sslcontext = SSLContext.getInstance("SSL"); 
			sslcontext.init(null, new TrustManager[] {trustManager}, null); 

			// Use the above SSLContext to create your socket factory 
			// (I found trying to extend the factory a bit difficult due to a 
			// call to createSocket with no arguments, a method which doesn't 
			// exist anywhere I can find, but hey-ho). 
			SSLSocketFactory sf = new SSLSocketFactory(sslcontext); 
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 

			// If you want a thread safe client, use the ThreadSafeConManager, but 
			// otherwise just grab the one from the current client, and get hold of its 
			// schema registry. THIS IS THE KEY THING. 
			ccm = client.getConnectionManager();
			SchemeRegistry schemeRegistry = ccm.getSchemeRegistry(); 

			// Register our new socket factory with the typical SSL port and the 
			// correct protocol name. 
			schemeRegistry.register(new Scheme("https", sf, port));
			
		} catch (Exception e) {
        	e.printStackTrace();
        }

		client = new DefaultHttpClient(ccm, client.getParams());

		if (phost != null && pport != null) {
			
			System.out.println(phost);
			System.out.println(pport);
			
			HttpHost proxy = new HttpHost(phost, Integer.parseInt(pport), "http");
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		
		
		
	}
	
	protected SmesXResponse execute(SmesXRequest request) {

		String url = "https://" + endpoint + ":" + port + "/smesx"; 
		
		byte[] bytes = marshall(request);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		String xml = new String(bytes);
		
		System.out.println("endpoint: " + url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("xml", xml));
		
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
			
			HttpPost post = new HttpPost(url);
			post.setHeader("User-Agent", userAgent);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(entity);
			post.getParams().setBooleanParameter("http.protocol.expect-continue", false);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			entity.writeTo(baos);
			
			System.out.println("Request:\n" + URLDecoder.decode(new String(baos.toByteArray()), "UTF-8"));
	
			baos.reset();
			
			xml = null; 
		
			HttpResponse response = client.execute(post);
			
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
			
			HttpEntity rentity = response.getEntity();
			
			rentity.writeTo(baos);
			rentity.consumeContent();
			
			System.out.println("Response:");
			xml = new String(baos.toByteArray());
			
			System.out.println(URLDecoder.decode(xml, "UTF-8"));
			
			baos.reset();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println(xml);
		
		return null;
	}
	
	protected byte[] marshall(SmesXRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("SmesX Request c annot be null");
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			marshaller.marshal(request, baos);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot marshall object", e);
		}
		
		return baos.toByteArray();
	}
	
	public boolean sendSMS(Message msg) {
		
		String msisdn = msg.getRecipient();
		String body = msg.getMessage();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, +3); // 3 days expire date
		
		Date expire = calendar.getTime();
		
		SmesXSMSSend sendSMS = new SmesXSMSSend();
		sendSMS.setBody(body);
		sendSMS.setMSISDN(msisdn);
		sendSMS.setExpireDate(expire);
		
		SmesXRequest request = new SmesXRequest();
		request.setOperation(sendSMS);
		request.setUser(user);
		request.setPassword(password);
		
		execute(request);
		
		return true;
	}
	
	public static void main(String[] args) {
		SmesXProvider p = new SmesXProvider("htguser2647", "PQZ5VnBq");
		p.sendSMS(new Message("+48509934614", "Test A"));
	}
}
