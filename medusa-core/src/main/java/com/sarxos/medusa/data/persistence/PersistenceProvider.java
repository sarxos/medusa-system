package com.sarxos.medusa.data.persistence;

import com.sarxos.medusa.trader.Trader;


/**
 * Persistence provider.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface PersistenceProvider {

	/**
	 * Persist trader (into DB or file system)
	 * 
	 * @param trader
	 * @return true if trader has been persisted, false otherwise
	 * @throws PersistenceException
	 */
	public boolean saveTrader(Trader trader) throws PersistenceException;

}
