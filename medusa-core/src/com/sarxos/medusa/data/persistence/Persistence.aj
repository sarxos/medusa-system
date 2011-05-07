package com.sarxos.medusa.data.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.trader.Trader;


/**
 * Persistence aspect - handle specific values changes and persist
 * particular elements. 
 * 
 * @author Bartosz Firyn (SarXos)
 */
public aspect Persistence {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Persistence.class.getSimpleName());
	
	/**
	 * Persistence writer runnable.
	 */
	private PersistenceWriter writer = new PersistenceWriter();
	
	pointcut dbdao():
		execution(* DBDAO.*(..)) &&
		within(DBDAO);
	
	/**
	 * Persistent creation.
	 * 
	 * @param t - created trader
	 */
	pointcut pcreate(Trader t):
		execution(@Persistent Trader.new(..)) &&
		within(@Persistent Trader) &&
		this(t) && !cflowbelow(dbdao());
	
	/**
	 * Persistent modification.
	 * 
	 * @param t - modified trader
	 */
	pointcut pmod(Trader t):
		set(@Persistent * *) &&
		withincode(* Trader.*(..)) &&
		within(@Persistent Trader) &&
		this(t) && !cflowbelow(dbdao());
	
	/**
	 * Constructor.
	 */
	public Persistence() {
		Thread runner = new Thread(writer, PersistenceWriter.class.getSimpleName());
		runner.setDaemon(true);
		runner.start();
		LOG.debug("Persistence writer has been started");
	}

	/**
	 * Invoked after trader creation.
	 * 
	 * @param t - trader to be persisted
	 */
	after(Trader t): pcreate(t) {
		LOG.debug("Persistent creation advice");
		persist(t);
	}

	/**
	 * Invoked after trader modification.
	 * 
	 * @param t - trader to be persisted
	 */
	after(Trader t): pmod(t) {
		LOG.debug("Persistent modification advice");
		persist(t);
	}
	
	/**
	 * Persist given trader.
	 * 
	 * @param t - trader to persist
	 */
	private void persist(Trader t) {
		try {
			writer.write(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

