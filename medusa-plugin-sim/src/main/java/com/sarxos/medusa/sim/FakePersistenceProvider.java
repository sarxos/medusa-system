package com.sarxos.medusa.sim;

import com.sarxos.medusa.data.persistence.PersistenceException;
import com.sarxos.medusa.data.persistence.PersistenceProvider;
import com.sarxos.medusa.trader.Trader;


public class FakePersistenceProvider implements PersistenceProvider {

	@Override
	public boolean saveTrader(Trader trader) throws PersistenceException {
		// do nothing
		return true;
	}

}
