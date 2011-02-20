package com.sarxos.medusa.comm;

/**
 * Message broker interface. It encapsulates abstraction of message sending and
 * receiving.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface Driver {

	/**
	 * Send given message via the message broker.
	 * 
	 * @param message - message to send
	 * @return true if message has been sent
	 * @throws MessagingException when something is wrong
	 */
	public boolean send(Message message) throws MessagingException;

	/**
	 * Receive response for given code.
	 * 
	 * @param code - code to search within received messages.
	 * @return Message if found, null otherwise.
	 * @throws MessagingException in case of any problems
	 */
	public Message receive(String code) throws MessagingException;
}
