package com.sarxos.medusa.provider;

import java.util.List;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


public interface HistoryProvider {

	/**
	 * Download and return 6 last quotes.
	 * 
	 * @return Return last 6 quotes.
	 * @throws ProviderException
	 */
	public List<Quote> getLastQuotes(Symbol symbol) throws ProviderException;

	/**
	 * Download and return all quotes.
	 * 
	 * @param symbol - symbol to download
	 * @return Whole historical data
	 * @throws ProviderException
	 */
	public List<Quote> getAllQuotes(Symbol symbol) throws ProviderException;
}
