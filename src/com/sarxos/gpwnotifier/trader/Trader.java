package com.sarxos.gpwnotifier.trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sarxos.gpwnotifier.data.HistoricalDataProvider;
import com.sarxos.gpwnotifier.data.RealTimeDataProvider;
import com.sarxos.gpwnotifier.data.bossa.BossaHDProvider;
import com.sarxos.gpwnotifier.data.interia.InteriaRTDProvider;
import com.sarxos.gpwnotifier.market.Paper;
import com.sarxos.gpwnotifier.market.Symbol;



public class Trader extends Thread implements DecisionListener {

	private Map<Symbol, DecisionMaker> makers = new HashMap<Symbol, DecisionMaker>();
	
	private static Trader instance = new Trader();
	
	private Wallet wallet = Wallet.getInstance();
	
	private HistoricalDataProvider hdp = null;
	
	private RealTimeDataProvider rtdp = null;

	
	public Trader() {
		setDaemon(true);
		// TODO read from config
		hdp = new BossaHDProvider();
		rtdp = new InteriaRTDProvider();
		
		init();
	}

	public static Trader getInstance() {
		return instance;
	}

	protected DecisionMaker createDecisionMaker(Paper paper) {
		Observer o = new Observer(rtdp, paper.getSymbol());
		DecisionMaker dm = new DecisionMaker(o);
		dm.addDecisionListener(this);
		return dm;
	}
	
	protected void init() {
		List<Paper> papers = wallet.getPapers();
		for (Paper paper : papers) {
			makers.put(paper.getSymbol(), createDecisionMaker(paper));
		}
	}
	
	@Override
	public void run() {
		super.run();
	}

	
	
	/**
	 * Add given paper to the wallet.
	 * 
	 * @param paper
	 * @return 
	 */
	public boolean addPaper(Paper paper) {
		boolean added = wallet.addPaper(paper); 
		if (added) {
			makers.put(paper.getSymbol(), createDecisionMaker(paper));
		}
		return added;
	}

	/**
	 * Remove given paper from the wallet.
	 * 
	 * @param paper - paper to remove
	 * @return
	 */
	public boolean updateWallet(Paper paper) {
		return wallet.removePaper(paper);
	}

	/**
	 * Add given paper to the wallet.
	 * 
	 * @param paper
	 * @return 
	 */
	public boolean removePaper(Paper paper) {
		boolean removed = wallet.removePaper(paper); 
		if (removed) {
			makers.remove(paper.getSymbol());
		}
		return removed;
	}
	
	public List<DecisionMaker> getDecisionMakers() {
		int size = (int) (makers.size() * 1.5);
		ArrayList<DecisionMaker> m = new ArrayList<DecisionMaker>(size);
		m.addAll(makers.values());
		return m;
	}

	public DecisionMaker getDecisionMakerForPaper(Paper paper) {
		Symbol symbol = paper.getSymbol();
		if (symbol == null) {
			throw new IllegalArgumentException(
					"Cannot get decision maker for paper with " +
					"null symbol"
			);
		}
		return makers.get(symbol);
	}
	
	public static void main(String[] args) {
		Trader t = new Trader();
		t.addPaper(new Paper(Symbol.WIG20, 60));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void decisionChange(DecisionEvent event) {
		
	}
}
