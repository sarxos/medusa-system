package com.sarxos.gpwnotifier.comm.smeskom.v22;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Used to check SMS status.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "sms_status")
public class SmesXSMSGetStatus extends SmesXOperation {

	@XmlList
	@XmlElement(name = "id")
	private List<String> ids = null;
	
	public void addID(String id) {
		if (ids == null) {
			ids = new LinkedList<String>();
		}
		ids.add(id);
	}
}
