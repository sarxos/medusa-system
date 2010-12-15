package com.sarxos.gpwnotifier.comm;


public class Message {

	public String recipient = null;
	
	public String message = null;
	
	public Message() {
	}
	
	public Message(String recipient, String message) {
		this.recipient = recipient;
		this.message = message;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
