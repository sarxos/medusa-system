package com.sarxos.gpwnotifier.comm.smeskom.v22;

import com.thoughtworks.xstream.annotations.XStreamAlias;


public enum ExecutionStatus {

	@XStreamAlias("success")
	SUCCESS,
	
	@XStreamAlias("failed")
	FAILED;
}
