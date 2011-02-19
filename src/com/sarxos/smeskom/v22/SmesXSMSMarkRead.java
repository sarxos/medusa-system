package com.sarxos.smeskom.v22;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * Used to mark message as read.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "mark_read")
public class SmesXSMSMarkRead extends SmesXOperation {

	/**
	 * List of SMS message IDs to mark as read.
	 */
	@XmlElement(name = "id")
	private List<String> ids = null;

	/**
	 * List of SMS messages marked as read.
	 */
	@XmlElement(name = "sms")
	private List<SmesXSMS> smses = null;

	/**
	 * Constructor.
	 */
	public SmesXSMSMarkRead() {
	}

	/**
	 * Construct mark message operation. This constructor is usefull when
	 * marking particular SMS messages.
	 * 
	 * @param sms - SMS to mark
	 */
	public SmesXSMSMarkRead(SmesXSMS sms) {
		this();
		this.addSMSToMark(sms);
	}

	/**
	 * Add SMS ID to check status of.
	 * 
	 * @param id - SMS ID
	 */
	public void addIDToMark(String id) {
		if (ids == null) {
			ids = new LinkedList<String>();
		}
		ids.add(id);
	}

	/**
	 * @return Return SMS IDs to check status of.
	 */
	@XmlTransient
	public List<String> getIDsToMark() {
		if (ids == null) {
			return Collections.emptyList();
		}
		return ids;
	}

	/**
	 * Add SMS to mark as read.
	 * 
	 * @param sms - SMS status descriptor
	 */
	public void addSMSToMark(SmesXSMS sms) {
		if (sms == null) {
			throw new IllegalArgumentException("SMS to mak as read cannot be null");
		}

		String id = sms.getID();

		if (id == null) {
			throw new IllegalArgumentException("SMS with null ID cannot be marked as read");
		} else {
			addIDToMark(id);
		}
	}

	/**
	 * @return Return all SMS statuses
	 */
	@XmlTransient
	public List<SmesXSMS> getSMSs() {
		return smses;
	}
}
