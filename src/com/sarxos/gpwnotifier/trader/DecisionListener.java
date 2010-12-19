package com.sarxos.gpwnotifier.trader;


/**
 * Decision listener interface.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface DecisionListener {

	/**
	 * Notified after decision change.
	 * 
	 * @param event - decision event
	 */
	public void decisionChange(DecisionEvent event);
	
}
