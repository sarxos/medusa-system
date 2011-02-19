package com.sarxos.smeskom;

import static com.sarxos.smeskom.v22.SmesXSMSReceiveType.UNREAD;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.http.HTTPClient;
import com.sarxos.medusa.http.NaiveSSLFactory;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.smeskom.v22.SmesXExecutionStatus;
import com.sarxos.smeskom.v22.SmesXOperation;
import com.sarxos.smeskom.v22.SmesXRequest;
import com.sarxos.smeskom.v22.SmesXResponse;
import com.sarxos.smeskom.v22.SmesXSMS;
import com.sarxos.smeskom.v22.SmesXSMSReceive;
import com.sarxos.smeskom.v22.SmesXSMSSend;


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

	/**
	 * Log file for XML requests/responses.
	 */
	protected File log = new File("log/smesx.log");

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

	protected SmesXResponse execute(SmesXOperation operation) throws SmesXException {

		if (operation == null) {
			throw new IllegalArgumentException("SmesX operation cannot be null");
		}

		SmesXRequest request = new SmesXRequest();
		request.setOperation(operation);
		request.setUser(user);
		request.setPassword(password);

		return execute(request);
	}

	protected SmesXResponse execute(SmesXRequest request) throws SmesXException {

		byte[] bytes = marshall(request);

		try {
			FileOutputStream fos = new FileOutputStream(log, true);
			fos.write(bytes);
			fos.write('\n');
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

			synchronized (client) {
				HttpResponse response = client.execute(post);
				HttpEntity rentity = response.getEntity();
				headers = response.getAllHeaders();
				rentity.writeTo(baos);
				rentity.consumeContent();
			}

			bytes = baos.toByteArray();
			baos.reset();

			try {
				FileOutputStream fos = new FileOutputStream(log, true);
				fos.write(bytes);
				fos.write('\n');
				fos.write('\n');
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

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
		String body = msg.getCode() + ": " + msg.getBody();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, +3); // 3 days expire date

		Date expire = calendar.getTime();

		SmesXSMSSend sendSMS = new SmesXSMSSend();
		sendSMS.setBody(body);
		sendSMS.setMSISDN(msisdn);
		sendSMS.setExpireDate(expire);

		SmesXResponse response = execute(sendSMS);

		return response.getExecutionStatus() == SmesXExecutionStatus.SUCCESS;
	}

	public List<Message> receiveUnreadSMSs() throws SmesXException {
		return receiveSMSs(false);
	}

	public List<Message> receiveSMSs(boolean markAsRead) throws SmesXException {

		List<Message> messages = new LinkedList<Message>();

		SmesXSMSReceive receiveSMS = null;

		do {

			receiveSMS = new SmesXSMSReceive();
			receiveSMS.setMarkAsRead(markAsRead);
			receiveSMS.setType(UNREAD);

			SmesXResponse response = execute(receiveSMS);
			SmesXOperation operation = response.getOperation();

			if (operation == null) {
				throw new SmesXException("Null operation not allowed in the response");
			}

			if (operation instanceof SmesXSMSReceive) {
				receiveSMS = (SmesXSMSReceive) operation;
				if (receiveSMS.containSMS()) {

					SmesXSMS sms = receiveSMS.getSMS();
					Message message = smsToMessage(sms);

					messages.add(message);

					if (receiveSMS.hasMore()) {
						receiveSMS.setAfterID(sms.getId());
					}
				}
			} else {
				throw new RuntimeException(
					"Resposne operation '" + operation.getClass().getSimpleName() + "' " +
					"is not allowed for the '" + receiveSMS.getClass().getSimpleName() +
					"' request");
			}
		} while (receiveSMS.hasMore());

		return messages;
	}

	/**
	 * Obtain message for particular message code.
	 * 
	 * @param code - code to search
	 * @return Message for given code or null if message has not been found
	 * @throws SmesXException
	 */
	public Message receiveSMSForCode(String code) throws SmesXException {

		if (code == null) {
			throw new IllegalArgumentException("Message code to receive cannot be null");
		}

		SmesXSMSReceive receiveSMS = null;

		do {

			receiveSMS = new SmesXSMSReceive();
			receiveSMS.setMarkAsRead(false);
			receiveSMS.setType(UNREAD);

			SmesXResponse response = execute(receiveSMS);
			SmesXOperation operation = response.getOperation();

			if (operation instanceof SmesXSMSReceive) {
				receiveSMS = (SmesXSMSReceive) operation;
				if (receiveSMS.containSMS()) {

					SmesXSMS sms = receiveSMS.getSMS();
					Message message = smsToMessage(sms);

					if (code.equals(message.getCode())) {
						return message;
					}

					if (receiveSMS.hasMore()) {
						receiveSMS.setAfterID(sms.getId());
					}
				}
			}
		} while (receiveSMS.hasMore());

		return null;
	}

	/**
	 * Convert {@link SmesXSMS} to the {@link Message} object.
	 * 
	 * @param sms - SMS to convert
	 * @return New message converted from SMS
	 */
	private Message smsToMessage(SmesXSMS sms) {

		String body = sms.getBody();
		String code = null;

		Pattern pat = Pattern.compile("^\\d+:");
		Matcher matcher = pat.matcher(body);
		if (matcher.find()) {
			code = matcher.group();
		}

		if (code != null) {
			body = body.substring(code.length()).trim();
			code = code.substring(0, code.length() - 1);
		}

		Message message = new Message();
		message.setBody(body);
		message.setCode(code);
		message.setSender(sms.getMSISDN());

		return message;
	}

	public static void main(String[] args) {

		Configuration cfg = Configuration.getInstance();

		String usr = cfg.getProperty("smesx", "user");
		String pwd = cfg.getProperty("smesx", "password");

		SmesXProvider p = new SmesXProvider(usr, pwd);

		try {
			// p.sendSMS(new Message("+48509934614", "Test A", "123456"));

			// List<Message> messages = p.receiveUnreadSMSs();
			// for (Message m : messages) {
			// System.out.println(m);
			// }

			Message m = p.receiveSMSForCode("123456");
			System.out.println(m);

		} catch (SmesXException e) {
			e.printStackTrace();
		}
	}
}
