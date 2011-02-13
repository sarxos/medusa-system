package com.sarxos.smeskom.v22;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static com.sarxos.smeskom.v22.SmesXEntity.DATE_FORMAT;


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
	@XmlElement(name = "id")
	private String id = null;
	
	/**
	 * SMS status.
	 */
	@XmlElement(name = "status")
	private SmesXSMSStatus status = null;
	
	/**
	 * SMS sent data ({@link Date} object).
	 */
	@XmlTransient
	private Date sentAtDate = null;

	/**
	 * SMS sent data ({@link String} object).
	 */
	@XmlElement(name = "sent_at")
	private String sentAtString = null;

	/**
	 * SMS delivery data ({@link Date} object).
	 */
	@XmlTransient
	private Date deliveredAtDate = null;

	/**
	 * SMS delivery data ({@link String} object).
	 */
	@XmlElement(name = "delivered_at")
	private String deliveredAtString = null;
	
	/**
	 * Fail code number.
	 */
	@XmlElement(name = "fail_code")
	private Integer failCode = null; 
	
	
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
}
