package com.sarxos.gpwnotifier.comm.smeskom.v22;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("send_sms")
public class SmesXSendSMS {

	@XStreamAlias("msisdn")
	private String msisdn = null;
	
	@XStreamAlias("body")
	private String body = null;
	
	@XStreamAlias("expire_at")
	private Date expire = null;
	
	@XStreamAlias("sender")
	private String sender = null;

	@XStreamAlias("sms_type")
	private String type = null;
	
	@XStreamAlias("send_after")
	private Date after = null;
}

