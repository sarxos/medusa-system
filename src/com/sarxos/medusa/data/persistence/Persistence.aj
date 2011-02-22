package com.sarxos.medusa.data.persistence;

import com.sarxos.medusa.trader.Trader;


/**
 * Persistence aspect - handle specific values changes and persist
 * particular elements. 
 * 
 * @author Bartosz Firyn (SarXos)
 */
public aspect Persistence {

	private PersistenceWriter writer = new PersistenceWriter();

	/**
	 * Persistent creation.
	 * 
	 * @param t - created trader
	 */
	pointcut pcreate(Trader t):
		execution(@Persistent Trader.new(..)) &&
		within(@Persistent Trader) &&
		this(t);
	
	/**
	 * Persistent modification.
	 * 
	 * @param t - modified trader
	 */
	pointcut pmod(Trader t):
		set(@Persistent * *) &&
		withincode(* Trader.*(..)) &&
		within(@Persistent Trader) &&
		this(t);

	/**
	 * Constructor.
	 */
	public Persistence() {
		Thread runner = new Thread(writer, PersistenceWriter.class.getSimpleName());
		runner.setDaemon(true);
		runner.start();
	}

	/**
	 * Invoked after trader creation or modification.
	 * 
	 * @param t - trader to be persisted
	 */
	after(Trader t): pcreate(t) || pmod(t) {
		try {
			writer.write(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

