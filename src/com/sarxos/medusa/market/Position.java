package com.sarxos.medusa.market;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "position")
public enum Position {
	
	@XmlEnumValue("long")
	LONG,
	
	@XmlEnumValue("short")
	SHORT;

}
