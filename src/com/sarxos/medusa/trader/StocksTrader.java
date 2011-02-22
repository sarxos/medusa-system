package com.sarxos.medusa.trader;

import static com.sarxos.medusa.market.Position.LONG;
import static com.sarxos.medusa.market.Position.SHORT;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;


public class StocksTrader extends Trader {

	public StocksTrader(String name, SignalGenerator<Quote> siggen, Symbol symbol) {
		super(name, siggen, symbol);
	}

	@Override
	public void decisionChange(DecisionEvent de) {

		System.out.println("stocks trader \n" + de);

		switch (de.getSignalType()) {
			case BUY:
				if (acknowledge(de)) {
					// TODO buy mechanism - future - need QuickFixJ endpoint
					// for Bossa or Alior DAO (preferred)
					setPosition(LONG);
				}
				break;
			case SELL:
				if (acknowledge(de)) {
					// TODO sell mechanism - future - need QuickFixJ endpoint
					// for Bossa or Alior DAO (preferred)
					setPosition(SHORT);
				}
				break;
		}
	}
}
