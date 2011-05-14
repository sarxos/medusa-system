package com.sarxos.medusa.comm.driver;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessagesDriver;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.http.MedusaHttpClient;
import com.sarxos.medusa.util.Configuration;


/**
 * Orange MBox message broker.
 * 
 * @see http://www.orange.pl/zaloguj.phtml
 * @author Bartosz Firyn (SarXos)
 */
public final class OrangeDriver implements MessagesDriver {

	/**
	 * User's TN.
	 */
	private String tn = null;

	/**
	 * User's MBox password.
	 */
	private String password = null;

	/**
	 * Create message broker. Get default credentials (TN and passwd) from
	 * medusa.ini file (configuration).
	 */
	public OrangeDriver() {
		Configuration cfg = Configuration.getInstance();

		String tn = cfg.getProperty("orange", "mobile");
		String pwd = cfg.getProperty("orange", "password");

		init(tn, pwd);
	}

	public OrangeDriver(String tn, String pwd) {
		this.init(tn, pwd);
	}

	private void init(String tn, String pwd) {
		this.tn = tn;
		this.password = pwd;
	}

	/**
	 * @return Return user's MBox TN
	 */
	public String getTn() {
		return tn;
	}

	/**
	 * Set user's MBox TN.
	 * 
	 * @param tn - user's MBox TN to set
	 */
	public void setTn(String tn) {
		this.tn = tn;
	}

	/**
	 * @return Return user's MBox password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set users's MBox password.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean send(Message message) throws MessagingException {

		MedusaHttpClient client = new MedusaHttpClient();

		HttpResponse response = null;
		HttpEntity entity = null;
		HttpGet get = null;
		HttpPost post = null;
		Header location = null;
		List<NameValuePair> nvps = null;

		String html = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		UrlEncodedFormEntity req_entity = null;

		try {
			get = new HttpGet("http://www.orange.pl/");
			get.setHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		try {
			get = new HttpGet("http://www.orange.pl/start.phtml");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		post = new HttpPost("https://www.orange.pl/zaloguj.phtml?_DARGS=/gear/static/signInLoginBox.jsp");
		post.setHeader("Host", "www.orange.pl");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
		post.setHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Referer", "https://www.orange.pl/zaloguj.phtml");
		post.setHeader("Origin", "https://www.orange.pl");
		post.getParams().setBooleanParameter("http.protocol.expect-continue", false);

		nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("_dyncharset", "UTF-8"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.loginErrorURL", "https://www.orange.pl/zaloguj.phtml"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.loginErrorURL", ""));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.login", tn));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.login", ""));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.password", password));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.password", ""));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.login.x", "0"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.login.y", "0"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.login", ""));
		nvps.add(new BasicNameValuePair("_DARGS", "/gear/static/signInLoginBox.jsp"));

		try {
			req_entity = new UrlEncodedFormEntity(nvps);
			post.setEntity(req_entity);
			response = client.execute(post);
			location = response.getHeaders("Location")[0];
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		try {
			get = new HttpGet(location.getValue());
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		try {
			get = new HttpGet("https://www.orange.pl/portal/map/map/message_box");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		String stamp = null;

		try {
			Pattern pat = Pattern.compile("\\d{13}");
			Matcher matcher = pat.matcher(html);
			if (matcher.find()) {
				stamp = matcher.group();
			}
		} catch (Exception e) {
			throw new MessagingException("Matching exception", e);
		}

		String new_message_url =
			"https://www.orange.pl/portal/map/map/message_box?mbox_edit=new&stamp=" +
			stamp + "&mbox_view=newsms";

		try {
			get = new HttpGet(new_message_url);
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		String token = null;

		try {
			Pattern pat = Pattern.compile("\"[a-zA-Z0-9]{15}\"");
			Matcher matcher = pat.matcher(html);
			if (matcher.find()) {
				token = matcher.group();
				token = token.substring(1, token.length() - 1);
			}
		} catch (Exception e) {
			throw new MessagingException("Token matching exception", e);
		}

		if (token == null) {
			throw new MessagingException("Canot find token");
		}

		post = new HttpPost("https://www.orange.pl/portal/map/map/message_box?_DARGS=/gear/mapmessagebox/smsform.jsp");
		post.setHeader("Host", "www.orange.pl");
		post.setHeader("Accept-Charset", "ISO-8859-2,utf-8;q=0.7,*;q=0.3");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
		post.setHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		post.setHeader("Referer", new_message_url);
		post.setHeader("Accept-Encoding", ":gzip,deflate,sdch");
		post.setHeader("Accept-Language", "pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4");
		post.setHeader("Cache-Control", "max-age=0");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Origin", "https://www.orange.pl");
		post.getParams().setBooleanParameter("http.protocol.expect-continue", false);

		String to = message.getRecipient();
		String msg = message.getBody();

		nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("_dyncharset", "UTF-8"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.type", "sms"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.type", " "));
		nvps.add(new BasicNameValuePair("enabled", "false"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.errorURL", "/portal/map/map/message_box?mbox_view=newsms"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.errorURL", " "));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.successURL", "/portal/map/map/message_box?mbox_view=messageslist"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.successURL", " "));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.to", to));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.to", " "));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.body", msg));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.body", " "));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.token", token));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.token", " "));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.create.x", "25"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.create.y", "13"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.create", "Wyœlij"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/messagebox/formhandlers/MessageFormHandler.create", " "));
		nvps.add(new BasicNameValuePair("_DARGS", "/gear/mapmessagebox/smsform.jsp"));

		try {
			req_entity = new UrlEncodedFormEntity(nvps);
			post.setEntity(req_entity);
			response = client.execute(post);
			location = response.getHeaders("Location")[0];
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		try {
			get = new HttpGet(location.getValue());
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			entity.getContent().close();
			html = new String(baos.toByteArray());
			baos.reset();
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		return true;
	}

	@Override
	public Message receive(String code) throws MessagingException {
		throw new MessagingException(
			getClass().getSimpleName() + " " +
			"does not support receiving messages");
	}
}
