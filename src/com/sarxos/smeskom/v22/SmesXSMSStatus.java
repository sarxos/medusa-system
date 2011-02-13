package com.sarxos.smeskom.v22;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * SMS status.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "status")
public enum SmesXSMSStatus {

	/**
	 * SMS in the system - has neither been processed nor sent.
	 */
	@XmlEnumValue("new")
	NEW,

	/**
	 * SMS has been processed by the system and it is ready to send. 
	 */
	@XmlEnumValue("processed")
	PROCESSED,

	/**
	 * Message has been sent, but recipient didn't receive it yet.
	 */
	@XmlEnumValue("sent")
	SENT,

	/**
	 * SMS delivered to recipient.
	 */
	@XmlEnumValue("delivered")
	DELIVERED,
	
	/**
	 * Cannot deliver SMS - fail code returned in the fail_code.
	 */
	@XmlEnumValue("failed")
	FAILED;
}
