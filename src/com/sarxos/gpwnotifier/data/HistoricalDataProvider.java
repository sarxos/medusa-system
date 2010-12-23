package com.sarxos.gpwnotifier.data;

import java.util.List;

import com.sarxos.gpwnotifier.market.Quote;
import com.sarxos.gpwnotifier.market.Symbol;


public interface HistoricalDataProvider {

	/**
	 * Download and return 6 last quotes.
	 * 
	 * @return Return last 6 quotes.
	 * @throws DataProviderException 
	 */
	public List<Quote> getLastQuotes(Symbol symbol) throws DataProviderException; 
	
	/**
	 * Download and return all quotes.
	 * 
	 * @param symbol - symbol to download
	 * @return Whole historical data
	 * @throws DataProviderException
	 */	
	public List<Quote> getAllQuotes(Symbol symbol) throws DataProviderException;
}
