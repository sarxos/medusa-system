package com.sarxos.medusa;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sarxos.medusa.db.DBDAO;
import com.sarxos.medusa.trader.Trader;


public class Runner extends Thread {

	/**
	 * Traders executor.
	 */
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	/**
	 * Currently working traders list.
	 */
	private List<Trader> traders = new LinkedList<Trader>();
	
	/**
	 * Is runner running?
	 */
	private AtomicBoolean running = new AtomicBoolean(false);
	
	
	public Runner() {
		super("Traders Runner");
	}
	
	protected void startOnce() {

		super.start();
		
		DBDAO dao = null;
		List<Trader> tmp = null;
		Iterator<Trader> it = null;
		Trader trader = null;
		String a, b;
		boolean found;

		while (running.get()) {
			
			try {
				dao = DBDAO.getInstance();
				tmp = dao.getTraders();
				if (tmp != null) {
					it = tmp.iterator();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			while (it != null && it.hasNext()) {
				
				trader = it.next();
				found = false;
				
				for (Trader t : traders) {
					a = t.getName();
					b = trader.getName();
					if (a != null && b != null) {
						found = a.equals(b);
						if (found) {
							break;
						}
					}
				}
				
				if (found) {
					continue;
				}
				
				System.out.println("Starting trader " + trader);
				try {
					executor.execute(trader);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopTraders() {
		running.compareAndSet(true, false);
	}
	
	public void startTraders() {
		if (running.compareAndSet(false, true)) {
			startOnce();
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	public static void main(String[] args) {
		Runner r = new Runner();
		r.startTraders();
		try {
			r.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
