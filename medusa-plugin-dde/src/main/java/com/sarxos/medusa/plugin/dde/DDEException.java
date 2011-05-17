package com.sarxos.medusa.plugin.dde;

import com.pretty_tools.dde.client.DDEClientConversation;


/**
 * Yet another, some abstract DDE exception.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DDEException extends Exception {

	private static final long serialVersionUID = 1L;

	public DDEException(DDEClientConversation conv, String message, Throwable cause) {
		super("DDE service '" + conv.getService() + "' topic '" + conv.getTopic() + "': " + message, cause);
	}

	public DDEException(DDEClientConversation conv, String message) {
		super("DDE service '" + conv.getService() + "' topic '" + conv.getTopic() + "': " + message);
	}

	public DDEException(DDEClientConversation conv, Throwable cause) {
		super("DDE service '" + conv.getService() + "' topic '" + conv.getTopic() + "': " + cause.getMessage(), cause);
	}
}
