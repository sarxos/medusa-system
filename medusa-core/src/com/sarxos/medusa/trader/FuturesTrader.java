package com.sarxos.medusa.trader;

import static com.sarxos.medusa.market.Position.LONG;
import static com.sarxos.medusa.market.Position.SHORT;

import com.sarxos.medusa.market.Future;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.SignalGenerator;


/**
 * Trader designed especially to make moves on the Futures market.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class FuturesTrader extends Trader {

	public FuturesTrader(String name, SignalGenerator<Future> siggen, Paper paper) {
		super(name, siggen, paper);
	}

	@Override
	public void decisionChange(DecisionEvent de) {

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
