package com.sarxos.medusa.sim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.Providers;
import com.sarxos.medusa.provider.RealTimeProvider;


public class FakeRealTimeProvider implements RealTimeProvider {

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

	private FakeQuotesRegistry registry = null;

	public FakeRealTimeProvider(Symbol symbol, long from, long to) {
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

		this.registry = FakeQuotesRegistry.getInstance();
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
		return symbol.toString().startsWith("FW20");
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
