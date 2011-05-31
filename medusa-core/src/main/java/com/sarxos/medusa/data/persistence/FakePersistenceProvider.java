package com.sarxos.medusa.data.persistence;

import com.sarxos.medusa.trader.Trader;


public class FakePersistenceProvider implements PersistenceProvider {

	@Override
	public boolean saveTrader(Trader trader) throws PersistenceException {
		// do nothing
		return true;
	}

}
