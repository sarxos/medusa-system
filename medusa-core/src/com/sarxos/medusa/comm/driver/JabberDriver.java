package com.sarxos.medusa.comm.driver;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.comm.Message;
import com.sarxos.medusa.comm.MessagesDriver;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.util.Configuration;


public class JabberDriver implements MessagesDriver, MessageListener {

	/**
	 * Configuration instance.
	 */
	private static final Configuration CFG = Configuration.getInstance();

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(JabberDriver.class.getSimpleName());

	private boolean initialized = false;
	private String jid = null;
	private String password = null;

	private XMPPConnection connection = null;

	public JabberDriver() {
		String jid = CFG.getProperty("xmpp", "jid");
		String pwd = CFG.getProperty("xmpp", "password");
		try {
			init(jid, pwd);
		} catch (XMPPException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public JabberDriver(String jid, String pwd) {
		try {
			init(jid, pwd);
		} catch (XMPPException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void init(String jid, String pwd) throws XMPPException {

		if (jid == null) {
			throw new IllegalArgumentException("Jabber ID cannot eb null!");
		}
		if (pwd == null) {
			throw new IllegalArgumentException("Account password cannot be null!");
		}

		this.jid = jid;
		this.password = pwd;

		String[] parts = jid.split("@");
		if (parts.length != 2) {
			throw new RuntimeException("Jabber ID '" + jid + "' is invalid");
		}

		// create configuration for new XMPP connection
		ConnectionConfiguration config = new ConnectionConfiguration(parts[1], 5222);
		config.setVerifyChainEnabled(false);
		config.setVerifyRootCAEnabled(false);
		config.setCompressionEnabled(false);
		config.setSASLAuthenticationEnabled(true);
		config.setDebuggerEnabled(false);

		SASLAuthentication.supportSASLMechanism("PLAIN", 0);

		connection = new XMPPConnection(config);
		connection.connect();

		try {
			connection.login(parts[0], password, "celeronik");
		} catch (Exception e) {
			e.printStackTrace();
		}

		ChatManager manager = connection.getChatManager();
		Chat chat = manager.createChat(CFG.getProperty("player", "jabber"), this);
		chat.sendMessage("Bubububu");

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		connection.disconnect();

		initialized = true;
	}

	@Override
	public boolean send(Message message) throws MessagingException {

		if (!initialized) {
			throw new IllegalStateException("Driver has not been initialized!");
		}

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Message receive(String code) throws MessagingException {

		if (!initialized) {
			throw new IllegalStateException("Driver has not been initialized!");
		}

		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws InterruptedException {
		new JabberDriver();
		Thread.sleep(20000);
	}

	@Override
	public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message m) {
		System.out.println(m.getBody());

	}

}
