package com.sarxos.medusa.comm.policy;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ThrottlingPolicy.class.getSimpleName());

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

		if (LOG.isInfoEnabled()) {
			long expire = symbols.getExpiration() / (1000 * 60 * 60);
			LOG.info(
				"Symbol " + symbol + " has been added to throttling policy " +
				"messaging list. Throttling fo it will expire in next " +
				expire + " hours.");
		}
	}
}
