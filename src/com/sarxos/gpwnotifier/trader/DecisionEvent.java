package com.sarxos.gpwnotifier.trader;

import java.util.EventObject;

import com.sarxos.gpwnotifier.market.Paper;
import com.sarxos.gpwnotifier.market.SignalType;


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
	

	/**
	 * Create price event.
	 * 
	 * @param judge - source observer
	 * @param previous - previous price
	 * @param current 0- current price
	 */
	public DecisionEvent(DecisionMaker judge, Paper paper, SignalType signal) {
		super(judge);
		this.paper = paper;
		this.signal = signal;
	}
	

	@Override
	public DecisionMaker getSource() {
		return (DecisionMaker)super.getSource();
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
}
