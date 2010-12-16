package com.sarxos.gpwnotifier.comm.smeskom.v22;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "response")
public class SmesXResponse extends SmesXEntity {

	@XmlElement(name = "execution_status")
	private SmesXExecutionStatus executionStatus = null; 
	
	@XmlElement(name = "fail_code")
	private int failCode = -1;
	
	@XmlElement(name = "fail_description")
	private String failDescription = null;

	
	public SmesXResponse() {
	}
	
	public SmesXExecutionStatus getExecutionStatus() {
		return executionStatus;
	}
	
	public void setExecutionStatus(SmesXExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	public int getFailCode() {
		return failCode;
	}
	
	public void setFailCode(int code) {
		this.failCode = code;
	}
	
	public String getFailDescription() {
		return failDescription;
	}
	
	public void setFailDescription(String failDescription) {
		this.failDescription = failDescription;
	}
}
