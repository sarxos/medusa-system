package com.sarxos.medusa.sim;

import com.sarxos.medusa.comm.FakeMessagesBroker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.data.FakeQuotesRegistry;
import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.FakeRealTimeProvider;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.trader.FuturesTrader;
import com.sarxos.medusa.trader.StoppingHandler;
import com.sarxos.medusa.util.Configuration;


public class FuturesSimulator {

	public static void main(String[] args) throws InterruptedException, MessagingException {

		Configuration CFG = Configuration.getInstance();

		// CFG.setProperty("data", "history",
		// "com.sarxos.medusa.plugin.bossa.BossaProvider");
		// CFG.setProperty("messaging", "driver",
		// "com.sarxos.medusa.sim.FakeMessagesDriver");

		Symbol symbol = Symbol.FW20M11;
		String name = symbol + "Trader";
		SignalGenerator<Quote> siggen = new MAVD(5, 10, 20);
		RealTimeProvider provider = new FakeRealTimeProvider(symbol, null, null);
		QuotesRegistry qr = new FakeQuotesRegistry();

		FuturesTrader trader = new FuturesTrader(name, siggen, symbol);

		trader.setProvider(provider);
		trader.setQuotesRegistry(qr);
		trader.getObserver().setInterval(0);
		trader.setMessagesBroker(new FakeMessagesBroker());
		trader.getDecisionMaker().setNullHandler(new StoppingHandler(trader.getObserver()));

		trader.trade();

		// System.out.println(trader.getObserver());
		Thread.sleep(20000);
	}
}
