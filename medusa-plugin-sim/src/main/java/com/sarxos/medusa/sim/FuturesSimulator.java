package com.sarxos.medusa.sim;

import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.trader.DecisionMaker.NullEventHandler;
import com.sarxos.medusa.trader.FuturesTrader;
import com.sarxos.medusa.trader.Observer.NullEvent;
import com.sarxos.medusa.util.Configuration;


public class FuturesSimulator {

	public static void main(String[] args) throws InterruptedException {

		Configuration CFG = Configuration.getInstance();

		CFG.setProperty("data", "history", "com.sarxos.medusa.plugin.bossa.BossaProvider");
		CFG.setProperty("messaging", "driver", "com.sarxos.medusa.sim.FakeMessagesDriver");

		Symbol symbol = Symbol.FW20M11;
		String name = symbol + "Trader";
		SignalGenerator<Quote> siggen = new MAVD(5, 10, 20);
		Paper paper = new Paper(symbol);
		RealTimeProvider provider = new FakeRealTimeProvider(symbol, null, null);

		final FuturesTrader trader = new FuturesTrader(name, siggen, symbol, provider);

		trader.getObserver().setInterval(0);
		trader.getDecisionMaker().setNullHandler(
			new NullEventHandler() {

				@Override
				public void handleNull(NullEvent ne) {
					trader.getObserver().stop();
				}
			}
		);
		trader.trade();

		// System.out.println(trader.getObserver());
		Thread.sleep(20000);
	}
}
