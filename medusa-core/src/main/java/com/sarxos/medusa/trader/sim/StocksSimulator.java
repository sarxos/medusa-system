package com.sarxos.medusa.trader.sim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.Providers;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.trader.DecisionEvent;
import com.sarxos.medusa.trader.DecisionListener;
import com.sarxos.medusa.trader.DecisionMaker;
import com.sarxos.medusa.trader.Observer;
import com.sarxos.medusa.trader.PositionEvent;
import com.sarxos.medusa.trader.Wallet;


/**
 * Special observer used to simulate trading mechanism on the bas of real
 * intraday quotes data.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class StocksSimulator extends Observer {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Internal real time data provider used in the simulation.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static class SimulationProvider implements RealTimeProvider {

		/**
		 * From date limit.
		 */
		private long from = 0;

		/**
		 * To date limit.
		 */
		private long to = 0;

		/**
		 * If 'to' limit has been reached.
		 */
		private boolean reached = false;

		/**
		 * Intraday quotes iterator.
		 */
		private QuotesIterator<Quote> qi = null;

		private QuotesRegistrySimulator registry = null;

		public SimulationProvider(Symbol symbol, long from, long to) {
			if (from > to) {
				throw new IllegalArgumentException(
					"Time 'from' cannot be larger then 'to' time. Current " +
					"values are 'from' = " + from + " and 'to' = " + to);
			}

			try {
				qi = new QuotesIterator<Quote>(symbol);
			} catch (FileNotFoundException e) {
				try {
					qi = Providers.getHistoryProvider().getIntradayQuotes(symbol);
				} catch (ProviderException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			this.from = from;
			this.to = to;

			this.registry = (QuotesRegistrySimulator) QuotesRegistrySimulator.getInstance();
		}

		@Override
		public Quote getQuote(Symbol symbol) throws ProviderException {

			if (reached) {
				return null;
			}

			Date d = null;
			Quote q = null;

			do {
				if ((q = qi.next()) == null) {
					return null;
				}
				putInRegistry(symbol, q);
				d = q.getDate();
			} while (d.getTime() < from);

			if (d.getTime() > to) {
				reached = true;
			}

			if (last != null) {
				q.setOpen(open);
				q.setHigh(high);
				q.setLow(low);
				q.setVolume(volume);
			}

			return q;
		}

		double open = 0;
		double high = Double.MIN_VALUE;
		double low = Double.MAX_VALUE;
		long volume = 0;

		private Quote last = null;
		private Calendar calendar = new GregorianCalendar();

		protected void putInRegistry(Symbol s, Quote q) {

			if (last == null) {
				last = q;
				open = q.getOpen();
				high = q.getHigh();
				low = q.getLow();
			}

			int a = getDay(q);
			int b = getDay(last);

			if (a > b) {
				Quote qq = new Quote(s, last.getDate(), open, high, low, last.getClose(), volume);
				registry.addQuote(s, qq);
				open = q.getOpen();
				high = q.getHigh();
				low = q.getLow();
				volume = 0;
			}

			double h = q.getHigh();
			double l = q.getLow();

			high = h > high ? h : high;
			low = l < low ? l : low;
			volume += q.getVolume();

			last = q;
		}

		private int getDay(Quote q) {
			calendar.setTime(q.getDate());
			return calendar.get(Calendar.DAY_OF_YEAR);
		}

		@Override
		public boolean canServe(Symbol symbol) {
			return true;
		}

		/**
		 * @return the from
		 */
		protected long getFrom() {
			return from;
		}

		/**
		 * @return the to
		 */
		protected long getTo() {
			return to;
		}

		/**
		 * @return the reached
		 */
		protected boolean isReached() {
			return reached;
		}
	}

	/**
	 * Class used to simulate original quotes registry.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static class QuotesRegistrySimulator extends QuotesRegistry {

		private Map<Symbol, List<Quote>> quotes = new HashMap<Symbol, List<Quote>>();

		public QuotesRegistrySimulator() {
			setInstance(this);
		}

		@Override
		public List<Quote> getQuotes(Symbol symbol) {
			return quotes.get(symbol);
		}

		public void addQuote(Symbol symbol, Quote q) {
			List<Quote> qs = quotes.get(symbol);
			if (qs == null) {
				qs = new LinkedList<Quote>();
				quotes.put(symbol, qs);
			}
			int n = qs.size();
			if (n > 0) {
				Quote t = qs.get(n - 1);
				t.setNext(q);
				q.setPrev(t);
			}
			qs.add(q);
		}
	}

	public StocksSimulator(Symbol symbol, Date from, Date to) {
		super(symbol, new SimulationProvider(symbol, from.getTime(), to.getTime()));
		setInterval(0);
	}

	/**
	 * @return the from
	 */
	protected Date getFrom() {
		long time = ((SimulationProvider) getProvider()).getFrom();
		return new Date(time);
	}

	/**
	 * @return the to
	 */
	protected Date getTo() {
		long time = ((SimulationProvider) getProvider()).getTo();
		return new Date(time);
	}

	protected static void configureLoggers() {

		// assume SLF4J is bound to logback in the current environment
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);

			// the context was probably already configured by default
			// configuration rules, so it needs to be reset
			lc.reset();
			configurator.doConfigure(new File("data/logback.xml"));
		} catch (JoranException e) {
			e.printStackTrace();
		}

		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.WARN);

	}

	public static void main(String[] args) throws ParseException {

		configureLoggers();

		Symbol sym = Symbol.BRE;
		String from = "2008-01-01 08:00:00";
		String to = "2011-04-12 08:00:00";
		SignalGenerator<Quote> siggen = new MAVD(20, 40, 50);

		Wallet.getInstance().addPaper(new Paper(sym, 100));

		final QuotesRegistrySimulator registry = new QuotesRegistrySimulator();

		final StocksSimulator observer = new StocksSimulator(sym, DATE_FORMAT.parse(from), DATE_FORMAT.parse(to));
		observer.getRunner().setDaemon(false);

		final CyclicBarrier cb = new CyclicBarrier(2);

		final DecisionMaker dmaker = new DecisionMaker(null, observer, siggen) {

			@Override
			protected void handleNull(NullEvent ne) {

				try {
					cb.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}

				getObserver().stop();
			}
		};
		dmaker.setRegistry(registry);

		final double start = 3000;
		final double[] finish = new double[1];

		DecisionListener dl = new DecisionListener() {

			Map<String, Object> dates = new HashMap<String, Object>();

			double cash = start;
			int number = 0;
			double assets = 0;

			@Override
			public void positionChange(PositionEvent pe) {
				// System.out.println(pe);
			}

			@Override
			public void decisionChange(DecisionEvent de) {

				Quote q = de.getQuote();
				String date = q.getDateString();

				if (dates.get(date) == null) {

					// System.out.println(de);
					if (de.getSignalType() == SignalType.BUY) {
						dmaker.setPosition(Position.LONG);

						int n = (int) Math.ceil(cash / q.getClose());
						double fund = n * q.getClose();
						double tax = fund * 0.0028;
						double spread = n * 0.01;

						cash = cash - fund - tax - spread;
						number = n;

						assets = n * q.getClose() + cash;
						finish[0] = assets;

						System.out.println(
							q.getDateString() + " " +
							"B " + n + " stocks for " + q.getClose() +
							" PLN/q - assets = " + String.format("%.2f", assets) + " " +
							"tax = " + String.format("%.2f", tax) + " spread = " +
							String.format("%.2f", spread));

					} else {

						dmaker.setPosition(Position.SHORT);

						double fund = number * q.getClose();
						double tax = fund * 0.0028;
						double spread = number * 0.01;

						int n = number;

						cash = cash + fund - tax - spread;
						number = 0;

						assets = cash;
						finish[0] = assets;

						System.out.println(
							q.getDateString() + " " +
							"S " + n + " stocks for " + q.getClose() +
							" PLN/q - assets = " + String.format("%.2f", assets) + " " +
							"tax = " + String.format("%.2f", tax) + " spread = " +
							String.format("%.2f", spread));
					}
				} else {
					return;
				}

				dates.put(date, true);
			}
		};

		dmaker.addDecisionListener(dl);
		dmaker.start();

		try {
			cb.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}

		System.out.println("Profit: " + Math.round(100 * (finish[0] - start) / start) + "%");
	}
}
