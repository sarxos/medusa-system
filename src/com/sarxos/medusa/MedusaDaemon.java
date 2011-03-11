package com.sarxos.medusa;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.data.MySQLRunner;
import com.sarxos.medusa.task.ReconcileQuotesDataTask;
import com.sarxos.medusa.trader.Trader;


/**
 * Medusa daemon. This is main class for Medusa service.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MedusaDaemon extends Thread {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MedusaDaemon.class.getSimpleName());

	/**
	 * VM shutdown handler. The goal of this class is to remove lock after
	 * graceful exit.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class ShutdownHandler extends Thread {

		@Override
		public void run() {
			// TODO remoe this class - this is only w/a
			LOG.info("Removing lock");
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

		LOG.info("Executing Medusa daemon");

		Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

		LOG.info("Checking MySQL");
		MySQLRunner.getInstance().runMySQL();

		DBDAO dao = null;
		List<Trader> tmp = null;
		Iterator<Trader> it = null;
		Trader trader = null;
		String a, b;
		boolean found;

		if (!createLock()) {
			LOG.error(
				"Lock already exists. In case of problems please " +
				"try to remove " + lockPath + " file"
			);
			return;
		}

		// initially reconcile quotes data
		new ReconcileQuotesDataTask().run();

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

					LOG.info("Starting trader " + trader);
					try {
						executor.execute(trader);
						traders.add(trader);
					} catch (Exception e) {
						e.printStackTrace();
					}

					Thread.sleep(1000);
				}

				try {
					Thread.sleep(60000);
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

	protected static void configureLoggers() {

		// assume SLF4J is bound to logback in the current environment
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);

			// the context was probably already configured by default
			// configuration rules, so it needs to be reset
			lc.reset();
			configurator.doConfigure(new File("data/logback.xml"));
		} catch (JoranException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		configureLoggers();

		MedusaDaemon r = new MedusaDaemon();
		r.startTraders();
		try {
			r.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
