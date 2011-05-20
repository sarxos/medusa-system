package com.sarxos.medusa.data.persistence;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.trader.Trader;


/**
 * Store persistent objects in the blocking queue and save them.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class PersistenceWriter implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(PersistenceWriter.class.getSimpleName());

	/**
	 * Persistence provider.
	 */
	private PersistenceProvider provider = null;

	/**
	 * Persistence objects storage where each of them waits to be persistent.
	 */
	private BlockingQueue<Trader> queue = null;

	/**
	 * Create persistence writer on top of persistence provider.
	 * 
	 * @param provider - persistence provider to use under writer
	 */
	public PersistenceWriter(PersistenceProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("Persistence provider cannot be null");
		}
		this.provider = provider;
		this.queue = new LinkedBlockingQueue<Trader>();
	}

	public PersistenceWriter() {
		this(DBDAO.getInstance());
	}

	@Override
	public void run() {

		Trader trader = null;

		while (true) {

			trader = null;
			try {
				trader = queue.take();
			} catch (InterruptedException e) {
				LOG.error("Interrupt exception", e);
			}

			if (trader != null) {
				LOG.info("Saving trader " + trader);
				try {
					provider.saveTrader(trader);
				} catch (PersistenceException e) {
					LOG.error("Cannot save trader", e);
				}
			}
		}
	}

	/**
	 * Add trader to the list of elements waiting for persistence.
	 * 
	 * @param trader - trader to write
	 */
	public void write(Trader trader) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Putting trader " + trader + " into persistence queue");
		}

		try {
			queue.put(trader);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
