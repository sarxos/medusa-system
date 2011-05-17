package com.sarxos.medusa.plugin.dde;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientEventListener;
import com.pretty_tools.dde.client.DDEClientException;
import com.sarxos.medusa.market.Quote;


/**
 * This is abstract class for all DDE services (NOL3, MT4, etc). User has to
 * override three basic methods listed below in <i>See Also</i> section.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see DDEService#getService()
 * @see DDEService#getTopic()
 * @see DDEService#getQuote(String)
 */
public abstract class AbstractDDEService implements DDEService {

	/**
	 * Collect data from DDE.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class DDECollector implements DDEClientEventListener {

		@Override
		public void onDiscconnect() {
			System.out.println("Disconnect by service");

			// TODO: reconnect or alert
		}

		@Override
		public void onItemChanged(String topic, String item, String data) {
			topic = topic.replaceAll("\n", "").trim();
			item = item.replaceAll("\n", "").trim();
			data = data.replaceAll("\n", "").trim();

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Item change: %s %s %s", topic, item, data));
			}

			storeItem(item, data);
		}
	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDDEService.class.getSimpleName());

	/**
	 * Map containing all items.
	 */
	protected Map<String, Quote> quotes = new HashMap<String, Quote>();

	/**
	 * DDE conversation object.
	 */
	private DDEClientConversation conv = new DDEClientConversation();

	/**
	 * Is client connected.
	 */
	private boolean connected = false;

	public AbstractDDEService() {
		conv.setEventListener(new DDECollector());
	}

	@Override
	public synchronized boolean connect() throws DDEException {

		if (connected) {
			return false; // cannot connect twice
		} else {
			try {

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Connecting %s %s", getService(), getTopic()));
				}

				conv.connect(getService(), getTopic());
				connected = true;

			} catch (DDEClientException e) {
				throw new DDEException(conv, "Cannot connect to the DDE server", e);
			}
			return true;
		}
	}

	@Override
	public synchronized boolean disconnect() throws DDEException {

		if (connected) {
			try {

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Disconnecting %s %s", conv.getService(), conv.getTopic()));
				}

				conv.disconnect();
				connected = false;

			} catch (DDEClientException e) {
				throw new DDEException(conv, "Cannot disconnect from the DDE server", e);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public DDEClientConversation getConversation() {
		return conv;
	}

	/**
	 * Store item in the memory for future usage.
	 * 
	 * @param item - item to be stored
	 * @param value - value of item
	 */
	protected abstract void storeItem(String item, String value);
}
