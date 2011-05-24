package com.sarxos.medusa.trader;

import static com.sarxos.medusa.market.SignalType.BUY;
import static com.sarxos.medusa.market.SignalType.SELL;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.comm.DefaultMessagesBroker;
import com.sarxos.medusa.comm.MessagesBroker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.Providers;
import com.sarxos.medusa.provider.RealTimeProvider;


/**
 * Trader class. It is designed to handle decision events from decision maker,
 * send notification/question and buy or sell given paper.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public abstract class Trader implements DecisionListener, Runnable, PriceListener {

	/**
	 * Workman thread. It will move price events from queue to the decision
	 * maker for further processing.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected static class Workman extends Thread {

		Trader trader = null;

		public Workman(Trader trader) {
			if (trader == null) {
				throw new IllegalArgumentException("Trader for workman cannot be null");
			}
			this.trader = trader;
			this.setDaemon(true);
			this.setName(trader.getName() + "[worker]");
		}

		@Override
		public void run() {
			super.run();
			do {
				// transfer price event
				BlockingQueue<PriceEvent> queue = trader.getQueue();
				DecisionMaker de = trader.getDecisionMaker();
				try {
					de.priceChange(queue.take());
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			} while (true);
		}

	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Trader.class.getSimpleName());

	/**
	 * Set of signal types to be acknowledged by player.
	 */
	protected static final Set<SignalType> NOTIFICATIONS = Collections.unmodifiableSet(EnumSet.of(BUY, SELL));

	/**
	 * Decision maker (encapsulate decision logic).
	 */
	private DecisionMaker decisionMaker = null;

	/**
	 * Signal generator to use.
	 */
	private SignalGenerator<Quote> siggen = null;

	/**
	 * Real time data provider.
	 */
	private RealTimeProvider provider = null;

	/**
	 * Trader name.
	 */
	private final String name;

	/**
	 * Current position (long, short).
	 */
	private Position position = null;

	/**
	 * Trader's paper quantity.
	 */
	private int quantity = 0;

	/**
	 * Trader's paper desired quantity (how many papers I want buy)
	 */
	private int desired = 0;

	/**
	 * Messaging broker.
	 */
	private MessagesBroker broker = null;

	/**
	 * Paper to trade.
	 */
	private Paper paper = null;

	/**
	 * Paper observer.
	 */
	private Observer observer = null;

	/**
	 * Price events queue.
	 */
	private BlockingQueue<PriceEvent> events = new LinkedBlockingQueue<PriceEvent>();

	/**
	 * Price events workman.
	 */
	private Workman workman = null;

	/**
	 * Trader constructor.
	 * 
	 * @param name - trader name
	 * @param siggen - signal generator
	 * @param paper - paper to observe
	 */
	public Trader(SignalGenerator<Quote> siggen, Symbol symbol) {
		this(null, siggen, symbol, null);
	}

	/**
	 * Trader constructor.
	 * 
	 * @param name - trader name
	 * @param siggen - signal generator
	 * @param paper - paper to observe
	 */
	public Trader(String name, SignalGenerator<Quote> siggen, Symbol symbol) {
		this(null, siggen, symbol, null);
	}

	public Trader(String name, SignalGenerator<Quote> siggen, Symbol symbol, RealTimeProvider provider) {
		if (siggen == null) {
			throw new IllegalArgumentException("Signal generator cannot be null");
		}
		if (paper == null) {
			throw new IllegalArgumentException("Paper cannot be null");
		}
		this.name = name;
		this.siggen = siggen;
		this.provider = provider;
		this.paper = new Paper(symbol);
		this.init();
	}

	/**
	 * Initialize trader
	 */
	protected void init() {

		decisionMaker = new DecisionMaker(this, siggen);
		decisionMaker.addDecisionListener(this);

		observer = new Observer(paper.getSymbol(), provider);
		observer.addPriceListener(this);

		try {
			broker = new DefaultMessagesBroker();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set new position
	 * 
	 * @param p - new position to set
	 */
	public void setPosition(Position p) {
		if (p == null) {
			throw new IllegalArgumentException("Position cannot be null");
		}
		this.position = p;
	}

	/**
	 * @return Return current position (long, short)
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * @return Decision maker
	 */
	public DecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	/**
	 * @return Price observer
	 */
	public Observer getObserver() {
		return observer;
	}

	/**
	 * @return Observed symbol (e.g. KGH, BRE)
	 */
	public Symbol getSymbol() {
		return paper.getSymbol();
	}

	/**
	 * @return Signal generator class name
	 */
	public String getGeneratorClassName() {
		return siggen.getClass().getName();
	}

	/**
	 * @return Signal generator
	 */
	public SignalGenerator<? extends Quote> getGenerator() {
		return siggen;
	}

	/**
	 * @return Trader's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Start trade.
	 * 
	 * @param symbol - observed symbol
	 */
	public void trade() {

		if (provider == null) {
			setProvider(Providers.getRealTimeProvider());
		}

		TradersRegistry.getInstance().addTrader(this);
		getObserver().start();
	}

	@Override
	public void run() {
		trade();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName()).append('[');
		sb.append(getSymbol()).append(' ');
		sb.append(getPosition()).append(' ');
		sb.append(paper.getQuantity()).append('/').append(paper.getDesiredQuantity()).append(']');
		sb.append('[').append(getGenerator().getClass().getSimpleName()).append(']');
		return sb.toString();
	}

	/**
	 * @return Messages broker
	 */
	public MessagesBroker getMessagesBroker() {
		return broker;
	}

	/**
	 * Set new messages broker.
	 * 
	 * @param broker - new messages broker to set
	 */
	public void setMessagesBroker(MessagesBroker broker) {
		if (broker == null) {
			throw new IllegalArgumentException("Messages broker cannot be null");
		}
		this.broker = broker;
	}

	/**
	 * Acknowledge player about decision maker's market decision. This method is
	 * blocking - it won't return any value till user acknowledgment response.
	 * 
	 * @param de - decision event
	 * @return true if user acknowledged, false otherwise
	 */
	public boolean acknowledge(DecisionEvent de) {

		SignalType signal = de.getSignalType();
		Paper paper = de.getPaper();

		if (NOTIFICATIONS.contains(signal)) {

			if (LOG.isInfoEnabled()) {
				LOG.info("Acknowledge decision " + de);
			}

			boolean ok = false;
			try {
				ok = getMessagesBroker().acknowledge(paper, signal);
			} catch (MessagingException e) {
				LOG.error("Cannot acknowledge decision", e);
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("Aknowledge response is " + ok);
			}

			return ok;
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug(
					"This kind of decision (" + de.getSignalType() +
					") cannot be acknowledge");
			}
		}

		return false;
	}

	/**
	 * @return the paperQuantity
	 */
	public int getCurrentQuantity() {
		return quantity;
	}

	/**
	 * @param quantity - the paper quantity to set
	 */
	public void setCurrentQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the paper desired quantity
	 */
	public int getDesiredQuantity() {
		return desired;
	}

	/**
	 * @param desired - desired paper quantity
	 */
	public void setDesiredQuantity(int desired) {
		this.desired = desired;
	}

	/**
	 * @return Return paper.
	 */
	public Paper getPaper() {
		return paper;
	}

	@Override
	public void priceChange(PriceEvent pe) {
		try {
			events.put(pe);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * @return Return price events queue
	 */
	protected BlockingQueue<PriceEvent> getQueue() {
		return events;
	}

	/**
	 * @return Return number of price events in the queue
	 */
	public int getQueueSize() {
		return events.size();
	}

	/**
	 * @return the workman
	 */
	protected Workman getWorkman() {
		if (workman == null) {
			workman = new Workman(this);
		}
		return workman;
	}

	/**
	 * @return Currently used provider
	 */
	public RealTimeProvider getProvider() {
		return provider;
	}

	/**
	 * Set new real time data provider
	 * 
	 * @param provider - new real time data provider to set
	 */
	public void setProvider(RealTimeProvider provider) {
		this.provider = provider;
		this.observer.setProvider(provider);
	}
}
