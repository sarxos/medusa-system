package com.sarxos.gpwnotifier.market;


/**
 * Possible signal types.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public enum SignalType {
	
	/**
	 * Do nothing.
	 */
	WAIT,
	
	/**
	 * Buy paper.
	 */
	BUY,
	
	/**
	 * Delay buy or sell.
	 */
	DELAY,
	
	/**
	 * Sell paper.
	 */
	SELL;
}
