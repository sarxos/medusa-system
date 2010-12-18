package com.sarxos.gpwnotifier.gpw;


/**
 * Securities groups.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public enum SecuritiesGroup {

	/**
	 * Stocks from WIG20 - 1'st group
	 */
	C1,
	
	/**
	 * Stocks from WIG20 - 2'nd group
	 */
	C2,
	
	/**
	 * Remaining stocks from the continuous trading - 1'st group
	 */
	C3,
	
	/**
	 * Remaining stocks from the continuous trading - 2'nd group
	 */
	C4,
	
	/**
	 * Stocks from mWIG40 - 1'st group
	 */
	C5,
	
	/**
	 * Stocks from mWIG40 - 2'nd group
	 */
	C6,
	
	/**
	 * Stocks from sWIG80 - 1'st group
	 */
	C7,
	
	/**
	 * Stocks from sWIG80 - 2'nd group
	 */
	C8,
	
	/**
	 * Rights to shares for companies listed in the continuous system
	 */
	C9,
	
	/**
	 * Treasury bonds
	 */
	CO, 
	
	/**
	 * Investment certificates
	 */
	CI,
	
	/**
	 * EFK certificates
	 */
	CE, 
	
	/**
	 * Structured certificates - quoted in the currency
	 */
	CV,
	
	/**
	 * Futures contracts on indexes
	 */
	CF, 
	
	/**
	 * Futures contracts on exchange rates (currency)
	 */
	CC, 
	
	/**
	 * Futures contracts on stock prices
	 */
	CA,
	
	/**
	 * Option contracts of MiniWig type
	 */
	CM, 
	
	/**
	 * Option contracts on the indexes
	 */
	CY; 
}
