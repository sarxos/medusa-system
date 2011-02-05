package com.sarxos.medusa.comm;


public class Message {

	public String recipient = null;
	
	public String message = null;
	
	public String code = null;
	
	public Message() {
	}
	
	public Message(String recipient, String body, String code) {
		this.recipient = recipient;
		this.message = code + ": " + body;
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
