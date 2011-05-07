package com.sarxos.medusa.comm.driver;

import static com.sarxos.smesx.v22.SmesXSMSReceiveType.UNREAD;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessagesDriver;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.smesx.SmesXException;
import com.sarxos.smesx.SmesXProvider;
import com.sarxos.smesx.v22.SmesXExecutionStatus;
import com.sarxos.smesx.v22.SmesXOperation;
import com.sarxos.smesx.v22.SmesXResponse;
import com.sarxos.smesx.v22.SmesXSMS;
import com.sarxos.smesx.v22.SmesXSMSMarkRead;
import com.sarxos.smesx.v22.SmesXSMSReceive;
import com.sarxos.smesx.v22.SmesXSMSSend;


/**
 * SmesX driver class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class SmesXDriver implements MessagesDriver {

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

		SmesXSMSSend sendSMS = convert(message);

		boolean sent = false;
		try {
			sent = sendRawSMS(sendSMS);
		} catch (SmesXException e) {
			throw new MessagingException(e);
		}

		return sent;
	}

	@Override
	public Message receive(String code) throws MessagingException {

		Message message = null;
		SmesXSMS sms = receiveSMSForCode(code);

		if (sms != null) {
			message = convert(sms);

			boolean marked = false;
			try {
				marked = markSMSAsRead(sms);
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
	 * Receive SMS for given code.
	 * 
	 * @param code - code to search.
	 * @return Return SMS
	 */
	protected SmesXSMS receiveSMSForCode(String code) {

		if (code == null) {
			throw new IllegalArgumentException("Message code to receive raw SMS cannot be null");
		}

		SmesXSMSReceive receiveSMS = new SmesXSMSReceive();

		do {

			receiveSMS.setMarkAsRead(false);
			receiveSMS.setType(UNREAD);

			SmesXResponse response = null;
			try {
				response = provider.execute(receiveSMS);
			} catch (SmesXException e) {
				e.printStackTrace();
			}

			if (response == null) {
				return null;
			}

			SmesXOperation operation = response.getOperation();

			if (operation instanceof SmesXSMSReceive) {
				receiveSMS = (SmesXSMSReceive) operation;
				if (receiveSMS.containSMS()) {

					SmesXSMS sms = receiveSMS.getSMS();
					Message message = convert(sms);

					if (code.equals(message.getCode())) {
						return sms;
					}

					if (receiveSMS.hasMore()) {
						receiveSMS.setSMS(null);
						receiveSMS.setAfterID(sms.getID());
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
	protected Message convert(SmesXSMS sms) {

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

	/**
	 * Convert {@link Message} to the {@link SmesXSMSSend} SmesX request.
	 * 
	 * @param msg - message to convert
	 * @return New 'send SMS' request object
	 */
	protected SmesXSMSSend convert(Message msg) {

		String msisdn = msg.getRecipient();
		String body = msg.getCode() + ": " + msg.getBody();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, +3); // 3 days expire date

		Date expire = calendar.getTime();

		SmesXSMSSend sendSMS = new SmesXSMSSend();
		sendSMS.setBody(body);
		sendSMS.setMSISDN(msisdn);
		sendSMS.setExpireDate(expire);

		return sendSMS;
	}

	/**
	 * Mark SMS as read.
	 * 
	 * @param sms - SMS to mark as read
	 * @return true in case of successful response, false otherwise
	 * @throws SmesXException
	 */
	protected boolean markSMSAsRead(SmesXSMS sms) throws SmesXException {
		if (sms == null) {
			throw new IllegalArgumentException("SMS to mark as read cannot be null");
		}
		SmesXResponse resposne = provider.execute(new SmesXSMSMarkRead(sms));
		return resposne.getExecutionStatus() == SmesXExecutionStatus.SUCCESS;
	}

	/**
	 * Send SMS
	 * 
	 * @param sendSMS - SMS message to send
	 * @return true if message has been sent, false otherwise
	 * @throws SmesXException
	 */
	protected boolean sendRawSMS(SmesXSMSSend sendSMS) throws SmesXException {
		SmesXResponse response = provider.execute(sendSMS);
		return response.getExecutionStatus() == SmesXExecutionStatus.SUCCESS;
	}
}

// public SmesXSMS receiveRawSMSForCode(boolean mark, String code, Date
// start) throws SmesXException {
//
// if (code == null) {
// throw new
// IllegalArgumentException("Message code to receive raw SMS cannot be null");
// }
//
// SmesXSMSReceive receiveSMS = new SmesXSMSReceive();
//
// do {
//
// receiveSMS.setMarkAsRead(mark);
//
// if (start == null) {
// receiveSMS.setType(UNREAD);
// } else {
// receiveSMS.setType(TIME);
// receiveSMS.setStartTime(start);
//
// // in v2.2 stop_time has to be specified, in other case SmesX
// // endpoint won't return messages - set end date to now, this
// // is a bug I guess. I've sent message to the SmesX developers.
// receiveSMS.setStopTime(new Date());
// }
//
// SmesXResponse response = execute(receiveSMS);
// SmesXOperation operation = response.getOperation();
//
// if (operation instanceof SmesXSMSReceive) {
// receiveSMS = (SmesXSMSReceive) operation;
// if (receiveSMS.containSMS()) {
//
// SmesXSMS sms = receiveSMS.getSMS();
// // TODO rework - get rid of Message here
// Message message = smsToMessage(sms);
//
// if (code.equals(message.getCode())) {
// return sms;
// }
//
// if (receiveSMS.hasMore()) {
// receiveSMS.setSMS(null);
// receiveSMS.setAfterID(sms.getID());
// }
// }
// }
// } while (receiveSMS.hasMore());
//
// return null;
// }
