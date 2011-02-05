package com.sarxos.medusa.comm.smeskom.v22;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "sms_type")
public enum SmesXSMSType {

	@XmlEnumValue("n")
	NORMAL,
	
	@XmlEnumValue("f")
	FLASH;
}
