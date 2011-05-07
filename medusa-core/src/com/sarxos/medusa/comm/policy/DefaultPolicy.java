package com.sarxos.medusa.comm.policy;

import com.sarxos.medusa.comm.MessagingPolicy;
import com.sarxos.medusa.market.Symbol;


/**
 * This policy will always allow Medusa to send message.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DefaultPolicy extends MessagingPolicy {

	/**
	 * Empty constructor have to be specified.
	 */
	public DefaultPolicy() {
	}

	@Override
	public boolean allows(Symbol symbol) {
		return true;
	}

	@Override
	public void sent(Symbol symbol) {
		// do nothing
	}
}
