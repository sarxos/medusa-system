package com.sarxos.medusa;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sarxos.medusa.db.DBDAO;
import com.sarxos.medusa.trader.Trader;


/**
 * Medusa daemon. This is main class for Medusa service.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MedusaDaemon extends Thread {

	/**
	 * VM shutdown handler. The goal of this class is to remove lock after
	 * graceful exit.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class ShutdownHandler extends Thread {

		@Override
		public void run() {
			File lock = new File(lockPath);
			if (lock.exists() && !lock.delete()) {
				lock.deleteOnExit();
			}
		}
	}

	/**
	 * Traders executor.
	 */
	private ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * Currently working traders list.
	 */
	private List<Trader> traders = new LinkedList<Trader>();

	/**
	 * Is runner running?
	 */
	private AtomicBoolean running = new AtomicBoolean(false);

	/**
	 * Lock file path.
	 */
	private String lockPath = "data/medusa.lock";

	public MedusaDaemon() {
		super("Traders Runner");
	}

	protected void startOnce() {

		super.start();

		Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

		MySQLRunner.getInstance().runMySQL();

		DBDAO dao = null;
		List<Trader> tmp = null;
		Iterator<Trader> it = null;
		Trader trader = null;
		String a, b;
		boolean found;

		if (!createLock()) {
			System.out.println(
				"Lock already exists. In case of problems please " +
				"try to remove " + lockPath + " file"
			);
			return;
		}

		while (running.get()) {
			try {
				try {
					dao = DBDAO.getInstance();
					tmp = dao.getTraders();
					if (tmp != null) {
						it = tmp.iterator();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				while (it != null && it.hasNext()) {

					trader = it.next();
					found = false;

					for (Trader t : traders) {
						a = t.getName();
						b = trader.getName();
						if (a != null && b != null) {
							found = a.equals(b);
							if (found) {
								break;
							}
						}
					}

					if (found) {
						continue;
					}

					System.out.println("Starting trader " + trader);
					try {
						executor.execute(trader);
						traders.add(trader);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create lock.
	 * 
	 * @return true if lock has been created, false otherwise
	 */
	protected boolean createLock() {
		File lock = new File(lockPath);
		if (!lock.exists()) {
			boolean ok = false;
			try {
				ok = lock.createNewFile();
				if (ok) {
					lock.deleteOnExit();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ok;
		}
		return false;
	}

	public void stopTraders() {
		running.compareAndSet(true, false);
	}

	public void startTraders() {
		if (running.compareAndSet(false, true)) {
			startOnce();
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	public static void main(String[] args) {
		MedusaDaemon r = new MedusaDaemon();
		r.startTraders();
		try {
			r.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
