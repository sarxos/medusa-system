package com.sarxos.medusa.market;

/**
 * Possible order status
 * 
 * @author Bartosz Firyn (SarXos)
 */
public enum OrderStatus {

	/**
	 * Order is active
	 */
	ACTIVE,

	/**
	 * While cancellation
	 */
	CANCELLATION,

	/**
	 * Canceled
	 */
	CANCELED,

	/**
	 * Order is realized
	 */
	REALIZED,

	/**
	 * Order is closed
	 */
	CLOSED;

}
