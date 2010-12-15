package com.sarxos.gpwnotifier.comm.smeskom.v22;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public enum SMSType {

	@XStreamAlias("n")
	NORMAL,

	@XStreamAlias("f")
	FLASH;
}
