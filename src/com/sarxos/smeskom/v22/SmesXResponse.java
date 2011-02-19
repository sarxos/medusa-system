package com.sarxos.smeskom.v22;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "response")
public class SmesXResponse extends SmesXEntity {

	/**
	 * Execution status.
	 */
	@XmlElement(name = "execution_status")
	private SmesXExecutionStatus executionStatus = null;

	/**
	 * Fail code number.
	 */
	@XmlElement(name = "fail_code")
	private Integer failCode = null;

	/**
	 * Fail detailed description.
	 */
	@XmlElement(name = "fail_description")
	private String failDescription = null;

	/**
	 * Various SmesX operations.
	 */
	@XmlElementRef
	private SmesXOperation operation = null;

	public SmesXResponse() {
	}

	/**
	 * @return SmesX operation.
	 */
	@XmlTransient
	public SmesXOperation getOperation() {
		return operation;
	}

	@XmlTransient
	public SmesXExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	@XmlTransient
	public int getFailCode() {
		if (failCode != null) {
			return failCode.intValue();
		}
		return -1;
	}

	public void setFailCode(int code) {
		this.failCode = Integer.valueOf(code);
	}

	@XmlTransient
	public String getFailDescription() {
		return failDescription;
	}

	public void setFailDescription(String failDescription) {
		this.failDescription = failDescription;
	}
}
