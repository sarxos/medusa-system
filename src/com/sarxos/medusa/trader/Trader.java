package com.sarxos.medusa.trader;

import static com.sarxos.medusa.market.Position.LONG;
import static com.sarxos.medusa.market.Position.SHORT;
import static com.sarxos.medusa.market.SignalType.BUY;
import static com.sarxos.medusa.market.SignalType.SELL;

import java.util.EnumSet;

import com.sarxos.medusa.comm.Broker;
import com.sarxos.medusa.comm.MessagingException;
import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.data.DBDAOException;
import com.sarxos.medusa.data.Persisteable;
import com.sarxos.medusa.data.Providers;
import com.sarxos.medusa.data.RealTimeDataProvider;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;


/**
 * Trader class. It is designed to handle decision events from decision maker,
 * send notification/question and buy or sell given paper.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Trader implements DecisionListener, Runnable, Persisteable {

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
	private RealTimeDataProvider provider = null;

	/**
	 * Observed symbol.
	 */
	private Symbol symbol = null;

	/**
	 * Trader name.
	 */
	private String name = null;

	private Position position = null;

	/**
	 * Messaging broker.
	 */
	private Broker broker = null;

	/**
	 * Set of signal types to be acknowledged by player.
	 */
	private EnumSet<SignalType> notification = EnumSet.of(BUY, SELL);

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

	public Trader(String name, SignalGenerator<Quote> siggen, Symbol symbol, RealTimeDataProvider provider) {
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
			provider = Providers.getDefaultRealTimeDataProvider();
		}

		Observer observer = new Observer(provider, symbol);
		DecisionMaker dm = new DecisionMaker(observer, siggen);
		dm.addDecisionListener(this);

		setDecisionMaker(dm);

		try {
			broker = new Broker();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void decisionChange(DecisionEvent de) {

		System.out.println(de);

		SignalType signal = de.getSignalType();

		boolean acknowledge = false;

		if (notification.contains(signal)) {
			try {
				acknowledge = broker.acknowledge(de.getPaper(), signal);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}

		if (acknowledge) {
			switch (signal) {
				case BUY:
					// TODO buy mechanism - future - need QuickFixJ endpoint
					// for Bossa or Alior DAO (preferred)
					setPosition(LONG);
					break;
				case SELL:
					// TODO sell mechanism - future - need QuickFixJ endpoint
					// for Bossa or Alior DAO (preferred)
					setPosition(SHORT);
					break;
			}
		}
	}

	@Override
	public void positionChange(PositionEvent pe) {
		this.position = pe.getNewPosition();
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
			+ getGeneratorClassName() + "]";
	}

	@Override
	public void persist() {
		try {
			DBDAO.getInstance().updateTrader(this);
		} catch (DBDAOException e) {
			e.printStackTrace();
		}
	}
}
