package com.sarxos.medusa.provider;

import java.util.List;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


public interface HistoryProvider {

	/**
	 * Return 6 last end-of-day quotes for given symbol.
	 * 
	 * @return Return last 6 quotes.
	 * @throws ProviderException
	 */
	public List<Quote> getLastQuotes(Symbol symbol) throws ProviderException;

	/**
	 * Return all end-of-day quotes for given symbol.
	 * 
	 * @param symbol - symbol to get quotes for
	 * @return Whole historical data
	 * @throws ProviderException
	 */
	public List<Quote> getAllQuotes(Symbol symbol) throws ProviderException;

	/**
	 * Return all intraday quotes iterator for given symbol.
	 * 
	 * @param symbol
	 * @return Return quotes iterator.
	 * @throws ProviderException
	 */
	public QuotesIterator<Quote> getIntradayQuotes(Symbol symbol) throws ProviderException;
}
