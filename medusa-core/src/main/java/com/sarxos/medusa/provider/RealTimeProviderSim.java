package com.sarxos.medusa.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;

import com.sarxos.medusa.data.FakeQuotesRegistry;
import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


/**
 * Fake real time data provider provides intraday quotes for given symbol within
 * given time interval.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class RealTimeProviderSim implements RealTimeProvider {

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

	/**
	 * Fake quotes registry (non persistent).
	 */
	private FakeQuotesRegistry registry = null;

	/**
	 * Opening price.
	 */
	private double open = 0;

	/**
	 * Highest price.
	 */
	private double high = Double.MIN_VALUE;

	/**
	 * Lowest price.
	 */
	private double low = Double.MAX_VALUE;

	/**
	 * Quotes volume.
	 */
	private long volume = 0;

	/**
	 * Last read quote.
	 */
	private Quote last = null;

	/**
	 * Calendar used to calculate dates.
	 */
	private Calendar calendar = new GregorianCalendar();

	/**
	 * Real time data provider. Its provides intraday quotes within given time
	 * interval.
	 * 
	 * @param symbol - symbol to provide quotes for
	 * @param from - begin from date
	 * @param to - up to date (after reaching this date null will be returned)
	 */
	public RealTimeProviderSim(Symbol symbol, Date from, Date to) {

		if (from != null) {
			this.from = from.getTime();
		} else {
			this.from = Long.MIN_VALUE;
		}
		if (to != null) {
			this.to = to.getTime();
		} else {
			this.to = Long.MAX_VALUE;
		}

		if (this.from > this.to) {
			throw new IllegalArgumentException(
				"Time 'from' cannot be larger then 'to' time. Current " +
				"values are 'from' = " + from + " and 'to' = " + to);
		}

		try {
			qi = new QuotesIterator<Quote>(symbol);
		} catch (FileNotFoundException e) {

			HistoryProvider hp = registry.getHistoryProvider();
			if (hp == null) {
				hp = Providers.getHistoryProvider();
			}

			try {
				qi = hp.getIntradayQuotes(symbol);
			} catch (ProviderException e1) {
				throw new RuntimeException(e1);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (from != null) {
			qi.forward(from);
			if (!qi.hasNext()) {
				throw new IllegalArgumentException(
					"Time 'from' cannot be larger then 'to' time. Current " +
					"values are 'from' = " + from + " and 'to' = " + to);
			}
		}

		this.registry = FakeQuotesRegistry.getInstance();
		this.registry.load(symbol, from);
	}

	@Override
	public Quote getQuote(Symbol symbol) throws ProviderException {

		if (reached) {
			return null;
		}

		Date d = null;
		Quote q = null;

		do {
			try {
				q = qi.next();
			} catch (NoSuchElementException e) {
				// end of iterator's underlying stream has been reached
				this.reached = true;
				qi.close();
				return null;
			}

			putInRegistry(symbol, q);
			d = q.getDate();

		} while (d.getTime() < from);

		if (d.getTime() > to) {
			reached = true;
			qi.close();
			return null;
		}

		if (last != null) {
			q.setOpen(open);
			q.setHigh(high);
			q.setLow(low);
			q.setVolume(volume);
		}

		return q;
	}

	/**
	 * Put given quote in registry.
	 * 
	 * @param s - quote's symbol
	 * @param q - quote to put in registry
	 */
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

	/**
	 * Return number of day for quote (day of year).
	 * 
	 * @param q
	 * @return Will return day of year number
	 */
	private int getDay(Quote q) {
		calendar.setTime(q.getDate());
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public boolean canServe(Symbol symbol) {
		return symbol.toString().startsWith("FW20");
	}

	/**
	 * @return Return from date
	 */
	protected long getFrom() {
		return from;
	}

	/**
	 * @return Return to date
	 */
	protected long getTo() {
		return to;
	}

	/**
	 * @return Is to date reached
	 */
	protected boolean isReached() {
		return reached;
	}
}
