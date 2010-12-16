package com.sarxos.gpwnotifier.comm.smeskom.v22;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "execution_status")
public enum SmesXExecutionStatus {

	@XmlEnumValue("success")
	SUCCESS,
	
	@XmlEnumValue("failed")
	FAILED;
}
