package com.sarxos.medusa.trader;

import com.sarxos.medusa.trader.DecisionMaker.NullEventHandler;
import com.sarxos.medusa.trader.Observer.NullEvent;


/**
 * This null event handler will stop underlying observer after receiving null
 * event.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class StoppingHandler implements NullEventHandler {

	/**
	 * Observer to be stopped.
	 */
	private Observer observer = null;

	/**
	 * This null event handler will stop given observer after receiving null
	 * event
	 * 
	 * @param observer - observer to be stopped
	 */
	public StoppingHandler(Observer observer) {
		this.observer = observer;
	}

	@Override
	public void handleNull(NullEvent ne) {
		this.observer.stop();
	}
}
