package com.sarxos.gpwnotifier.comm.smeskom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.message.BasicNameValuePair;

import com.sarxos.gpwnotifier.comm.Message;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXExecutionStatus;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXRequest;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXResponse;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMSSend;
import com.sarxos.gpwnotifier.http.HTTPClient;
import com.sarxos.gpwnotifier.http.NaiveSSLFactory;


/**
 * 
 * @see http://www.howardism.org/Technical/Java/SelfSignedCerts.html
 * @author Bartosz Firyn (SarXos)
 */
public class SmesXProvider {

	/**
	 * Default SmesX endpoint.
	 */
	public static final String DEFAULT_ENDPOINT = "smesx1.smeskom.pl"; 

	/**
	 * Default SmesX port.
	 */
	public static final int DEFAULT_PORT = 2200; 
	
	/**
	 * Current SmesX user.
	 */
	private String user = null;
	
	/**
	 * Current SmesX password.
	 */
	private String password = null;
	
	/**
	 * Current SmesX endpoint.
	 */
	private String endpoint = DEFAULT_ENDPOINT;
	
	/**
	 * Current SmesX port.
	 */
	private int port = DEFAULT_PORT;
	
	/**
	 * JAXB marshaller used to marshall SmesX entities. 
	 */
	private Marshaller marshaller = null;
	
	/**
	 * JAXB unmarshaller used to unmarshall SmesX entities. 
	 */
	private Unmarshaller unmarshaller = null;
	
	/**
	 * Apache HTPP client used to send HTTP requests.
	 */
	private HTTPClient client = null; 
	
	/**
	 * Default user agent header value.
	 */
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

		client = HTTPClient.getInstance();
		
		// add naive HTTPS schema for port 2200
		SSLSocketFactory factory = NaiveSSLFactory.createNaiveSSLSocketFactory();
		ClientConnectionManager manager = client.getConnectionManager();
		SchemeRegistry registry = manager.getSchemeRegistry();

		// check if schema is already registered
		Scheme scheme = registry.getScheme(new HttpHost(endpoint, port, "https"));
		// schema for https port 443 also work fine with port 2200 
		if (scheme == null) {
			registry.register(new Scheme("https", factory, 2200));
		}
	}
	
	protected SmesXResponse execute(SmesXRequest request) throws SmesXException {

		
		byte[] bytes = marshall(request);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Header[] headers = null;
		
		try {
			String xml = new String(bytes);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("xml", xml));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
			
			String url = "https://" + endpoint + ":" + port + "/smesx"; 
			HttpPost post = client.createPost(url);
			post.setHeader("User-Agent", userAgent);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(entity);
			
			// TODO write request xml file
			//entity.writeTo(baos);
			//baos.reset();
			
			HttpResponse response = client.execute(post);
			HttpEntity rentity = response.getEntity();
			
			headers = response.getAllHeaders();
			
			rentity.writeTo(baos);
			rentity.consumeContent();

			// TODO write response xml to file 
			bytes = baos.toByteArray();
			baos.reset();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bytes.length == 0) {
			throw new SmesXException("SmesX response is empty!", headers);
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes); 
		
		try {
			return (SmesXResponse) unmarshaller.unmarshal(bais);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
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
	
	public boolean sendSMS(Message msg) throws SmesXException {
		
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
		
		SmesXResponse response = execute(request);
		
		return response.getExecutionStatus() == SmesXExecutionStatus.SUCCESS;
	}
	
	public static void main(String[] args) {
		SmesXProvider p = new SmesXProvider("htguser2647", "PQZ5VnBq");
		try {
			p.sendSMS(new Message("+48509934614", "Test A", "127856"));
		} catch (SmesXException e) {
			e.printStackTrace();
		}
	}
}
