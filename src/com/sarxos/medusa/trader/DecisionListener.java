package com.sarxos.medusa.trader;

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

	/**
	 * Notified after position change.
	 * 
	 * @param pe
	 */
	public void positionChange(PositionEvent pe);

}
