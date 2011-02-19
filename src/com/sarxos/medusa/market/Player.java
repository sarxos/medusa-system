package com.sarxos.medusa.market;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessageBroker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.util.CodeGenerator;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.smeskom.SmesXMessageBroker;


public class Player {

	private CodeGenerator codegen = CodeGenerator.getInstance();

	private Configuration configuration = Configuration.getInstance();

	private MessageBroker broker = new SmesXMessageBroker();

	/**
	 * Ask player (send SMS, email, Jabber, etc) to get permission to sell/buy
	 * paper.
	 * 
	 * @param paper
	 * @param type
	 * @return
	 */
	public boolean empower(Paper paper, SignalType type) {

		String recipient = configuration.getProperty("player", "mobile");
		String body = buildMessageString(paper, type);
		String code = codegen.generate();

		Message message = new Message();
		message.setCode(code);
		message.setBody(body);
		message.setRecipient(recipient);

		try {
			broker.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return false;
	}

	protected String buildMessageString(Paper paper, SignalType type) {

		int current = paper.getQuantity();
		int desired = paper.getDesiredQuantity();

		String symbol = paper.getSymbol().getName();
		int quantity = type == SignalType.SELL ? current : desired;

		StringBuffer sb = new StringBuffer();

		sb.append(type);
		sb.append(" ");
		sb.append(symbol);
		sb.append(" ");
		sb.append(quantity);

		return sb.toString();
	}
}
