package com.sarxos.medusa.trader;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.sarxos.medusa.data.Providers;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.RealTimeProvider;


/**
 * Stock symbol observer. Default price check interval is 30s.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Observer implements Runnable {

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
	private long interval = 1000;

	/**
	 * Last observed symbol price.
	 */
	private double price = -1.;

	/**
	 * Price listeners list.
	 */
	private List<PriceListener> listeners = new LinkedList<PriceListener>();

	/**
	 * This constructor shall never be used, it is required only by JAXB
	 * reflection mechanism.
	 */
	protected Observer() {
	}

	public Observer(Symbol symbol) {
		this(null, symbol);
	}

	/**
	 * Create new data observer.
	 * 
	 * @param provider - real time data provider
	 * @param symbol - observed symbol
	 */
	public Observer(RealTimeProvider provider, Symbol symbol) {
		if (provider == null) {
			provider = Providers.getDefaultRealTimeDataProvider();
		}
		this.provider = provider;
		this.observe(symbol);
	}

	/**
	 * Observe symbol.
	 * 
	 * @param symbol - stock symbol
	 */
	public void observe(Symbol symbol) {
		if (provider.canServe(symbol)) {
			this.symbol = symbol;
		} else {
			throw new IllegalArgumentException(
				"Data provider " + provider.getClass().getName() +
				" " + "cannot serve " + symbol + " data");
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
			throw new IllegalArgumentException("Check interval in seconds must be non-negative");
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
	}

	/**
	 * Pause observation.
	 */
	public void pause() {
		if (state != State.RUNNIG) {
			throw new IllegalStateException("Cannot pause not running observer");
		}
		this.state = State.PAUSED;
		try {
			runner.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resume observation.
	 */
	public void resume() {
		if (state != State.PAUSED) {
			throw new IllegalStateException("Cannot resume not paused observer");
		}
		this.state = State.RUNNIG;
	}

	/**
	 * Start observation.
	 */
	public void start() {
		if (symbol == null) {
			throw new IllegalStateException("Cannot start observer when symbol is not set");
		}
		if (runner == null) {
			runner = getRunner();
		}
		if (state == State.RUNNIG) {
			throw new IllegalStateException("Observer is already started - cannot start it again");
		}

		state = State.RUNNIG;
		runner.start();
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
		this.provider = provider;
	}

	@Override
	public void run() {
		do {
			if (state == State.STOPPED) {
				break;
			} else {
				try {
					runOnce();
				} catch (ProviderException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} while (true);
	}

	/**
	 * Run once per observer tick. If price is different in comparison previous
	 * one, notify price listeners.
	 * 
	 * @throws ProviderException
	 */
	protected void runOnce() throws ProviderException {

		Quote q = provider.getQuote(symbol);

		if (q == null) {
			stop();
			System.out.println(
				"No quotes available - shutting down " + getSymbol() + " " +
				"observer");
			return;
		}

		double tmp = q.getClose();
		if (tmp != price && price != -1) {
			notifyListeners(new PriceEvent(this, price, tmp, q));
		}

		price = tmp;
	}

	/**
	 * @return Thread group for observation runners.
	 */
	public static ThreadGroup getRunnersGroup() {
		return group;
	}

	/**
	 * @return Return last observed price or -1 if no price has been observed
	 *         yet.
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
}
