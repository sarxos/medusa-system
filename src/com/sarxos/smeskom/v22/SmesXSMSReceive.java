package com.sarxos.smeskom.v22;

import static com.sarxos.smeskom.v22.SmesXEntity.DATE_FORMAT;
import static com.sarxos.smeskom.v22.SmesXSMSReceiveType.TIME;
import static com.sarxos.smeskom.v22.SmesXSMSReceiveType.UNREAD;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sarxos.smeskom.Validable;
import com.sarxos.smeskom.ValidationContext;


/**
 * Used to check receive SMS list.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "receive_sms")
public class SmesXSMSReceive extends SmesXOperation implements Validable {

	/**
	 * Receive type - can be time or simply unread.
	 */
	@XmlElement(name = "type", required = true)
	private SmesXSMSReceiveType type = UNREAD;

	/**
	 * Start time (String only).
	 */
	@XmlElement(name = "start_time")
	private String startTimeString = null;

	/**
	 * Start time (Date).
	 */
	private transient Date startTimeDate = null;

	/**
	 * Stop time (String only).
	 */
	@XmlElement(name = "stop_time")
	private String stopTimeString = null;

	/**
	 * Stop time (Date).
	 */
	private transient Date stopTimeDate = null;

	/**
	 * Receive list of SMS messages after this ID.
	 */
	@XmlElement(name = "after_id")
	private String afterID = null;

	/**
	 * If we set this value to true, SMS will be marked as read after receiving
	 * it. In other case when we set to false, SMS will be still unread after
	 * receive.
	 */
	@XmlElement(name = "mark")
	private Boolean mark = Boolean.valueOf(true);

	/**
	 * True if response contains at least one received SMS message.
	 */
	@XmlElement(name = "contain_sms")
	private Boolean containSMS = null;

	/**
	 * Set to true means that there are more SMS messages available for the same
	 * request.
	 */
	@XmlElement(name = "has_more")
	private Boolean hasMore = null;

	/**
	 * Received SMS messages list.
	 */
	@XmlElement(name = "sms")
	private SmesXSMS sms = null;

	/**
	 * Constructor.
	 */
	public SmesXSMSReceive() {
	}

	/**
	 * Used to add SMS status to the list.
	 * 
	 * @param sms - SMS status descriptor
	 */
	public void setSMS(SmesXSMS sms) {
		this.sms = sms;
	}

	/**
	 * @return Return all SMS statuses
	 */
	@XmlTransient
	public SmesXSMS getSMS() {
		return sms;
	}

	@Override
	public boolean validate(ValidationContext ctx) {

		boolean ok = true;

		if (type == TIME) {
			if (startTimeString == null) {
				ctx.addMessage(
					"Start time have to be specified when using '" + TIME +
					"' receive SMS type. Currently used type is '" + type + "'.");
				ok = false;
			}
		} else {
			if (startTimeString != null) {
				ctx.addMessage(
					"Start time has sense only when using '" + TIME +
					"' receive SMS type. Currently used type is '" + type + "'.");
				ok = false;
			}
			if (stopTimeString != null) {
				ctx.addMessage(
					"Stop time has sense only when using '" + TIME +
					"' receive SMS type. Currently used type is '" + type + "'.");
				ok = false;
			}
		}

		return ok;
	}

	/**
	 * @return Receive SMS type (unread, time)
	 */
	@XmlTransient
	public SmesXSMSReceiveType getType() {
		return type;
	}

	public void setType(SmesXSMSReceiveType type) {
		this.type = type;
	}

	@XmlTransient
	public Date getStartTime() {
		if (startTimeDate == null && startTimeString != null) {
			try {
				startTimeDate = DATE_FORMAT.parse(startTimeString);
			} catch (ParseException e) {
				String msg = "Cannot parse start time date '" + startTimeString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return startTimeDate;
	}

	public void setStartTime(Date start) {
		this.startTimeDate = start;
		this.startTimeString = DATE_FORMAT.format(start);
	}

	@XmlTransient
	public Date getStopTime() {
		if (stopTimeDate == null && stopTimeString != null) {
			try {
				stopTimeDate = DATE_FORMAT.parse(stopTimeString);
			} catch (ParseException e) {
				String msg = "Cannot parse stop time date '" + stopTimeString + "'";
				throw new RuntimeException(msg, e);
			}
		}
		return stopTimeDate;
	}

	public void setStopTime(Date stop) {
		this.stopTimeDate = stop;
		this.stopTimeString = DATE_FORMAT.format(stop);
	}

	@XmlTransient
	public String getAfterID() {
		return afterID;
	}

	public void setAfterID(String afterID) {
		this.afterID = afterID;
	}

	@XmlTransient
	public boolean isMarkAsRead() {
		return mark != null ? mark.booleanValue() : true;
	}

	public void setMarkAsRead(boolean mark) {
		this.mark = Boolean.valueOf(mark);
	}

	public boolean containSMS() {
		return containSMS != null ? containSMS.booleanValue() : false;
	}

	public Boolean hasMore() {
		return hasMore;
	}
}
