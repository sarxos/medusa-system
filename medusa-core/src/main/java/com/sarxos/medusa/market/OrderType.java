package com.sarxos.medusa.market;

/**
 * Order type.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public enum OrderType {

	/**
	 * Order with limit
	 */
	LIMIT,

	/**
	 * Order with the current market price.
	 */
	PCR,
	
	/**
	 * Order with the opening price
	 */
	PCRO,

	/**
	 * Order with any price
	 */
	PKC;
}
