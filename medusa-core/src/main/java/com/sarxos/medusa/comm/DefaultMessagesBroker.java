package com.sarxos.medusa.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.util.CodeGenerator;
import com.sarxos.medusa.util.Configuration;


/**
 * Messages broker class. It utilizes {@link MessagesDriver} created in the
 * runtime by reflection, and use it to send and/or receive messages. This class
 * uses {@link MessagingPolicy} object to decide if message for given symbol can
 * be, or cannot be send.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see MessagesDriver#send(Message)
 * @see MessagingPolicy#allows(Symbol)
 */
public class DefaultMessagesBroker implements MessagesBroker {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessagesBroker.class.getSimpleName());

	/**
	 * Messages driver.
	 */
	private MessagesDriver driver = null;

	/**
	 * Medusa configuration instance.
	 */
	private static final Configuration CFG = Configuration.getInstance();

	/**
	 * Code generator for acknowledge messages.
	 */
	private CodeGenerator codegen = CodeGenerator.getInstance();

	/**
	 * Interval to check messages in seconds.
	 */
	private long[] intervals = new long[] { 30, 60, 120, 240, 480, 960 }; // seconds

	/**
	 * Create message broker.
	 * 
	 * @throws MessagingException when initialization problem occur
	 */
	public DefaultMessagesBroker() throws MessagingException {
		try {
			init();
		} catch (Exception e) {
			throw new MessagingException(e);
		}
	}

	/**
	 * Initialize
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		String name = CFG.getProperty("messaging", "driver");
		if (name == null) {
			throw new RuntimeException("Messaging driver class name cannot be null");
		}

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> clazz = loader.loadClass(name);
		driver = (MessagesDriver) clazz.newInstance();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Messages broker initialized. Used driver is " + name);
		}
	}

	/**
	 * @return Messages driver.
	 */
	public MessagesDriver getDriver() {
		return driver;
	}

	/**
	 * Set new messages driver.
	 * 
	 * @param driver - new driver to set
	 */
	public void setDriver(MessagesDriver driver) {
		this.driver = driver;
	}

	@Override
	public boolean acknowledge(Paper paper, SignalType type) throws MessagingException {

		if (paper == null) {
			throw new IllegalArgumentException("Paper cannot be null!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Signal type cannot be null!");
		}

		MessagingPolicy policy = MessagingPolicy.getPolicy();
		Symbol symbol = paper.getSymbol();

		// check if message for given symbol can be send
		if (!policy.allows(symbol)) {

			if (LOG.isInfoEnabled()) {
				LOG.info(
					"Messaging policy " + policy.getClass().getSimpleName() + " " +
					"does not allow sending acknowledge message to the player");
			}

			return false;
		}

		String recipient = CFG.getProperty("player", "mobile");
		String body = buildMessageString(paper, type);
		String code = codegen.generate();

		// create message object
		Message message = new Message();
		message.setCode(code);
		message.setBody(body);
		message.setRecipient(recipient);

		if (LOG.isInfoEnabled()) {
			LOG.info("Sending message to the player " + message);
		}

		// send message via messages driver
		boolean sent = false;
		try {
			sent = driver.send(message);
			policy.sent(symbol); // tell policy object that we have sent message
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		if (!sent) {
			throw new MessagingException("Message cannot be sent");
		}

		// loop until message response is being received
		int i = 0;
		do {

			long sleep = intervals[i]; // seconds
			if (i < intervals.length - 1) {
				i++;
			}
			try {
				Thread.sleep(sleep * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Checking response for the code " + code);
			}

			// receive message for given code - will return null if no message
			// is available
			message = driver.receive(code);

		} while (message == null);

		// return player's decision
		return getDecision(message);
	}

	/**
	 * Extract player decision from message body.
	 * 
	 * @param m - message to extract decision from
	 * @return true in case when user has sent "ok" response, false otherwise
	 */
	protected boolean getDecision(Message m) {
		if (m.getBody() == null) {
			throw new IllegalArgumentException("Cannot read decision from null body");
		}
		return "ok".equalsIgnoreCase(m.getBody().trim());
	}

	/**
	 * Build message which will be send to the player.
	 * 
	 * @param paper - paper to buy/sell
	 * @param type - buy/sell signal
	 * @return Message body
	 */
	protected String buildMessageString(Paper paper, SignalType type) {

		StringBuffer sb = new StringBuffer();
		sb.append(type);
		sb.append(" ");
		sb.append(paper.getSymbol().getName());

		return sb.toString();
	}

	/**
	 * @return Check interval in seconds (30s by default)
	 */
	public long[] getCheckIntervals() {
		return intervals;
	}

	/**
	 * Set check interval (seconds).
	 * 
	 * @param intervals - new interval to set in seconds
	 */
	public void setCheckIntervals(long[] intervals) {
		this.intervals = intervals;
	}

}
