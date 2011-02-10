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


/**
 * Trader class. It is designed to handle decision events from decision maker,
 * send notification/question and buy or sell given paper.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Trader implements DecisionListener, Runnable {

	/**
	 * Decision maker (encapsulate decision logic).
	 */
	private DecisionMaker decisionMaker = null;
	
	/**
	 * Signal generator to use.
	 */
	private SignalGenerator<Quote> siggen = null;
	
	/**
	 * Real time data provider.
	 */
	private RealTimeDataProvider provider = null;

	/**
	 * Observed symbol.
	 */
	private Symbol symbol = null;
	
	/**
	 * Trader name.
	 */
	private String name = null;
	
	
	/**
	 * Trader constructor.
	 * 
	 * @param name - trader name
	 * @param siggen - signal generator
	 * @param symbol - symbol to trade (e.g. KGH, BRE)
	 */
	public Trader(String name, SignalGenerator<Quote> siggen, Symbol symbol) {
		this(name, siggen, symbol, null);
	}

	public Trader(String name, SignalGenerator<Quote> siggen, Symbol symbol, RealTimeDataProvider provider) {
		if (name == null) {
			throw new IllegalArgumentException("Trader name cannot be null");
		}
		if (siggen == null) {
			throw new IllegalArgumentException("Signal generator cannotbe null");
		}
		this.name = name;
		this.siggen = siggen;
		this.symbol = symbol;
		this.provider = provider;
		this.symbol = symbol;
		this.init();
	}
	
	/**
	 * Initialize trader
	 */
	protected void init() {
		
		if (provider == null) {
			provider = Providers.getDefaultRealTimeDataProvider();
		}
		
		Observer observer = new Observer(provider, symbol);
		DecisionMaker dm = new DecisionMaker(observer, siggen);
		dm.addDecisionListener(this);
		
		setDecisionMaker(dm);		
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
	
	/**
	 * @return Decision maker
	 */
	public DecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	/**
	 * Set new decision maker
	 * 
	 * @param decisionMaker - new decision maker to set
	 */
	public void setDecisionMaker(DecisionMaker decisionMaker) {
		this.decisionMaker = decisionMaker;
	}
	
	/**
	 * @return Price observer
	 */
	public Observer getObserver() {
		if (getDecisionMaker() == null) {
			return null;
		}
		return getDecisionMaker().getObserver();
	}

	/**
	 * Set new price observer.
	 * 
	 * @param observer - new observer to set
	 */
	public void setObserver(Observer observer) {
		if (getDecisionMaker() == null) {
			throw new IllegalStateException(
					"Cannot set observer because decision maker " +
					"is not created."
			);
		}
		getDecisionMaker().setObserver(observer);
	}

	/**
	 * @return Observed symbol (e.g. KGH, BRE)
	 */
	public Symbol getSymbol() {
		DecisionMaker dm = getDecisionMaker();
		Observer o = null;
		if (dm != null && (o = dm.getObserver()) != null) {
			return o.getSymbol();
		} else {
			return symbol;
		}
	}
	
	/**
	 * @return Signal generator class name
	 */
	public String getGeneratorClassName(){
		return siggen.getClass().getName();
	}
	
	/**
	 * @return Signal generator
	 */
	public SignalGenerator<Quote> getGenerator() {
		return siggen;
	}

	/**
	 * @return Trader's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Start trade.
	 *  
	 * @param symbol - observed symbol
	 */
	public void trade() {
		getDecisionMaker().getObserver().start();
	}

	@Override
	public void run() {
		trade();
	}
}
