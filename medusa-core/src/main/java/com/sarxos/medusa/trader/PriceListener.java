package com.sarxos.medusa.trader;


/**
 * Price listener interface.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface PriceListener {

	/**
	 * Notified after price change.
	 * 
	 * @param event - price event
	 */
	public void priceChange(PriceEvent pe);
	
}
