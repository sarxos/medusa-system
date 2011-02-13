package com.sarxos.smeskom.v22;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * Used to check SMS status.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "sms_status")
public class SmesXSMSGetStatus extends SmesXOperation {

	/**
	 * ID of the SMS to check.
	 */
	@XmlElement(name = "id")
	private List<String> ids = null;
	
	/**
	 * SMS list with statuses.
	 */
	@XmlElement(name = "sms")
	private List<SmesXSMS> smses = null;

	
	/**
	 * Constructor.
	 */
	public SmesXSMSGetStatus() {
	}
	
	/**
	 * Add SMS ID to check status of. 
	 * @param id - SMS ID
	 */
	public void addID(String id) {
		if (ids == null) {
			ids = new LinkedList<String>();
		}
		ids.add(id);
	}
	
	/**
	 * @return Return SMS IDs to check status of.
	 */
	@XmlTransient
	public List<String> getIDs() {
		return ids;
	}
	
	/**
	 * Used to add SMS status to the list.
	 * 
	 * @param sms - SMS status descriptor
	 */
	public void addSMS(SmesXSMS sms) {
		if (smses == null) {
			smses = new LinkedList<SmesXSMS>();
		}
		smses.add(sms);
	}
	
	/**
	 * @return Return all SMS statuses
	 */
	@XmlTransient
	public List<SmesXSMS> getSMSs() {
		return smses;
	}
}
