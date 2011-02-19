package com.sarxos.medusa.comm;

import java.util.Date;


/**
 * Simple message class.
 * 
 * TODO implement ActiveMQ (JMS)
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Message {

	/**
	 * Sender address.
	 */
	private String sender = null;

	/**
	 * Recipient address.
	 */
	private String recipient = null;

	/**
	 * Body.
	 */
	private String body = null;

	/**
	 * Message code.
	 */
	private String code = null;

	/**
	 * Sent at date.
	 */
	private Date sentAt = null;

	/**
	 * Received at date.
	 */
	private Date receivedAt = null;

	/**
	 * Create new message.
	 */
	public Message() {
	}

	/**
	 * @return Recipient address
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * Set new recipient address
	 * 
	 * @param recipient - new address to set
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @return Message body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Set message body
	 * 
	 * @param body - new body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return Message code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Set message code
	 * 
	 * @param code - new code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return Sender address
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * Set sender address.
	 * 
	 * @param sender - new address to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append("---- ").append(getClass().getSimpleName()).append(" ----").append("\n");
		sb.append("code:   ").append(getCode()).append("\n");
		sb.append("body:   ").append(getBody()).append("\n");

		if (getSender() != null) {
			sb.append("from:   ").append(getSender()).append("\n");
		}
		if (getRecipient() != null) {
			sb.append("to:     ").append(getRecipient()).append("\n");
		}

		return sb.toString();
	}

	/**
	 * @return Sent at date
	 */
	public Date getSentAt() {
		return sentAt;
	}

	/**
	 * Set sent at date
	 * 
	 * @param sentAt - date to set
	 */
	public void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}

	/**
	 * @return Received at date
	 */
	public Date getReceivedAt() {
		return receivedAt;
	}

	/**
	 * Set received at date.
	 * 
	 * @param receivedAt - date to set
	 */
	public void setReceivedAt(Date receivedAt) {
		this.receivedAt = receivedAt;
	}
}
