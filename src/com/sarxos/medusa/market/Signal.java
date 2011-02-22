package com.sarxos.medusa.market;

import java.util.Date;


/**
 * Signal class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Signal {

	/**
	 * Signal type - buy/sell/delay/wait
	 */
	private SignalType type = null;

	/**
	 * Signal generation date.
	 */
	private Date date = null;

	/**
	 * Quote element on which signal has been generated
	 */
	private Quote quote = null;

	/**
	 * Numeric value of signal trigger - need to be removed/reworked.
	 */
	private double level = 0;

	public double getLevel() {
		return level;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	public Signal() {
		super();
	}

	public Signal(Quote quote, SignalType type) {
		this(quote.getDate(), type, quote, 0);
	}

	public Signal(Date date, SignalType type) {
		this(date, type, null, 0);
	}

	public Signal(Date date, SignalType type, Quote quote) {
		this(date, type, quote, 0);
	}

	public Signal(Date date, SignalType type, Quote quote, double level) {
		super();
		this.date = date;
		this.type = type;
		this.quote = quote;
		this.level = level;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public SignalType getType() {
		return type;
	}

	public void setType(SignalType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
