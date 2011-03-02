package com.sarxos.medusa.comm.policy;

import java.util.concurrent.TimeUnit;

import com.sarxos.medusa.comm.MessagingPolicy;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.util.ExpireSet;


/**
 * This policy will allow Medusa to send message for given symbol only once per
 * 12 hours.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ThrottlingPolicy extends MessagingPolicy {

	/**
	 * Each symbol expires in 12 hours
	 */
	private ExpireSet<Symbol> symbols = new ExpireSet<Symbol>(12, 1, TimeUnit.HOURS);

	/**
	 * Empty constructor have to be specified.
	 */
	public ThrottlingPolicy() {
	}

	@Override
	public boolean allows(Symbol symbol) {
		if (symbols.contains(symbol)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void sent(Symbol symbol) {
		symbols.add(symbol);
	}
}
