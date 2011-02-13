package com.sarxos.medusa.data;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.sarxos.medusa.db.DBDAO;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


/**
 * This class is a runtime storage for quotes for various market
 * symbols. Quotes are stored in weak hash map, so it is always 
 * purged whenever GC requires more memory to allocate. 
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
	private DBDAO dbdao = DBDAO.getInstance();

	/**
	 * Quotes registry singleton instance.
	 */
	private static AtomicReference<QuotesRegistry> instance = new AtomicReference<QuotesRegistry>();
	
	
	/**
	 * Private constructor - this class is a singleton.
	 */
	private QuotesRegistry() {
	}

	/**
	 * @return Return quotes registry instance.
	 */
	public static QuotesRegistry getInstance() {
		instance.compareAndSet(null, new QuotesRegistry());
		return instance.get();
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
}
