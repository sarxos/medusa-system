package com.sarxos.smeskom;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessageBroker;
import com.sarxos.medusa.comm.MessagingException;


public class SmesXMessageBroker implements MessageBroker {

	@Override
	public boolean send(Message message) throws MessagingException {
		return false;
	}

}
