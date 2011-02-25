package com.sarxos.medusa.provider;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


public interface RealTimeProvider {

	/**
	 * @param symbol
	 * @return Quote for given symbol.
	 * @throws ProviderException
	 */
	public Quote getQuote(Symbol symbol) throws ProviderException;

	/**
	 * @param symbol - market symbol to check
	 * @return true if provider can serve data for symbol, false otherwise
	 */
	public boolean canServe(Symbol symbol);
}
