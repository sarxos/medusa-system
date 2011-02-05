package com.sarxos.gpwnotifier.examples;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessageBroker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.comm.orange.OrangeSMSBroker;


public class OrangeMBoxSendSMSExample {

	public static void main(String[] args) {
		
		MessageBroker broker = new OrangeSMSBroker("mbox.tn", "mbox.password");
		Message message = new Message("user.tn", "This is message", "1");
		try {
			broker.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}	
}
