package com.sarxos.medusa.task;

import static com.sarxos.smesx.v22.SmesXSMSReceiveType.UNREAD;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.trader.PlannedTask;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.smesx.SmesXException;
import com.sarxos.smesx.SmesXProvider;
import com.sarxos.smesx.v22.SmesXExecutionStatus;
import com.sarxos.smesx.v22.SmesXOperation;
import com.sarxos.smesx.v22.SmesXResponse;
import com.sarxos.smesx.v22.SmesXSMS;
import com.sarxos.smesx.v22.SmesXSMSMarkRead;
import com.sarxos.smesx.v22.SmesXSMSReceive;


/**
 * Reconcile missing quotes data.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ClearSmesXMessagesTask extends PlannedTask {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ClearSmesXMessagesTask.class.getSimpleName());

	/**
	 * Configuration instance
	 */
	private static final Configuration CFG = Configuration.getInstance();

	private SmesXProvider provider = null;

	public ClearSmesXMessagesTask() {

		// create SmesX provider
		String usr = CFG.getProperty("smesx", "user");
		String pwd = CFG.getProperty("smesx", "password");
		provider = new SmesXProvider(usr, pwd);

		// set execution time
		Date now = new Date();
		Date execution = null;

		GregorianCalendar calendar = new GregorianCalendar();

		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 4);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		execution = calendar.getTime();
		if (execution.getTime() < now.getTime()) {
			calendar.add(Calendar.DATE, +1);
		}

		execution = calendar.getTime();

		setExecutionTime(execution);
		setExecutionPeriod(PlannedTask.PERIOD_DAY);
	}

	@Override
	public void run() {

		LOG.info("Performing SmesX messages purification");

		SmesXSMSReceive receive = new SmesXSMSReceive();
		SmesXResponse response = null;
		SmesXOperation operation = null;

		do {

			receive.setMarkAsRead(false);
			receive.setType(UNREAD);

			try {
				response = provider.execute(receive);
			} catch (SmesXException e) {
				LOG.error(e.getMessage(), e);
			}

			if (response == null) {
				throw new RuntimeException("SmesX enpoint response is null!");
			}

			operation = response.getOperation();

			if (operation instanceof SmesXSMSReceive) {

				receive = (SmesXSMSReceive) operation;

				if (receive.containSMS()) {

					SmesXSMS sms = receive.getSMS();

					if (receive.hasMore()) {
						receive.setSMS(null);
						receive.setAfterID(sms.getID());
					}

					try {
						if (!markSMSAsRead(sms)) {
							LOG.warn("Cannot mark SMS '" + sms.getID() + "' as read");
						}
					} catch (SmesXException e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}

		} while (receive.hasMore());
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

	public static void main(String[] args) {
		new ClearSmesXMessagesTask().run();
	}
}
