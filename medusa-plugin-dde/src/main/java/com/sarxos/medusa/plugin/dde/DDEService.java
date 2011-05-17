package com.sarxos.medusa.plugin.dde;

import com.sarxos.medusa.market.Quote;


/**
 * Abstract interface for the DDE service. User can connect / disconnect
 * service.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface DDEService {

	/**
	 * Connect to the DDE service.
	 * 
	 * @return true in case if connection is established, false otherwise
	 */
	public boolean connect() throws DDEException;

	/**
	 * Disconnect from the DDE service.
	 * 
	 * @return true in case if client has been disconnected, false otherwise
	 */
	public boolean disconnect() throws DDEException;

	/**
	 * Return quote for given symbol.
	 * 
	 * @param symbol - symbol to get quote for (e.g. "PEO" or "FW20M11")
	 * @return Quote for given symbol
	 */
	public Quote getQuote(String symbol) throws DDEException;

	/**
	 * @return Return DDE service name
	 */
	public String getService();

	/**
	 * @return Return DDE topic name
	 */
	public String getTopic();
}
