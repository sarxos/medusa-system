package com.sarxos.medusa.trader;

import static com.sarxos.medusa.market.SignalType.BUY;
import static com.sarxos.medusa.market.SignalType.SELL;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.sarxos.medusa.comm.MessagesBroker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.data.persistence.Persistent;
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
@Persistent("trader")
public abstract class Trader implements DecisionListener, Runnable {

	/**
	 * Set of signal types to be acknowledged by player.
	 */
	public static final Set<SignalType> NOTIFICATIONS = Collections.unmodifiableSet(EnumSet.of(BUY, SELL));

	/**
	 * Decision maker (encapsulate decision logic).
	 */
	private DecisionMaker decisionMaker = null;

	/**
	 * Signal generator to use.
	 */
	@Persistent
	private SignalGenerator<Quote> siggen = null;

	/**
	 * Real time data provider.
	 */
	private RealTimeProvider provider = null;

	/**
	 * Observed symbol.
	 */
	@Persistent
	private Symbol symbol = null;

	/**
	 * Trader name.
	 */
	@Persistent
	private final String name;

	/**
	 * Current position (long, short).
	 */
	@Persistent
	private Position position = null;

	/**
	 * Messaging broker.
	 */
	private MessagesBroker broker = null;

	/**
	 * Trader constructor.
	 * 
	 * @param name - trader name
	 * @param siggen - signal generator
	 * @param symbol - symbol to trade (e.g. KGH, BRE)
	 */
	public Trader(String name, SignalGenerator<Quote> siggen, Symbol symbol) {
		this(name, siggen, symbol, null);
	}

	@Persistent
	public Trader(String name, SignalGenerator<Quote> siggen, Symbol symbol, RealTimeProvider provider) {
		if (name == null) {
			throw new IllegalArgumentException("Trader name cannot be null");
		}
		if (siggen == null) {
			throw new IllegalArgumentException("Signal generator cannotbe null");
		}
		this.name = name;
		this.siggen = siggen;
		this.symbol = symbol;
		this.provider = provider;
		this.symbol = symbol;
		this.init();
	}

	/**
	 * Initialize trader
	 */
	protected void init() {

		if (provider == null) {
			provider = Providers.getRealTimeProvider();
		}

		Observer observer = new Observer(provider, symbol);
		DecisionMaker dm = new DecisionMaker(observer, siggen);
		dm.addDecisionListener(this);

		setDecisionMaker(dm);

		try {
			broker = new MessagesBroker();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void positionChange(PositionEvent pe) {
		Position p = pe.getNewPosition();
		if (position != p) {
			position = p;
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
		if (position != p) {
			position = p;
		}
		getDecisionMaker().setCurrentPosition(p);
	}

	/**
	 * @return Return current position (long, short)
	 */
	public Position getPosition() {
		if (position == null) {
			DecisionMaker dm = getDecisionMaker();
			if (dm == null) {
				return null;
			}
			return dm.getCurrentPosition();
		} else {
			return position;
		}
	}

	/**
	 * @return Decision maker
	 */
	public DecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	/**
	 * Set new decision maker
	 * 
	 * @param decisionMaker - new decision maker to set
	 */
	public void setDecisionMaker(DecisionMaker decisionMaker) {
		this.decisionMaker = decisionMaker;
	}

	/**
	 * @return Price observer
	 */
	public Observer getObserver() {
		if (getDecisionMaker() == null) {
			return null;
		}
		return getDecisionMaker().getObserver();
	}

	/**
	 * Set new price observer.
	 * 
	 * @param observer - new observer to set
	 */
	public void setObserver(Observer observer) {
		if (getDecisionMaker() == null) {
			throw new IllegalStateException(
				"Cannot set observer because decision maker is not " +
				"created.");
		}
		getDecisionMaker().setObserver(observer);
	}

	/**
	 * @return Observed symbol (e.g. KGH, BRE)
	 */
	public Symbol getSymbol() {
		DecisionMaker dm = getDecisionMaker();
		Observer o = null;
		if (dm != null && (o = dm.getObserver()) != null) {
			return o.getSymbol();
		} else {
			return symbol;
		}
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
	public SignalGenerator<Quote> getGenerator() {
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

		Wallet wallet = Wallet.getInstance();
		Paper paper = wallet.getPaper(getSymbol());
		if (paper == null) {
			throw new IllegalStateException(
				"There is no '" + symbol + "' paper specified in the wallet. " +
				"You have to add paper to the wallet first, and then start " +
				"trading.");
		}

		getDecisionMaker().getObserver().start();
	}

	@Override
	public void run() {
		trade();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getSymbol() + "]["
			+ getGenerator().getClass().getSimpleName() + "]";
	}

	/**
	 * @return Messages broker
	 */
	public MessagesBroker getBroker() {
		return broker;
	}

	/**
	 * Set new messages broker.
	 * 
	 * @param broker - new messages broker to set
	 */
	public void setBroker(MessagesBroker broker) {
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

			boolean ok = false;
			try {
				ok = getBroker().acknowledge(paper, signal);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			return ok;
		}

		return false;
	}
}
