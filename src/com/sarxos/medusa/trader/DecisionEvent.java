package com.sarxos.medusa.trader;

import java.util.EventObject;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalType;


/**
 * Decision event - shall I buy or sell given paper.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DecisionEvent extends EventObject {

	private static final long serialVersionUID = 2024272282480149837L;

	/**
	 * Paper to buy / sell.
	 */
	private Paper paper = null;

	/**
	 * Signal type (buy, sell)
	 */
	private SignalType signal = null;

	private Quote quote = null;

	/**
	 * Create price event.
	 * 
	 * @param judge - source observer
	 * @param previous - previous price
	 * @param current 0- current price
	 */
	public DecisionEvent(DecisionMaker judge, Paper paper, Quote quote, SignalType signal) {
		super(judge);
		this.paper = paper;
		this.quote = quote;
		this.signal = signal;
	}

	@Override
	public DecisionMaker getSource() {
		return (DecisionMaker) super.getSource();
	}

	/**
	 * @return Paper to buy or sell.
	 */
	public Paper getPaper() {
		return paper;
	}

	/**
	 * @return signal
	 */
	public SignalType getSignalType() {
		return signal;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append("[");
		sb.append(getSignalType().toString().substring(0, 1));
		sb.append(" ");
		sb.append(getPaper());
		sb.append(" ");
		sb.append(getQuote());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @return the quote
	 */
	protected Quote getQuote() {
		return quote;
	}
}
