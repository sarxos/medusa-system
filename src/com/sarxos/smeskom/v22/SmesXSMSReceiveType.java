package com.sarxos.smeskom.v22;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * SMS receive SMS type.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "type")
public enum SmesXSMSReceiveType {

	/**
	 * Receive SMS messages by time.
	 */
	@XmlEnumValue("time")
	TIME,

	/**
	 * Receive only unread SMS messages.
	 */
	@XmlEnumValue("unread")
	UNREAD;
}
