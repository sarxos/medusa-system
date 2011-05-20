package com.sarxos.medusa.comm;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.SignalType;


public interface MessagesBroker {

	/**
	 * Acknowledge player about trader decision and receive his response. This
	 * method will return true if player has confirmed, or false if player
	 * refused to acknowledge.
	 * 
	 * @param paper - paper to buy/sell
	 * @param type - buy/sell action
	 * @return true if player has confirmed, or false if player refused to
	 * @throws MessagingException
	 */
	public abstract boolean acknowledge(Paper paper, SignalType type) throws MessagingException;

}