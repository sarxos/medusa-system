package com.sarxos.medusa.trader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sarxos.medusa.market.Symbol;


/**
 * Papers wallet.
 * 
 * TODO: need to reimplement to interact strictly with traders
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Wallet {

	/**
	 * Entry to be stored in wallet.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static class WalletEntry {

		/**
		 * Symbol.
		 */
		private Symbol symbol = null;

		/**
		 * Number of papers.
		 */
		private int quantity = 0;

		/**
		 * Creates new Wallet entry
		 * 
		 * @param paper - paper
		 * @param count - number of papers
		 */
		protected WalletEntry(Symbol symbol, int count) {
			super();
			this.symbol = symbol;
			this.quantity = count;
		}

		/**
		 * @return Return paper symbol
		 */
		public Symbol getSymbol() {
			return symbol;
		}

		/**
		 * @return Return number of papers
		 */
		public int getQuantity() {
			return quantity;
		}
	}

	private List<WalletEntry> papers = new LinkedList<Wallet.WalletEntry>();

	/**
	 * Wallet instance.
	 */
	private static Wallet instance = new Wallet();

	/**
	 * Private constructor.
	 */
	private Wallet() {
		this.reload();
	}

	/**
	 * @return Return {@link Wallet} singleton instance.
	 */
	public static Wallet getInstance() {
		return instance;
	}

	/**
	 * Force reload wallet.
	 */
	public void reload() {

		TradersRegistry registry = TradersRegistry.getInstance();
		Map<Symbol, Integer> mapping = new HashMap<Symbol, Integer>();
		for (Trader t : registry.getTraders()) {
			Symbol s = t.getPaper().getSymbol();
			Integer c = mapping.get(s);
			int n = t.getCurrentQuantity();
			if (c == null) {
				mapping.put(s, Integer.valueOf(n));
			} else {
				mapping.put(s, Integer.valueOf(c.intValue() + n));
			}
		}

		Set<Entry<Symbol, Integer>> entries = mapping.entrySet();
		List<WalletEntry> tmp = new LinkedList<Wallet.WalletEntry>();
		for (Entry<Symbol, Integer> e : entries) {
			tmp.add(new WalletEntry(e.getKey(), e.getValue().intValue()));
		}

		synchronized (papers) {
			papers.clear();
			papers.addAll(tmp);
		}
	}
}
