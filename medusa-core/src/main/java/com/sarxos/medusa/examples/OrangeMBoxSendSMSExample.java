package com.sarxos.medusa.examples;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessagesDriver;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.comm.driver.OrangeDriver;


public class OrangeMBoxSendSMSExample {

	public static void main(String[] args) {

		MessagesDriver broker = new OrangeDriver("mbox.TN", "mbox.password");
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
