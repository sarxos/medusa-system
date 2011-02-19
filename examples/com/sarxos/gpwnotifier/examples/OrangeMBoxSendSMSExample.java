package com.sarxos.gpwnotifier.examples;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessageBroker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.orangembox.OrangeSMSBroker;


public class OrangeMBoxSendSMSExample {

	public static void main(String[] args) {

		MessageBroker broker = new OrangeSMSBroker("mbox.TN", "mbox.password");
		Message message = new Message();
		message.setCode("1");
		message.setBody("This is message");
		message.setRecipient("user.TN");

		try {
			broker.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
