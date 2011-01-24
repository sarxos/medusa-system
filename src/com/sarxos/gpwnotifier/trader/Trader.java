package com.sarxos.gpwnotifier.trader;

import com.sarxos.gpwnotifier.data.Providers;
import com.sarxos.gpwnotifier.data.RealTimeDataProvider;
import com.sarxos.gpwnotifier.market.SignalType;
import com.sarxos.gpwnotifier.market.Symbol;


public class Trader extends Thread implements DecisionListener {

	private DecisionMaker decisionMaker = null;
	private Observer observer = null;

	
	@Override
	public void decisionChange(DecisionEvent de) {
		
		SignalType signal = de.getSignalType();
		
		switch (signal) {
			case BUY:
				break;
			case SELL:
				break;
			case DELAY:
				break;
		}
		
		// buy o sell and notify decision maker
	}
	
	public DecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(DecisionMaker decisionMaker) {
		this.decisionMaker = decisionMaker;
	}
	
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
	}
}
