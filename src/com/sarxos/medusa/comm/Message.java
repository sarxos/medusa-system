package com.sarxos.medusa.comm;

public class Message {

	private String recipient = null;

	private String message = null;

	private String code = null;

	public Message() {
	}

	public Message(String recipient, String body, String code) {
		this.recipient = recipient;
		this.message = code + ": " + body;
		this.code = code;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
