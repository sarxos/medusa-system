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
	 * Order with the opening price
	 */
	PCR,

	/**
	 * Order with any price
	 */
	PKC;
}
