package com.sarxos.medusa.sim;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessagesDriver;
import com.sarxos.medusa.comm.MessagingException;


public class FakeMessagesDriver implements MessagesDriver {

	@Override
	public boolean send(Message message) throws MessagingException {
		return true;
	}

	@Override
	public Message receive(String code) throws MessagingException {
		Message m = new Message();
		m.setBody("OK");
		return m;
	}

}
