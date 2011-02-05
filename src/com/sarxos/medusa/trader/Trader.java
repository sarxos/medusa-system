package com.sarxos.medusa.trader;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sarxos.medusa.data.Providers;
import com.sarxos.medusa.data.RealTimeDataProvider;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;

import static com.sarxos.medusa.market.Position.LONG;
import static com.sarxos.medusa.market.Position.SHORT;


@XmlRootElement(name = "trader")
public class Trader extends Thread implements DecisionListener {

	/**
	 * Decision maker (encapsulate decision logic).
	 */
	@XmlElement(name = "dm", required = true)
	private DecisionMaker decisionMaker = null;

	/**
	 * Price observer.
	 */
	@XmlElement(name = "observer", required = true)
	private Observer observer = null;
	
	/**
	 * Current position.
	 */
	@XmlAttribute(name = "position", required = true)
	private Position position = SHORT; 

	
	@Override
	public void decisionChange(DecisionEvent de) {
		
		SignalType signal = de.getSignalType();
		
		switch (signal) {
			case BUY:
				// buy mechanism
				setPosition(LONG);
				break;
			case SELL:
				// sell mechanism
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
		this.position = p;
		getDecisionMaker().setCurrentPosition(p);
	}
	
	/**
	 * @return Return current position (long, short)
	 */
	@XmlTransient
	public Position getPosition() {
		return position;
	}
	
	@XmlTransient
	public DecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(DecisionMaker decisionMaker) {
		this.decisionMaker = decisionMaker;
	}
	
	@XmlTransient
	public Observer getObserver() {
		return observer;
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	public void trade(Symbol symbol) {
		
		RealTimeDataProvider rtdp = Providers.getDefaultRealTimeDataProvider();
		
		Observer observer = new Observer(rtdp, symbol);
		DecisionMaker dm = new DecisionMaker(observer);

		dm.addDecisionListener(this);
		
		setObserver(observer);
		setDecisionMaker(dm);
		
		observer.start();
	}
}
