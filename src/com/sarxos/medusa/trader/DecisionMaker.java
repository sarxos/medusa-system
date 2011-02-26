package com.sarxos.medusa.trader;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Signal;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;


/**
 * Decision maker class. Here is decided if I shall buy or sell observed paper.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DecisionMaker implements PriceListener {

	/**
	 * Price observer.
	 */
	private Observer observer = null;

	/**
	 * Signals generator.
	 */
	private SignalGenerator<Quote> generator = null;

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
	private QuotesRegistry registry = QuotesRegistry.getInstance();

	/**
	 * Protected constructor - used somewhere?
	 */
	protected DecisionMaker() {
	}

	/**
	 * Create Decision maker with given observer and signal generator. After
	 * price notification from observer, generator will calculate signal and on
	 * its base decision maker will decide if given paper shall be bought or
	 * sell.
	 * 
	 * @param observer
	 * @param generator
	 */
	public DecisionMaker(Observer observer, SignalGenerator<Quote> generator) {
		this.observer = observer;
		this.observer.addPriceListener(this);
		this.generator = generator;
	}

	@Override
	public void priceChange(PriceEvent pe) {

		Wallet wallet = Wallet.getInstance();
		Paper paper = wallet.getPaper(observer.getSymbol());
		Quote quote = pe.getQuote();

		quote = bind(quote, paper.getSymbol());

		Signal signal = generator.generate(quote);
		DecisionEvent de = new DecisionEvent(this, paper, signal.getType());

		notifyListeners(de);
	}

	/**
	 * Bind quote with historical data.
	 * 
	 * @param q - quote to bind
	 * @param symbol - symbol to lookup in the quotes registry
	 * @return Quote
	 */
	private Quote bind(Quote q, Symbol symbol) {
		List<Quote> quotes = registry.getQuotes(symbol);
		Quote p = quotes.get(quotes.size() - 1);
		q.setPrev(p);
		p.setNext(q);
		return q;
	}

	/**
	 * Notify all listeners about price change.
	 * 
	 * @param de - decision event (buy or sell paper)
	 */
	protected void notifyListeners(DecisionEvent de) {

		DecisionListener listener = null;
		ListIterator<DecisionListener> i = listeners.listIterator();

		while (i.hasNext()) {
			try {
				listener = i.next();
				listener.decisionChange(de);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Notify all listeners about price change.
	 * 
	 * @param de - decision event (buy or sell paper)
	 */
	protected void notifyListeners(PositionEvent pe) {

		DecisionListener listener = null;
		ListIterator<DecisionListener> i = listeners.listIterator();

		while (i.hasNext()) {
			try {
				listener = i.next();
				listener.positionChange(pe);
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
	 * @return Paper observer
	 */
	public Observer getObserver() {
		return observer;
	}

	/**
	 * Set new price observer. If other observer has been set, decision maker
	 * object will be removed from its listeners.
	 * 
	 * @param observer - new observer to set
	 */
	public void setObserver(Observer observer) {
		if (this.observer != null) {
			this.observer.removePriceListener(this);
		}
		this.observer = observer;
		this.observer.addPriceListener(this);
	}

	/**
	 * Set signal generator.
	 * 
	 * @param generator - new generator to set.
	 */
	public void setGenerator(SignalGenerator<Quote> generator) {
		this.generator = generator;
	}

	/**
	 * @return Signal generator.
	 */
	public SignalGenerator<Quote> getGenerator() {
		return generator;
	}

	/**
	 * @return Current position being set (long, short).
	 */
	public Position getCurrentPosition() {
		return position;
	}

	/**
	 * Set current position (long or short).
	 * 
	 * @param position - position type
	 */
	public void setCurrentPosition(Position position) {
		if (position == null) {
			throw new IllegalArgumentException("Position cannot be null");
		}

		PositionEvent pe = null;
		if (position != this.position) {
			pe = new PositionEvent(this, this.position, position);
		}

		this.position = position;

		if (pe != null) {
			notifyListeners(pe);
		}
	}

	/**
	 * @return Quotes registry used to bind single quote with historical data
	 */
	protected QuotesRegistry getRegistry() {
		return registry;
	}

	/**
	 * Set new quotes registry used to bind single quote with corresponding
	 * historical data.
	 * 
	 * @param registry - new quotes registry to set
	 */
	protected void setRegistry(QuotesRegistry registry) {
		if (registry == null) {
			throw new IllegalArgumentException("Quotes registry cannot be null");
		}
		this.registry = registry;
	}
}
