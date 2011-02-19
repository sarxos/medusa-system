package com.sarxos.smeskom;

import java.util.LinkedList;
import java.util.List;


/**
 * This context gather info connected with the validation process.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ValidationContext {

	/**
	 * All validation messages.
	 */
	private List<String> messages = new LinkedList<String>();

	/**
	 * @return Validation messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * Add validation message.
	 * 
	 * @param message - message to add
	 */
	public void addMessage(String message) {
		messages.add(message);
	}
}
