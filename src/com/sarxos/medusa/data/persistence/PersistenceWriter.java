package com.sarxos.medusa.data.persistence;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.trader.Trader;


/**
 * Store persistent objects in the blocking queue and save them.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class PersistenceWriter implements Runnable {

	/**
	 * Persistence provider.
	 */
	private PersistenceProvider provider = null;

	/**
	 * Persistence objects storage where each of them waits to be persistent.
	 */
	private BlockingQueue<Trader> queue = null;

	public PersistenceWriter() {
		provider = DBDAO.getInstance();
		queue = new LinkedBlockingQueue<Trader>();
	}

	@Override
	public void run() {

		Trader trader = null;

		while (true) {

			trader = null;
			try {
				trader = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (trader != null) {
				try {
					provider.saveTrader(trader);
				} catch (PersistenceException e) {
					e.printStackTrace();
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
		try {
			queue.put(trader);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
