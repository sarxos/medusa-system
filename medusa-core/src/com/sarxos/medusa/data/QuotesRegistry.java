package com.sarxos.medusa.data;

import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


/**
 * This class is a runtime storage for quotes for various market symbols. Quotes
 * are stored in weak hash map, so it is always purged whenever GC requires more
 * memory to allocate.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class QuotesRegistry {

	/**
	 * Weak hash storage for market quotes.
	 */
	private WeakHashMap<Symbol, List<Quote>> quotes = new WeakHashMap<Symbol, List<Quote>>();

	/**
	 * Database DAO instance.
	 */
	private DBDAO dbdao = null;

	/**
	 * Quotes registry singleton instance.
	 */
	private static QuotesRegistry instance = new QuotesRegistry();

	/**
	 * Private constructor - this class is a singleton.
	 */
	protected QuotesRegistry() {
		MySQLRunner.getInstance().runMySQL();
		dbdao = DBDAO.getInstance();
	}

	/**
	 * @return Return quotes registry instance.
	 */
	public static QuotesRegistry getInstance() {
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
		if (q == null) {
			q = dbdao.getQuotes(symbol);
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
			list = dbdao.getQuotes(s);
			quotes.put(s, list);
		}
	}

	/**
	 * Reload quotes within registry.
	 */
	public void reload(Symbol s) {
		quotes.put(s, dbdao.getQuotes(s));
	}
}
