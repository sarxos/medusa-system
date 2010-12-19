package com.sarxos.gpwnotifier.data;

import com.sarxos.gpwnotifier.market.Symbol;


public interface RealTimeDataProvider {

	public double getValue(Symbol symbol) throws DataProviderException;
	
	public boolean canServe(Symbol symbol);
}
