package com.sarxos.medusa.trader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
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
				d = q.getDate();
			} while (d.getTime() < from);

			if (d.getTime() > to) {
				reached = true;
			}

			return q;
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

		PriceListener pl = new PriceListener() {

			@Override
			public void priceChange(PriceEvent pe) {
				System.out.println(pe);
			}
		};

		String from = "2011-01-01 08:00:00";
		String to = "2011-02-26 08:00:00";

		ObserverSimulator simula = new ObserverSimulator(
			Symbol.CPS,
			DATE_FORMAT.parse(from),
			DATE_FORMAT.parse(to));
		simula.addPriceListener(pl);
		simula.getRunner().setDaemon(false);
		simula.start();
	}
}
