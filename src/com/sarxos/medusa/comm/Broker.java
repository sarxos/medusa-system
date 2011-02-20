package com.sarxos.medusa.comm;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.util.CodeGenerator;
import com.sarxos.medusa.util.Configuration;


/**
 * Messages broker class. It utilizes messages driver created in the runtime by
 * reflection, and use it to send and/or receive messages.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Broker {

	/**
	 * Messages driver.
	 */
	private Driver driver = null;

	/**
	 * Medusa configuration instance.
	 */
	private Configuration configuration = Configuration.getInstance();

	/**
	 * Code generator for acknowledge messages.
	 */
	private CodeGenerator codegen = CodeGenerator.getInstance();

	/**
	 * Interval to check messages.
	 */
	private long checkInterval = 30000;

	/**
	 * Create message broker.
	 * 
	 * @throws MessagingException when initialization problem occur
	 */
	public Broker() throws MessagingException {
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
		String name = configuration.getProperty("messaging", "driver");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> clazz = loader.loadClass(name);
		driver = (Driver) clazz.newInstance();
	}

	/**
	 * @return Messages driver.
	 */
	public Driver getDriver() {
		return driver;
	}

	/**
	 * Set new messages driver.
	 * 
	 * @param driver - new driver to set
	 */
	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	/**
	 * Acknowledge player about trader decision and receive his response. This
	 * method will return true if player has confirmed, or false if player
	 * refused to acknowledge.
	 * 
	 * @param paper - paper to buy/sell
	 * @param type - buy/sell action
	 * @return true if player has confirmed, or false if player refused to
	 * @throws MessagingException
	 */
	public boolean acknowledge(Paper paper, SignalType type) throws MessagingException {

		String recipient = configuration.getProperty("player", "mobile");
		String body = buildMessageString(paper, type);
		String code = codegen.generate();

		Message message = new Message();
		message.setCode(code);
		message.setBody(body);
		message.setRecipient(recipient);

		boolean sent = false;
		try {
			sent = driver.send(message);
		} catch (Exception e) {
			throw new MessagingException(e);
		}

		if (!sent) {
			throw new MessagingException("Message cannot be sent");
		}

		do {
			try {
				Thread.sleep(getCheckInterval());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			message = driver.receive(code);

		} while (message != null);

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

		int current = paper.getQuantity();
		int desired = paper.getDesiredQuantity();
		int quantity = type == SignalType.SELL ? current : desired;

		StringBuffer sb = new StringBuffer();
		sb.append(type);
		sb.append(" ");
		sb.append(paper.getSymbol().getName());
		sb.append(" ");
		sb.append(quantity);

		return sb.toString();
	}

	/**
	 * @return Check interval in seconds (30s by default)
	 */
	public long getCheckInterval() {
		return checkInterval / 1000;
	}

	/**
	 * Set check interval (seconds).
	 * 
	 * @param checkInterval - new interval to set in seconds
	 */
	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval * 1000;
	}

}
