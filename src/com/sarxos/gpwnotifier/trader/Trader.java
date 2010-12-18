package com.sarxos.gpwnotifier.trader;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sarxos.gpwnotifier.data.RealTimeDataProvider;
import com.sarxos.gpwnotifier.data.bizzone.BizzoneDataProvider;
import com.sarxos.gpwnotifier.entities.Symbol;
import com.sarxos.gpwnotifier.gpw.Calendarium;
import com.sarxos.gpwnotifier.gpw.Paper;



public class Trader extends Thread implements PriceListener {

	private Map<Symbol, Observer> observers = new HashMap<Symbol, Observer>();
	
	private Calendarium calendarium = Calendarium.getInstance();
	
	private Wallet wallet = Wallet.getInstance();
	
	public Trader() {
		setDaemon(true);
	}
	
	@Override
	public void run() {
		
		super.run();
		
		RealTimeDataProvider provider = new BizzoneDataProvider();

		Observer observer = null;
		Symbol symbol = null;
		
		for (Paper paper : wallet.getPapers()) {
			symbol = paper.getSymbol();
			observer = new Observer(provider, symbol);
			//observer.start();
			observers.put(symbol, observer);
		}
		
		Date now = null;
		
		boolean working = false;
		boolean session = false;
		
		do {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			now = new Date();
			working = calendarium.isWorkingDay(now);
			session = true; // check if session in progress
			
			// if stock exchange is open and session in progress
			if (working && session) {
				// resume observers if paused
			} else {
				// pause observers if running
			}
			
		} while(true);
	}

	@Override
	public void priceChange(PriceEvent event) {
		// trade
	}
	
	/**
	 * Add given paper to the wallet.
	 * 
	 * @param paper
	 * @return Number of papers with given symbol after remove
	 */
	public int addToWallet(Paper paper) {
		return wallet.add(paper);
	}

	/**
	 * Remove given paper from the wallet.
	 * 
	 * @param paper - paper to remove
	 * @return Number of papers with given symbol after remove
	 */
	public int removeFromWallet(Paper paper) {
		return wallet.remove(paper);
	}
	
	public static void main(String[] args) {
		Trader t = new Trader();
		t.addToWallet(new Paper(Symbol.WIG20, 60));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
