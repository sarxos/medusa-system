package com.sarxos.medusa.data;

import java.util.List;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


/**
 * Quotes storage.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface QuotesStorage {

	/**
	 * Add quotes history to the storage.
	 * 
	 * @param symbol - quotes symbol
	 * @param quotes - list of quotes
	 * @return true if quotes has been added, false otherwise
	 */
	public boolean addQuotes(Symbol symbol, List<Quote> quotes);

	/**
	 * Read all quotes for given symbol.
	 * 
	 * @param symbol - symbol to read
	 * @return Return list of all quotes for particular symbol
	 */
	public List<Quote> getQuotes(Symbol symbol);

}