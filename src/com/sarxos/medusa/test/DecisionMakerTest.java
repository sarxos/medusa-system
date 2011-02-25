package com.sarxos.medusa.test;

import java.util.Date;

import junit.framework.TestCase;

import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.trader.DecisionEvent;
import com.sarxos.medusa.trader.DecisionListener;
import com.sarxos.medusa.trader.DecisionMaker;
import com.sarxos.medusa.trader.Observer;
import com.sarxos.medusa.trader.PositionEvent;


public class DecisionMakerTest extends TestCase {

	private static final Symbol SYMBOL = Symbol.KGH;
	private static final Observer OBSERVER = new Observer(SYMBOL);
	private static final SignalGenerator<Quote> SIGGEN = new MAVD();

	public void test_listener() {

		DecisionMaker dm = new DecisionMaker(OBSERVER, SIGGEN);

		DecisionListener dl = new DecisionListener() {

			@Override
			public void decisionChange(DecisionEvent event) {
				System.out.println(event);
			}

			@Override
			public void positionChange(PositionEvent pe) {
				System.out.println(pe);
			}
		};

		dm.addDecisionListener(dl);

		Quote q = new Quote(new Date(), 10, 12, 9, 11, 3456);

		// dm.priceChange(new PriceEvent(OBSERVER, 11.5, 12, q));

	}
}
