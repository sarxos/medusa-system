package com.sarxos.gpwnotifier.comm.smeskom.v22;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXEntity.DATE_FORMAT; 


/**
 * Used to send SMS.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "send_sms")
public class SmesXSMSSend extends SmesXOperation {

	@XmlElement(name = "msisdn")
	private String msisdn = null;
	
	@XmlElement(name = "body")
	private String body = null;
	
	@XmlElement(name = "expire_at")
	private String expireString = null;
	
	private transient Date expireDate = null;
	
	@XmlElement(name = "sender")
	private String sender = null;

	@XmlElement(name = "sms_type")
	private SmesXSMSType type = null;
	
	@XmlElement(name = "send_after")
	private String sendAfterString = null;

	private transient Date sendAfterDate = null;
	
	
	public SmesXSMSSend() {
	}
	
	@XmlTransient
	public String getMsisdn() {
		return msisdn;
	}

	public void setMSISDN(String msisdn) {
		this.msisdn = msisdn;
	}

	@XmlTransient
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@XmlTransient
	public Date getExpireDate() {
		if (expireDate == null && expireString != null) {
			try {
				expireDate = DATE_FORMAT.parse(expireString);
			} catch (ParseException e) {
				String msg = "Cannot parse expiration date '" + expireString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return expireDate;
	}

	public void setExpireDate(Date date) {
		this.expireDate = date;
		this.expireString = DATE_FORMAT.format(date);
	}

	@XmlTransient
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	@XmlTransient
	public SmesXSMSType getSMSType() {
		return type;
	}

	public void setSMSType(SmesXSMSType type) {
		this.type = type;
	}

	@XmlTransient
	public Date getSendAfterDate() {
		if (sendAfterDate == null && sendAfterString != null) {
			try {
				sendAfterDate = DATE_FORMAT.parse(sendAfterString);
			} catch (ParseException e) {
				String msg = "Cannot parse after send after date '" + sendAfterString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return sendAfterDate;
	}

	public void setSendAfterDate(Date date) {
		this.sendAfterDate = date;
		this.sendAfterString = DATE_FORMAT.format(date);
	}
}
