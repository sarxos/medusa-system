package com.sarxos.medusa.data;

import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.HistoryProvider;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.Providers;
import com.sarxos.medusa.sql.DBDAO;


/**
 * This class is a runtime storage for quotes for various market symbols. Quotes
 * are stored in weak hash map, so it is always purged whenever GC requires more
 * memory to allocate.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class QuotesRegistry {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(QuotesRegistry.class.getSimpleName());

	/**
	 * Weak hash storage for market quotes.
	 */
	private WeakHashMap<Symbol, List<Quote>> quotes = new WeakHashMap<Symbol, List<Quote>>();

	/**
	 * Database DAO instance.
	 */
	private QuotesStorage storage = null;

	/**
	 * Quotes registry singleton instance.
	 */
	private static QuotesRegistry instance = null;

	/**
	 * Protected constructor - this is singleton class.
	 */
	protected QuotesRegistry() {
	}

	/**
	 * @return Return quotes registry instance.
	 */
	public static QuotesRegistry getInstance() {
		if (instance == null) {
			instance.storage = DBDAO.getInstance();
			instance = new QuotesRegistry();
		}
		return instance;
	}

	protected void setInstance(QuotesRegistry qr) {
		if (qr == null) {
			throw new IllegalArgumentException("Quotes registry instance cannot be null");
		}
		instance = qr;
	}

	/**
	 * This method return quotes for given symbol.
	 * 
	 * @param symbol (e.g. KGH, BRE, etc)
	 * @return Quotes for given symbol
	 */
	public List<Quote> getQuotes(Symbol symbol) {
		List<Quote> q = quotes.get(symbol);
		if (q == null || q.isEmpty()) {
			q = storage.getQuotes(symbol);
			if (q == null || q.isEmpty()) {
				reimport(symbol);
				q = storage.getQuotes(symbol);
				if (q == null || q.isEmpty()) {
					throw new RuntimeException("Cannot get quotes for symbol " + symbol);
				}
			}
			quotes.put(symbol, q);
		}
		return q;
	}

	/**
	 * Reload quotes within registry.
	 */
	public void reload() {

		List<Quote> list = null;
		Set<Symbol> symbols = quotes.keySet();

		for (Symbol s : symbols) {
			list = storage.getQuotes(s);
			quotes.put(s, list);
		}
	}

	/**
	 * Reload quotes within registry.
	 */
	public void reload(Symbol s) {
		quotes.put(s, storage.getQuotes(s));
	}

	/**
	 * Import quotes to the storage.
	 * 
	 * @param symbol - quotes symbol to import
	 */
	public void reimport(Symbol symbol) {
		LOG.info("Importing quotes for symbol " + symbol);
		HistoryProvider hp = Providers.getHistoryProvider();
		try {
			List<Quote> quotes = hp.getAllQuotes(symbol);
			storage.addQuotes(symbol, quotes);
		} catch (ProviderException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
