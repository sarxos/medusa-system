package com.sarxos.medusa.trader;


import static com.sarxos.medusa.market.Position.LONG;
import static com.sarxos.medusa.market.Position.SHORT;

import com.sarxos.medusa.data.Providers;
import com.sarxos.medusa.data.RealTimeDataProvider;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;


public class Trader implements DecisionListener {

	/**
	 * Decision maker (encapsulate decision logic).
	 */
	private DecisionMaker decisionMaker = null;
	
	private SignalGenerator<Quote> siggen = null;
	
	private RealTimeDataProvider provider = null;
	
	private Symbol symbol = null;
	
	private String name = null;
	
	
	public Trader(String name, SignalGenerator<Quote> siggen) {
		this(name, siggen, null, null);
	}

	public Trader(String name, SignalGenerator<Quote> siggen, RealTimeDataProvider provider) {
		this(name, siggen, provider, null);
	}
	
	public Trader(String name, SignalGenerator<Quote> siggen, RealTimeDataProvider provider, Symbol symbol) {
		if (name == null) {
			throw new IllegalArgumentException("Trader name cannot be null");
		}
		if (siggen == null) {
			throw new IllegalArgumentException("Signal generator cannotbe null");
		}
		this.name = name;
		this.siggen = siggen;
		this.provider = provider;
		this.symbol = symbol;
		this.init();
	}
	
	protected void init() {
		if (provider == null) {
			provider = Providers.getDefaultRealTimeDataProvider();
		}
	}
	
	@Override
	public void decisionChange(DecisionEvent de) {
		
		SignalType signal = de.getSignalType();
		
		switch (signal) {
			case BUY:
				// TODO buy mechanism
				setPosition(LONG);
				break;
			case SELL:
				// TODO sell mechanism
				setPosition(SHORT);
				break;
			case DELAY:
				// do nothing
				break;
		}
	}

	/**
	 * Set new position
	 * @param p - new position to set
	 */
	public void setPosition(Position p) {
		if (p == null) {
			throw new IllegalArgumentException("Position cannot be null");
		}
		getDecisionMaker().setCurrentPosition(p);
	}
	
	/**
	 * @return Return current position (long, short)
	 */
	public Position getPosition() {
		DecisionMaker dm = getDecisionMaker();
		if (dm == null) {
			return null;
		}
		return dm.getCurrentPosition();
	}
	
	public DecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(DecisionMaker decisionMaker) {
		this.decisionMaker = decisionMaker;
	}
	
	public Observer getObserver() {
		if (getDecisionMaker() == null) {
			return null;
		}
		return getDecisionMaker().getObserver();
	}

	public void setObserver(Observer observer) {
		if (getDecisionMaker() == null) {
			throw new IllegalStateException(
					"Cannot set observer because decision maker " +
					"is not created."
			);
		}
		getDecisionMaker().setObserver(observer);
	}

	public Symbol getSymbol() {
		DecisionMaker dm = getDecisionMaker();
		Observer o = null;
		if (dm != null && (o = dm.getObserver()) != null) {
			return o.getSymbol();
		} else {
			return symbol;
		}
	}
	
	public String getGeneratorName(){
		return siggen.getClass().getName();
	}
	
	public SignalGenerator<Quote> getGenerator() {
		return siggen;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Start trade.
	 *  
	 * @param symbol - observed symbol
	 */
	public void trade(Symbol symbol) {
		
		if (symbol == null) {
			throw new IllegalArgumentException("Symbol to trade cannot be null");
		}
		
		Observer observer = new Observer(provider, symbol);
		DecisionMaker dm = new DecisionMaker(observer, siggen);
		dm.addDecisionListener(this);
		
		setDecisionMaker(dm);
		
		dm.getObserver().start();
	}

	public void trade() {
		this.trade(symbol);
	}
}
