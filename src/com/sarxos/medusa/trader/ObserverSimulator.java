package com.sarxos.medusa.trader;

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

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.generator.SAR;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.RealTimeProvider;


/**
 * Special observer used to simulate trading mechanism on the bas of real
 * intraday quotes data.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ObserverSimulator extends Observer {

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

	public ObserverSimulator(Symbol symbol, Date from, Date to) {
		super(new SimulationProvider(symbol, from.getTime(), to.getTime()), symbol);
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

	public static void main(String[] args) throws ParseException {

		Symbol sym = Symbol.KGH;
		String from = "2010-08-26 08:00:00";
		String to = "2011-02-26 08:00:00";
		SignalGenerator<Quote> siggen = new SAR(0.02, 0.2);

		Wallet.getInstance().addPaper(new Paper(sym, 100));

		final QuotesRegistrySimulator registry = new QuotesRegistrySimulator();

		final ObserverSimulator observer = new ObserverSimulator(sym, DATE_FORMAT.parse(from), DATE_FORMAT.parse(to));
		observer.getRunner().setDaemon(false);

		final DecisionMaker dmaker = new DecisionMaker(observer, siggen) {

			@Override
			protected void handleNull(NullEvent ne) {
				getObserver().stop();
			}
		};
		dmaker.setRegistry(registry);

		DecisionListener dl = new DecisionListener() {

			Map<String, Object> dates = new HashMap<String, Object>();

			@Override
			public void positionChange(PositionEvent pe) {
				// System.out.println(pe);
			}

			@Override
			public void decisionChange(DecisionEvent de) {
				String date = de.getQuote().getDateString();
				if (dates.get(date) == null) {
					System.out.println(de);
					if (de.getSignalType() == SignalType.BUY) {
						dmaker.setCurrentPosition(Position.LONG);
					} else {
						dmaker.setCurrentPosition(Position.SHORT);
					}
				} else {
					return;
				}
				dates.put(date, true);
			}
		};

		dmaker.addDecisionListener(dl);
		dmaker.start();
	}
}
