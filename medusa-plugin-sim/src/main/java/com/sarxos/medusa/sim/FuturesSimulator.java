package com.sarxos.medusa.sim;

import java.util.Date;

import com.sarxos.medusa.comm.FakeMessagesBroker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.data.FakeQuotesRegistry;
import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.RealTimeProviderSim;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.trader.FuturesTrader;
import com.sarxos.medusa.trader.StoppingHandler;
import com.sarxos.medusa.util.DateUtils;


public class FuturesSimulator {

	private Symbol symbol = null;
	private Date from = null;
	private Date to = null;
	private String name = null;
	private SignalGenerator<Quote> siggen = null;

	public FuturesSimulator(Symbol symbol, Date from, Date to, SignalGenerator<Quote> siggen) {
		this.symbol = symbol;
		this.from = from;
		this.to = to;
		this.siggen = siggen;
		this.name = symbol + "Simulation";
	}

	public void start() {

		RealTimeProvider provider = new RealTimeProviderSim(symbol, from, to);
		QuotesRegistry qr = new FakeQuotesRegistry();

		FuturesTrader trader = new FuturesTrader(name, siggen, symbol);

		trader.setProvider(provider);
		trader.setQuotesRegistry(qr);
		trader.getObserver().setInterval(0);
		trader.setMessagesBroker(new FakeMessagesBroker());
		trader.getDecisionMaker().setNullHandler(new StoppingHandler(trader.getObserver()));

		trader.trade();

		try {
			Thread.sleep(200000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException, MessagingException {
		Symbol symbol = Symbol.FW20M11;
		SignalGenerator<Quote> siggen = new MAVD(5, 10, 20);
		Date from = DateUtils.fromCGL("20110505");
		Date upto = DateUtils.fromCGL("20110520");

		FuturesSimulator sim = new FuturesSimulator(symbol, from, upto, siggen);
		sim.start();

	}
}
