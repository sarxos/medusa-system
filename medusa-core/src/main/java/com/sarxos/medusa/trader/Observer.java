package com.sarxos.medusa.trader;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.LackOfQuoteException;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.Providers;
import com.sarxos.medusa.provider.RealTimeProvider;


/**
 * Stock symbol observer. Default price check interval is 30s.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Observer implements Runnable {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Observer.class.getSimpleName());

	/**
	 * Simple class to notify about occurring null quotes events.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static class NullEvent extends PriceEvent {

		private static final long serialVersionUID = -9214937855786805177L;

		public NullEvent(Observer observer, double previous, double current, Quote quote) {
			super(observer, previous, current, quote);
		}
	}

	/**
	 * Possible observer states. Possible transitions are:
	 * <ul>
	 * <li>STOPPED =&gt; RUNNING</li>
	 * <li>RUNNING =&gt; PAUSED</li>
	 * <li>RUNNING =&gt; STOPPED</li>
	 * <li>PAUSED =&gt; STOPPED</li>
	 * <li>PAUSED =&gt; RUNNING</li>
	 * </ul>
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static enum State {
		RUNNIG, PAUSED, STOPPED;
	}

	/**
	 * Thread group for all observer runners.
	 */
	private static ThreadGroup group = new ThreadGroup("Observers");

	/**
	 * Stock data provider.
	 */
	private RealTimeProvider provider = null;

	/**
	 * Runner for each observer;
	 */
	private Thread runner = null;

	/**
	 * Runner state.
	 */
	private State state = State.STOPPED;

	/**
	 * Observed symbol.
	 */
	private Symbol symbol = null;

	/**
	 * Default check interval (30s).
	 */
	private long interval = 20000;

	/**
	 * Last observed symbol price.
	 */
	private double price = -1.;

	/**
	 * Price listeners list.
	 */
	private List<PriceListener> listeners = new LinkedList<PriceListener>();

	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Create observer for symbol.
	 * 
	 * @param symbol - observed symbol
	 */
	public Observer(Symbol symbol) {
		this(symbol, null);
	}

	/**
	 * Create new data observer. If quotes provider is null, then it will be set
	 * to default provider (taken from the Medusa configuration) after start.
	 * 
	 * @param provider - quotes real time data provider
	 * @param symbol - observed symbol
	 */
	public Observer(Symbol symbol, RealTimeProvider provider) {
		this.provider = provider;
		this.observe(symbol);
	}

	/**
	 * Observe symbol.
	 * 
	 * @param symbol - stock symbol
	 */
	public void observe(Symbol symbol) {
		if (provider != null) {
			if (provider.canServe(symbol)) {
				this.symbol = symbol;
			} else {
				String pname = provider.getClass().getName();
				String sym = symbol.toString();
				String msg = String.format("Provider %s cannot serve %s data", pname, sym);
				throw new IllegalArgumentException(msg);
			}
		} else {
			this.symbol = symbol;
		}
	}

	/**
	 * @return Return check interval in seconds
	 */
	public long getInterval() {
		return interval / 1000;
	}

	/**
	 * Set check interval in seconds.
	 * 
	 * @param interval
	 */
	public void setInterval(int interval) {
		if (interval < 0) {
			throw new IllegalArgumentException("Check interval in seconds must be positive");
		}
		this.interval = interval * 1000;
	}

	/**
	 * Stop observation. After calling this method observation will be stopped,
	 * but observer can be run once again in any moment.
	 */
	public void stop() {
		if (state != State.RUNNIG && state != State.PAUSED) {
			throw new IllegalStateException("Cannot stop not running or paused observer");
		}
		this.state = State.STOPPED;
		try {
			getRunner().interrupt();
			getRunner().join();
		} catch (InterruptedException e) {
			LOG.debug(symbol + " observer stop has been interrupted");
		}
		LOG.info(getSymbol() + " observer has been stopped");
	}

	/**
	 * Pause observation.
	 */
	public void pause() {
		if (state != State.RUNNIG) {
			throw new IllegalStateException("Cannot pause not running observer");
		}
		this.state = State.PAUSED;
		lock.lock();
		LOG.info(getSymbol() + " observer has been paused");
	}

	/**
	 * Resume observation.
	 */
	public void resume() {
		if (state != State.PAUSED) {
			throw new IllegalStateException("Cannot resume not paused observer");
		}
		this.state = State.RUNNIG;
		lock.unlock();
		LOG.info(getSymbol() + " observer has been resumed");
	}

	/**
	 * Start observation.
	 */
	public void start() {
		if (symbol == null) {
			throw new IllegalStateException("Cannot start observer when symbol is not set");
		}
		if (state != State.STOPPED) {
			throw new IllegalStateException(
				"This observer has been already started - cannot start it " +
				"again");
		}
		if (provider == null) {
			provider = Providers.getRealTimeProvider();
		}

		state = State.RUNNIG;
		getRunner().start();
	}

	/**
	 * @return Return real time data provider.
	 */
	public RealTimeProvider getProvider() {
		return provider;
	}

	/**
	 * Set new real time data provider.
	 * 
	 * @param provider - data provider to set
	 */
	public void setProvider(RealTimeProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("Provider cannot be null");
		}
		if (symbol != null && !provider.canServe(symbol)) {
			String pname = provider.getClass().getName();
			String sym = symbol.toString();
			String msg = String.format("Provider %s cannot serve %s data", pname, sym);
			throw new IllegalArgumentException(msg);
		}
		this.provider = provider;
	}

	@Override
	public void run() {

		if (LOG.isInfoEnabled()) {
			LOG.info(getSymbol() + " observer has been started");
		}

		do {
			if (state == State.STOPPED) {
				break;
			} else {
				lock.lock();
				try {
					runOnce();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				} finally {
					lock.unlock();
				}
				if (interval > 0) {

					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						LOG.debug(symbol + " observer sleep has been interrupted");
					}
				}
			}
		} while (state != State.STOPPED);
	}

	/**
	 * Run once per observer tick. If price is different in comparison previous
	 * one, notify price listeners.
	 * 
	 * @throws ProviderException
	 */
	protected void runOnce() throws ProviderException {

		Quote q = null;

		boolean error = false;
		int attempts = 0;
		do {
			try {
				q = provider.getQuote(symbol);
				error = false;
				break;
			} catch (LackOfQuoteException e) {
				error = true;
				// this situation may occurs when there was no trade in given
				// instrument within particular day
				LOG.warn("Due to lack of quotes Observer will sleep for 5 minutes. " + e.getMessage());
				try {
					// sleep for 5 minutes
					Thread.sleep(1000 * 60 * 5);
				} catch (InterruptedException e1) {
					LOG.error(e.getMessage(), e);
				}
			}
		} while (error && attempts++ < 5);

		if (q == null) {
			notifyListeners(new NullEvent(this, price, price, null));
		} else {
			double tmp = q.getClose();
			if (tmp != price && price != -1) {
				notifyListeners(new PriceEvent(this, price, tmp, q));
			}
			price = tmp;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(getSymbol() + " observer read quote " + q);
		}
	}

	/**
	 * @return Thread group for observation runners.
	 */
	public static ThreadGroup getRunnersGroup() {
		return group;
	}

	/**
	 * @return Return last observed price or -1 if no price has been observed.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Notify all listeners about price change.
	 * 
	 * @param pe - price event
	 */
	protected void notifyListeners(PriceEvent pe) {

		PriceListener listener = null;
		ListIterator<PriceListener> i = listeners.listIterator();

		while (i.hasNext()) {
			listener = i.next();
			try {
				listener.priceChange(pe);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return Price listeners array.
	 */
	public PriceListener[] getPriceListeners() {
		return listeners.toArray(new PriceListener[listeners.size()]);
	}

	/**
	 * 
	 * @param listener
	 * @return true if listener was added or false if it is already on the list
	 */
	public boolean addPriceListener(PriceListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
			return true;
		}
		return false;
	}

	/**
	 * Remove particular price listener.
	 * 
	 * @param listener - price listener to remove
	 * @return true if listener list contained specified element
	 */
	public boolean removePriceListener(PriceListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * @return Runnable runner.
	 */
	public Thread getRunner() {
		if (runner == null) {
			runner = new Thread(group, this, symbol.toString() + "Observer");
			runner.setDaemon(true);
		}
		return runner;
	}

	/**
	 * @return Observer state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * @return Observed symbol.
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getSymbol() + "]";
	}
}
