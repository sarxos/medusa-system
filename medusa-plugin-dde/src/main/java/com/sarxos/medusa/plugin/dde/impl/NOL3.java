package com.sarxos.medusa.plugin.dde.impl;

import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientEventListener;
import com.pretty_tools.dde.client.DDEClientException;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.plugin.dde.DDEException;
import com.sarxos.medusa.plugin.dde.DDEService;


public class NOL3 implements DDEService {

	static {
		System.setProperty("java.library.path", "target/lib");
	}

	/**
	 * Collect data from DDE.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class DDECollector implements DDEClientEventListener {

		@Override
		public void onDiscconnect() {
			System.out.println("Disconnect");
		}

		@Override
		public void onItemChanged(String topic, String item, String data) {
			System.out.println(String.format("Item change: %s %s %s", topic, item, data));
		}
	}

	/**
	 * DDE service (application hosting DDE items).
	 */
	private final static String DDE_SERVICE = "NOL3";

	/**
	 * DDE stream topic.
	 */
	private final static String DDE_TOPIC = "DDE";

	/**
	 * DDE copnversation object.
	 */
	private DDEClientConversation conv = new DDEClientConversation();

	/**
	 * Is client connected.
	 */
	private boolean connected = false;

	public NOL3() {
		conv.setEventListener(new DDECollector());
	}

	@Override
	public synchronized boolean connect() throws DDEException {
		if (connected) {
			return false; // cannot connect twice
		} else {
			try {
				conv.connect(DDE_SERVICE, DDE_TOPIC);
				connected = true;
			} catch (DDEClientException e) {
				throw new DDEException("Cannot connect to the DDE server", e);
			}
			return true;
		}
	}

	@Override
	public synchronized boolean disconnect() throws DDEException {
		if (connected) {
			try {
				conv.disconnect();
				connected = false;
			} catch (DDEClientException e) {
				throw new DDEException("Cannot disconnect from the DDE server", e);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isConnected() {
		return connected;
	}

	@Override
	public synchronized Quote getQuote(Symbol symbol) {

		// TODO: create advices map, store items in map of maps
		// conv.startAdvice("_PZU_KrOdn");

		// TODO: start async advices and run sync request to get quote
		// System.out.println(conv.request("_PZU_KrOdn"));

		return null;
	}

}
