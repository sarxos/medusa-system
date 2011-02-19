package com.sarxos.smeskom.v22;

import static com.sarxos.smeskom.v22.SmesXEntity.DATE_FORMAT;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * SMS status descriptor.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "sms")
public class SmesXSMS {

	/**
	 * SMS ID.
	 */
	@XmlElement
	private String id = null;

	/**
	 * SMS status.
	 */
	@XmlElement
	private SmesXSMSStatus status = null;

	/**
	 * SMS sent data ({@link Date} object).
	 */
	private transient Date sentAtDate = null;

	/**
	 * SMS sent data ({@link String} object).
	 */
	@XmlElement(name = "sent_at")
	private String sentAtString = null;

	/**
	 * SMS delivery data ({@link Date} object).
	 */
	private transient Date deliveredAtDate = null;

	/**
	 * SMS delivery data ({@link String} object).
	 */
	@XmlElement(name = "delivered_at")
	private String deliveredAtString = null;

	/**
	 * SMS received by GSM modem date (Date).
	 */
	private transient Date receivedAtDate = null;

	/**
	 * SMS received by GSM modem date (String).
	 */
	@XmlElement(name = "received_at")
	private String receivedAtString = null;

	/**
	 * When SMS has been inserted to the messages pool (Date).
	 */
	private transient Date insertedAtDate = null;

	/**
	 * When SMS has been inserted to the messages pool (String).
	 */
	@XmlElement(name = "inserted_at")
	private String insertedAtString = null;

	/**
	 * When SMS has been marked as read (Date).
	 */
	private transient Date markedAtDate = null;

	/**
	 * When SMS has been marked as read (String).
	 */
	@XmlElement(name = "marked_at")
	private String markedAtString = null;

	/**
	 * Fail code number.
	 */
	@XmlElement(name = "fail_code")
	private Integer failCode = null;

	/**
	 * Sender MSISDN number.
	 */
	@XmlElement
	private String msisdn = null;

	/**
	 * Message body.
	 */
	@XmlElement
	private String body = null;

	/**
	 * Constructor.
	 */
	public SmesXSMS() {
	}

	/**
	 * @return Return SMS ID.
	 */
	@XmlTransient
	public String getId() {
		return id;
	}

	/**
	 * Set SMS ID.
	 * 
	 * @param id - ID to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Return SMS status.
	 */
	@XmlTransient
	public SmesXSMSStatus getStatus() {
		return status;
	}

	/**
	 * Set SMS status.
	 * 
	 * @param status - SMS status to set.
	 */
	public void setStatus(SmesXSMSStatus status) {
		this.status = status;
	}

	/**
	 * @return Return date when SMS has been sent.
	 */
	@XmlTransient
	public Date getSentAtDate() {
		if (sentAtDate == null && sentAtString != null) {
			try {
				sentAtDate = DATE_FORMAT.parse(sentAtString);
			} catch (ParseException e) {
				String msg = "Cannot parse sent at date '" + sentAtString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return sentAtDate;
	}

	/**
	 * Set the date when SMS has been sent.
	 * 
	 * @param sentAtDate - date to set
	 */
	public void setSentAtDate(Date sentAtDate) {
		this.sentAtDate = sentAtDate;
		this.sentAtString = DATE_FORMAT.format(sentAtDate);
	}

	/**
	 * @return Return the date when SMS has been delivered.
	 */
	@XmlTransient
	public Date getDeliveredAtDate() {
		if (deliveredAtDate == null && deliveredAtString != null) {
			try {
				deliveredAtDate = DATE_FORMAT.parse(deliveredAtString);
			} catch (ParseException e) {
				String msg = "Cannot parse delivered at date '" + deliveredAtString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return deliveredAtDate;
	}

	/**
	 * Set the date when SMS has been delivered.
	 * 
	 * @param deliveredAtDate - date to set
	 */
	public void setDeliveredAtDate(Date deliveredAtDate) {
		this.deliveredAtDate = deliveredAtDate;
		this.deliveredAtString = DATE_FORMAT.format(deliveredAtDate);
	}

	/**
	 * @return Return the date when SMS has been received by GSM modem.
	 */
	@XmlTransient
	public Date getReceivedAtDate() {
		if (receivedAtDate == null && receivedAtString != null) {
			try {
				receivedAtDate = DATE_FORMAT.parse(receivedAtString);
			} catch (ParseException e) {
				String msg = "Cannot parse received at date '" + receivedAtString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return receivedAtDate;
	}

	/**
	 * Set the date when SMS has been received by GSM modem.
	 * 
	 * @param deliveredAtDate - date to set
	 */
	public void setReceivedAtDate(Date receivedAtDate) {
		this.receivedAtDate = receivedAtDate;
		this.receivedAtString = DATE_FORMAT.format(receivedAtDate);
	}

	/**
	 * @return Return the date when SMS has been inserted to the SMS pool.
	 */
	@XmlTransient
	public Date getInsertedAtDate() {
		if (insertedAtDate == null && insertedAtString != null) {
			try {
				insertedAtDate = DATE_FORMAT.parse(insertedAtString);
			} catch (ParseException e) {
				String msg = "Cannot parse inserted at date '" + insertedAtString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return insertedAtDate;
	}

	/**
	 * Set the date when SMS has been received by GSM modem.
	 * 
	 * @param deliveredAtDate - date to set
	 */
	public void setInsertedAtDate(Date insertedAtDate) {
		this.insertedAtDate = insertedAtDate;
		this.insertedAtString = DATE_FORMAT.format(insertedAtDate);
	}

	/**
	 * @return Return the date when SMS has been marked as read.
	 */
	@XmlTransient
	public Date getMarkedAtDate() {
		if (markedAtDate == null && markedAtString != null) {
			try {
				markedAtDate = DATE_FORMAT.parse(markedAtString);
			} catch (ParseException e) {
				String msg = "Cannot parse marked at date '" + markedAtString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return markedAtDate;
	}

	/**
	 * Set the date when SMS has been marked as read.
	 * 
	 * @param deliveredAtDate - date to set
	 */
	public void setMarkedAtDate(Date markedAtDate) {
		this.markedAtDate = markedAtDate;
		this.markedAtString = DATE_FORMAT.format(markedAtDate);
	}

	/**
	 * @return Return failure code.
	 */
	@XmlTransient
	public Integer getFailCode() {
		return failCode;
	}

	/**
	 * Set failure code.
	 * 
	 * @param failCode - fail code to set
	 */
	public void setFailCode(Integer failCode) {
		this.failCode = failCode;
	}

	/**
	 * @return Sender MSISDN number.
	 */
	@XmlTransient
	public String getMSISDN() {
		return msisdn;
	}

	@XmlTransient
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
