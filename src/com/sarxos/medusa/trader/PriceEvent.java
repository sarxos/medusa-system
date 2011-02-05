package com.sarxos.medusa.trader;

import java.util.EventObject;

import com.sarxos.medusa.market.Quote;


/**
 * Price event - it handles information about price change.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class PriceEvent extends EventObject {

	private static final long serialVersionUID = 2024272282480149837L;

	private Quote quote = null;

	/**
	 * Previous price.
	 */
	private double previous = 0;
	
	/**
	 * Current (newest) price.
	 */
	private double current = 0;
	
	
	/**
	 * Create price event.
	 * 
	 * @param observer - source observer
	 * @param previous - previous price
	 * @param current - current price
	 * @param quote - quote
	 */
	public PriceEvent(Observer observer, double previous, double current, Quote quote) {
		super(observer);
		this.previous = previous;
		this.current = current;
		this.quote = quote;
	}

	public Quote getQuote() {
		return quote;
	}
	
	/**
	 * @return Return previous price.
	 */
	public double getPreviousPrice() {
		return previous;
	}
	
	/**
	 * @return Return current price.
	 */
	public double getCurrentPrice() {
		return current;
	}

	@Override
	public Observer getSource() {
		return (Observer)super.getSource();
	}
}
