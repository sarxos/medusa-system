package com.sarxos.gpwnotifier.comm.smeskom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sarxos.gpwnotifier.comm.Message;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXRequest;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXResponse;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMSSend;


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

		if (phost != null && pport != null) {
			
			System.out.println(phost);
			System.out.println(pport);
			
			HttpHost proxy = new HttpHost(phost, Integer.parseInt(pport), "http");
			client.getParams().setParameter(
					ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		
		
		
	}
	
	protected SmesXResponse execute(SmesXRequest request) {

		byte[] bytes = marshall(request);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		InputStreamEntity ise = new InputStreamEntity(bais, bytes.length);
		
		String url = "https://" + endpoint + ":" + port + "/smesx"; 
		
		System.out.println("endpoint: " + url);
		
		HttpPost post = new HttpPost(url);
		post.setHeader("User-Agent", userAgent);
		post.setHeader("Content-Type", "application/xml");
		post.setEntity(ise);
		post.getParams().setBooleanParameter("http.protocol.expect-continue", false);

		System.out.println(new String(bytes));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String xml = null; 
		
		try {
			HttpResponse response = client.execute(post);;
			
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
			
			HttpEntity entity = response.getEntity();
			
			entity.writeTo(baos);
			entity.consumeContent();
			
			xml = new String(baos.toByteArray());
			
			baos.reset();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(xml);
		
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
