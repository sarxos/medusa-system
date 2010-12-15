package com.sarxos.gpwnotifier.comm.smeskom.v22;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("response")
public class SmesXResponse {

	/**
	 * Protocol version.
	 */
	@XStreamAlias("version")
	@XStreamAsAttribute
	public static final String VERSION = "2.2";
	
	/**
	 * Protocol name.
	 */
	@XStreamAlias("protocol")
	@XStreamAsAttribute
	public static final String PROTOCOL = "SmesX";
	
	private ExecutionStatus executionStatus = null; 
	
	private Integer failCode = null;
	
	private String failDescription = null;

	
	public SmesXResponse() {
	}
	
	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}
	
	public void setExecutionStatus(ExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	public int getFailCode() {
		if (failCode == null) {
			return -1;
		} else {
			return failCode.intValue();
		}
	}
	
	public void setFailCode(int code) {
		this.failCode = Integer.valueOf(code);
	}
	
	public String getFailDescription() {
		return failDescription;
	}
	
	public void setFailDescription(String failDescription) {
		this.failDescription = failDescription;
	}
}
