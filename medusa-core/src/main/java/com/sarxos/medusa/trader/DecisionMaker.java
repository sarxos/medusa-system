package com.sarxos.medusa.trader;

import static com.sarxos.medusa.market.Position.LONG;
import static com.sarxos.medusa.market.Position.SHORT;
import static com.sarxos.medusa.market.SignalType.BUY;
import static com.sarxos.medusa.market.SignalType.SELL;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Signal;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.trader.Observer.NullEvent;


/**
 * Decision maker class. Here is decided if I shall buy or sell observed paper.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DecisionMaker implements PriceListener {

	/**
	 * Null event handler.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static interface NullEventHandler {

		/**
		 * Handle null event.
		 * 
		 * @param ne - null event to handle
		 */
		public void handleNull(NullEvent ne);

	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DecisionMaker.class.getSimpleName());

	/**
	 * Trader
	 */
	private Trader trader = null;

	/**
	 * Signals generator.
	 */
	private SignalGenerator<? extends Quote> generator = null;

	/**
	 * Decision listeners (traders).
	 */
	private List<DecisionListener> listeners = new LinkedList<DecisionListener>();

	/**
	 * Current wallet position.
	 */
	private Position position = Position.SHORT;

	/**
	 * Quotes registry used to bind single quote with corresponding historical
	 * data for particular symbol. This objects store lists of quotes for
	 * various symbols.
	 */
	private QuotesRegistry registry = null;

	/**
	 * Null events handler.
	 */
	private NullEventHandler nullHandler = null;

	/**
	 * You have to set trader and signal generatore implicitly when you are
	 * using this constructor.
	 */
	public DecisionMaker() {
	}

	/**
	 * Create Decision maker with signal generator. After price notification
	 * from observer, generator will calculate signal and on its base decision
	 * maker will decide if given paper shall be bought or sell.
	 * 
	 * @param trader - trader instance
	 * @param generator - signal generator
	 */
	public DecisionMaker(Trader trader, SignalGenerator<Quote> generator) {
		this(trader, generator, null);
	}

	/**
	 * Create Decision maker with signal generator. After price notification
	 * from observer, generator will calculate signal and on its base decision
	 * maker will decide if given paper shall be bought or sell.
	 * 
	 * @param trader - trader instance
	 * @param generator - signal generator
	 * @param qr - quotes registry
	 */
	public DecisionMaker(Trader trader, SignalGenerator<Quote> generator, QuotesRegistry qr) {
		this.generator = generator;
		this.setTrader(trader);
		if (qr != null) {
			setQuotesRegistry(qr);
		}
	}

	/**
	 * @return Return trader
	 */
	public Trader getTrader() {
		return trader;
	}

	/**
	 * Set trader.
	 * 
	 * @param trader - trader to set
	 */
	public void setTrader(Trader trader) {
		if (trader == null) {
			throw new IllegalArgumentException("Trader cannot be null");
		}
		this.trader = trader;
		this.addDecisionListener(trader);
	}

	@Override
	public void priceChange(PriceEvent pe) {

		if (pe instanceof NullEvent) {
			handleNull((NullEvent) pe);
			return;
		}

		Paper paper = trader.getPaper();
		if (paper == null) {
			throw new RuntimeException("Paper from trader is null!");
		}

		Quote quote = pe.getQuote();

		bind(quote);

		Signal signal = generator.generate(quote);
		SignalType type = signal.getType();

		boolean buy = position == SHORT && type == BUY;
		boolean sell = position == LONG && type == SELL;

		if (LOG.isInfoEnabled()) {
			LOG.info("Price change " + pe + " signal " + signal);
		}

		if (buy || sell) {
			notifyListeners(new DecisionEvent(this, paper, quote, type));
		} else {
			unbind(quote);
		}
	}

	/**
	 * This method is used only by simulators.
	 * 
	 * @param ne
	 */
	protected void handleNull(NullEvent ne) {
		if (nullHandler != null) {
			LOG.debug("Passing null event to null handler");
			nullHandler.handleNull(ne);
		} else {
			LOG.warn("Null event detected");
		}
	}

	/**
	 * Bind quote with historical data.
	 * 
	 * @param q - quote to bind
	 * @return Quote
	 */
	private Quote bind(Quote q) {

		QuotesRegistry qr = getQuotesRegistry();
		if (qr == null) {
			throw new RuntimeException("Quotes registry cannot eb null");
		}

		List<Quote> quotes = qr.getQuotes(q.getSymbol());
		if (quotes == null) {
			throw new RuntimeException("Quotes in the registry for symbol " + q.getSymbol() + " are null!");
		}

		Quote p = quotes.get(quotes.size() - 1);
		q.setPrev(p);
		p.setNext(q);

		return q;
	}

	/**
	 * Unbind quote from historical data.
	 * 
	 * @param q - quote to unbind
	 * @return Quote
	 */
	private Quote unbind(Quote q) {

		QuotesRegistry qr = getQuotesRegistry();
		if (qr == null) {
			throw new RuntimeException("Quotes registry cannot eb null");
		}

		List<Quote> quotes = qr.getQuotes(q.getSymbol());
		Quote p = quotes.get(quotes.size() - 1);
		q.setPrev(null);
		p.setNext(null);

		return q;
	}

	/**
	 * Notify all listeners about price change.
	 * 
	 * @param de - decision event (buy or sell paper)
	 */
	protected void notifyListeners(DecisionEvent de) {

		if (LOG.isDebugEnabled()) {
			LOG.debug(
				"Notifying decision listeneres. Number of listeners to " +
				"notify is " + listeners.size());
		}

		DecisionListener listener = null;
		ListIterator<DecisionListener> i = listeners.listIterator();

		while (i.hasNext()) {
			listener = i.next();
			try {
				listener.decisionChange(de);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return Decision listeners array.
	 */
	public DecisionListener[] getDecisionListeners() {
		return listeners.toArray(new DecisionListener[listeners.size()]);
	}

	/**
	 * 
	 * @param listener
	 * @return true if listener was added or false if it is already on the list
	 */
	public boolean addDecisionListener(DecisionListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
			return true;
		}
		return false;
	}

	/**
	 * Remove particular decision listener.
	 * 
	 * @param listener - decision listener to remove
	 * @return true if listener list contained specified element
	 */
	public boolean removeDecisionListener(DecisionListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Set signal generator.
	 * 
	 * @param generator - new generator to set.
	 */
	public void setSignalGenerator(SignalGenerator<Quote> generator) {
		this.generator = generator;
	}

	/**
	 * @return Signal generator.
	 */
	public SignalGenerator<? extends Quote> getSignalGenerator() {
		return generator;
	}

	/**
	 * @return Quotes registry used to bind single quote with historical data
	 */
	public QuotesRegistry getQuotesRegistry() {
		if (registry == null) {
			QuotesRegistry.getInstance();
		}
		return registry;
	}

	/**
	 * Set new quotes registry used to bind single quote with corresponding
	 * historical data.
	 * 
	 * @param registry - new quotes registry to set
	 */
	public void setQuotesRegistry(QuotesRegistry registry) {
		if (registry == null) {
			throw new IllegalArgumentException("Quotes registry cannot be null");
		}
		this.registry = registry;
	}

	/**
	 * @return Will return actually set null handler
	 */
	public NullEventHandler getNullHandler() {
		return nullHandler;
	}

	/**
	 * Set new null handler object
	 * 
	 * @param nh - new null handler object to set
	 */
	public void setNullHandler(NullEventHandler nh) {
		this.nullHandler = nh;
	}
}
