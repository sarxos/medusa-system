package com.sarxos.medusa.trader;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.provider.RealTimeProvider;


public class TestTrader extends Trader {

	@Override
	public void decisionChange(DecisionEvent event) {
	}

	public TestTrader(String name, SignalGenerator<? extends Quote> siggen, Paper paper, RealTimeProvider provider) {
		super(name, siggen, paper, provider);
	}

	public TestTrader(String name, SignalGenerator<? extends Quote> siggen, Paper paper) {
		super(name, siggen, paper);
	}
}