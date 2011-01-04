package com.sarxos.gpwnotifier.market;

import java.util.List;
import java.util.WeakHashMap;

import com.sarxos.gpwnotifier.db.DBDAO;


public class QuotesRegistry {

	private WeakHashMap<Symbol, List<Quote>> quotes = new WeakHashMap<Symbol, List<Quote>>();
	
	private DBDAO dbdao = new DBDAO();

	private static QuotesRegistry instance = null;
	
	private QuotesRegistry() {
	}

	public static QuotesRegistry getInstance() {
		return instance;
	}
	
	public List<Quote> getQuotes(Symbol symbol) {
		List<Quote> q = quotes.get(symbol);
		if (q == null) {
			q = dbdao.getQuotes(symbol);
			quotes.put(symbol, q);
		}
		return q;
	}
}
