package com.sarxos.medusa.plugin.dde.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientException;
import com.sarxos.medusa.market.BidAsk;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.plugin.dde.AbstractDDEService;
import com.sarxos.medusa.plugin.dde.DDEException;


/**
 * DDE service wrapper for NOL3 application.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class NOL3 extends AbstractDDEService {

	/**
	 * DDE items to complete quote entry.
	 */
	private static final String[] DDE_ITEMS = new String[] {
		"_%s_KrOtw", // open 0
		"_%s_KrMax", // max 1
		"_%s_KrMin", // min 2
		"_%s_OstTrKr1", // close 3
		"_%s_KpLim1", // bid 4
		"_%s_SpLim1", // ask 5
		"_%s_KpIL1", // bid count 6
		"_%s_SpIL1", // ask count 7
	};

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(NOL3.class.getSimpleName());

	/**
	 * DDE service (application hosting DDE items).
	 */
	private final static String DDE_SERVICE = "NOL3";

	/**
	 * DDE stream topic.
	 */
	private final static String DDE_TOPIC = "DDE";

	/**
	 * Creates new NOL3 instance.
	 */
	public NOL3() {
		super();
	}

	@Override
	public synchronized Quote getQuote(String symbol) throws DDEException {

		if (symbol == null) {
			throw new IllegalArgumentException("Symbol to get from NOL3 cannot be null");
		} else if (symbol.length() == 0) {
			throw new IllegalArgumentException("Symbol to get from NOL3 cannot be empty");
		}

		Quote quote = quotes.get(symbol);
		if (quote == null) {
			quote = new Quote();
			quote.setBidAsk(new BidAsk());
			quotes.put(symbol, quote);
		} else {
			return quote;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Configuring DDE to get '" + symbol + "'");
		}

		DDEClientConversation conv = getConversation();
		String ddei = null;

		// synchronous request
		for (String item : DDE_ITEMS) {
			ddei = String.format(item, symbol);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Requesting DDE item '" + ddei + "'");
			}
			String data = null;
			try {
				data = conv.request(ddei);
			} catch (DDEClientException e) {
				throw new DDEException(conv, "Cannot request item '" + ddei + "'", e);
			}
			data = data.replaceAll("\n", "").replaceAll("\r", "");
			storeItem(ddei, data);
		}

		// asynchronous advice - handled by DDECollector
		for (String item : DDE_ITEMS) {
			ddei = String.format(item, symbol);
			try {
				conv.startAdvice(ddei);
			} catch (DDEClientException e) {
				throw new DDEException(conv, "Cannot advice '" + ddei + "'", e);
			}
		}

		return quote;
	}

	@Override
	public String getService() {
		return DDE_SERVICE;
	}

	@Override
	public String getTopic() {
		return DDE_TOPIC;
	}

	@Override
	protected void storeItem(String item, String value) {

		String[] parts = item.split("_");
		String symbol = parts[1];
		String element = parts[2];

		Quote quote = quotes.get(symbol);
		String tmp = "_%s_" + element;

		for (int i = 0; i < DDE_ITEMS.length; i++) {
			if (tmp.equals(DDE_ITEMS[i])) {
				double c = 0;
				int n = 0;
				if (i <= 5) {
					c = Double.parseDouble(value.replaceAll(",", "."));
				} else if (i >= 6 && i <= 7) {
					n = Integer.parseInt(value);
				}
				switch (i) {
					case 0: // _%s_KrOtw = open
						quote.setOpen(c);
						break;
					case 1: // _%s_KrMax = max
						quote.setHigh(c);
						break;
					case 2: // _%s_KrMin = min
						quote.setLow(c);
						break;
					case 3: // _%s_OstTrKr1 = close
						quote.setClose(c);
						break;
					case 4: // _%s_KpLim1 = bid
						quote.getBidAsk().setBid(c);
						break;
					case 5: // _%s_SpLim1 = ask
						quote.getBidAsk().setAsk(c);
						break;
					case 6: // _%s_KpIL1 = bid count
						quote.getBidAsk().setBidCount(n);
						break;
					case 7: // _%s_SpIL1 = ask count
						quote.getBidAsk().setAskCount(n);
						break;
					default:
						throw new RuntimeException("Unsupported DDE item: '" + tmp + "'");
				}
				break;
			}
		}
	}
}
