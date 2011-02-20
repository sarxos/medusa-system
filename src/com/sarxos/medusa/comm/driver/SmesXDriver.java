package com.sarxos.medusa.comm.driver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sarxos.medusa.comm.Driver;
import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.smeskom.SmesXException;
import com.sarxos.smeskom.SmesXProvider;
import com.sarxos.smeskom.v22.SmesXSMS;


/**
 * SmesX driver class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class SmesXDriver implements Driver {

	/**
	 * SemsX messaging provider.
	 */
	private SmesXProvider provider = null;

	/**
	 * Configuration instance.
	 */
	private Configuration configuration = Configuration.getInstance();

	/**
	 * Construct driver. This constructor will be most probably called within
	 * reflection mechanism.
	 */
	public SmesXDriver() {
		String usr = configuration.getProperty("smesx", "user");
		String pwd = configuration.getProperty("smesx", "password");
		provider = new SmesXProvider(usr, pwd);
	}

	@Override
	public boolean send(Message message) throws MessagingException {

		boolean sent = false;
		try {
			sent = provider.sendSMS(message);
		} catch (SmesXException e) {
			throw new MessagingException(e);
		}

		return sent;
	}

	@Override
	public Message receive(String code) throws MessagingException {

		Message message = null;
		SmesXSMS sms = null;

		try {
			sms = provider.receiveRawSMSForCode(false, code);
		} catch (SmesXException e) {
			throw new MessagingException(e);
		}

		if (sms != null) {
			message = smsToMessage(sms);

			boolean marked = false;
			try {
				marked = provider.markRawSMSAsRead(sms);
			} catch (SmesXException e) {
				throw new MessagingException(e);
			}

			if (!marked) {
				throw new MessagingException("Unable to mark message as read");
			}
		}

		return message;
	}

	/**
	 * Convert {@link SmesXSMS} to the {@link Message} object.
	 * 
	 * @param sms - SMS to convert
	 * @return New message converted from SMS
	 */
	protected Message smsToMessage(SmesXSMS sms) {

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
}
