package com.sarxos.medusa.trader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Traders registry.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class TradersRegistry {

	/**
	 * Static instance.
	 */
	private static final TradersRegistry INSTANCE = new TradersRegistry();

	/**
	 * Hash map where traders are stored.
	 */
	private Map<String, Trader> traders = new HashMap<String, Trader>();

	/**
	 * @return Return static instance
	 */
	public static final TradersRegistry getInstance() {
		return INSTANCE;
	}

	/**
	 * Add trader to the registry
	 * 
	 * @param t - trader to add
	 */
	public void addTrader(Trader t) {
		if (t == null) {
			throw new IllegalArgumentException("Trader to add to the registry cannot be null");
		}
		String name = t.getName();
		if (!traders.containsKey(name)) {
			traders.put(name, t);
		} else {
			throw new IllegalArgumentException(
				"Traders registry already contains trader with " +
				"name '" + name + "'");
		}
	}

	/**
	 * Remove trader from the registry
	 * 
	 * @param t - trader to remove
	 */
	public void removeTrader(Trader t) {
		traders.remove(t.getName());
	}

	/**
	 * Remove trader from the registry
	 * 
	 * @param name - name of the trader to remove
	 */
	public void removeTrader(String name) {
		traders.remove(name);
	}

	/**
	 * @return Return all traders in the registry
	 */
	public List<Trader> getTraders() {

		List<Trader> list = new LinkedList<Trader>();
		Iterator<Entry<String, Trader>> ei = traders.entrySet().iterator();

		while (ei.hasNext()) {
			list.add(ei.next().getValue());
		}

		return list;
	}

	/**
	 * @return Return number of traders in the registry
	 */
	public int getTradersCount() {
		return traders.size();
	}
	
	/**
	 * Return trader on the base name given as the input argument
	 * 
	 * @param name - name of the trader to find
	 * @return Return trader of null if not found
	 */
	public Trader getTrader(String name) {
		return traders.get(name);
	}
}
