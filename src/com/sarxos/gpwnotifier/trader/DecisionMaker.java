package com.sarxos.gpwnotifier.trader;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.sarxos.gpwnotifier.market.Paper;
import com.sarxos.gpwnotifier.market.Quote;


public class DecisionMaker implements PriceListener {

	private Observer observer = null;
	
	private List<DecisionListener> listeners = new LinkedList<DecisionListener>();
	
	
	public DecisionMaker(Observer observer) {
		this.observer = observer;
		this.observer.addPriceListener(this);
	}

	@Override
	public void priceChange(PriceEvent pe) {

		Wallet wallet = Wallet.getInstance();
		Paper paper = wallet.getPaper(observer.getSymbol());
		
		Quote tmp = new Quote(new Date(), pe.getCurrentPrice());
		
		
		
		
		// decision logic
	}
	
	/**
	 * Notify all listeners about price change.
	 * 
	 * @param de - decision event (buy or sell paper)
	 */
	protected void notifyListeners(DecisionEvent de) {
		
		DecisionListener listener = null;
		ListIterator<DecisionListener> i = listeners.listIterator();
		
		while (i.hasNext()) {
			listener = i.next();
			try {
				listener.decisionChange(de);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return Decision listeners array.
	 */
	public DecisionListener[] getDecisionListeners() {
		return listeners.toArray(new DecisionListener[listeners.size()]);
	}
	
	/**
	 * 
	 * @param listener
	 * @return true if listener was added or false if it is already on the list 
	 */
	public boolean addDecisionListener(DecisionListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
		return false;
	}
	
	/**
	 * Remove particular decision listener.
	 * 
	 * @param listener - decision listener to remove
	 * @return true if listener list contained specified element
	 */
	public boolean removeDecisionListener(DecisionListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * @return Paper observer
	 */
	public Observer getObserver() {
		return observer;
	}
}
