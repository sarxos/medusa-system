package com.sarxos.medusa;

import java.io.File;
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
	 * The goal of this class is to perform periodic SLF4J update when
	 * configuration file has been changed.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected static class LogConfigurationUpdater implements Runnable {

		private static final String LOG_CFG_PATH = "data/logback.xml";

		@Override
		public void run() {

			LOG.info("Log configuration updater has been started");

			long updated = 0;
			long tmp = 0;

			while (true) {

				File cfg = new File(LOG_CFG_PATH);
				tmp = cfg.lastModified();

				if (tmp != updated) {
					configure();
					updated = tmp;
				}

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Configure SLF4J.
		 */
		public static void configure() {

			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			File cfg = new File(LOG_CFG_PATH);

			try {
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(lc);

				// the context was probably already configured by default
				// configuration rules, so it needs to be reset
				lc.reset();

				configurator.doConfigure(cfg);

			} catch (JoranException e) {
				e.printStackTrace();
			}
		}
	}

	// configure loggers
	static {
		LogConfigurationUpdater.configure();
	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MedusaDaemon.class.getSimpleName());

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

	public MedusaDaemon() {
		super("Traders Runner");
	}

	protected void startOnce() {

		super.start();

		LOG.info("Executing Medusa daemon");

		// ensure mysql running
		LOG.info("Checking MySQL");
		MySQLRunner.getInstance().runMySQL();

		// initially reconcile quotes data
		new ReconcileQuotesDataTask().run();

		// start traders
		DBDAO dao = null;

		while (running.get()) {
			try {

				List<Trader> tmp = null;
				Iterator<Trader> it = null;

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

					Trader trader = it.next();
					boolean found = false;

					for (Trader t : traders) {
						String tn1 = t.getName();
						String tn2 = trader.getName();
						if (tn1 != null && tn2 != null) {
							found = tn1.equals(tn2);
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

		// start logger configuration updater
		Thread updater = new Thread(new LogConfigurationUpdater(), "LogCfgUpdater");
		updater.setDaemon(true);
		updater.start();

		// start Medusa
		MedusaDaemon r = new MedusaDaemon();
		r.startTraders();
		try {
			r.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
