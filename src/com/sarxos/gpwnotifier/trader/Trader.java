package com.sarxos.gpwnotifier.trader;



public class Trader extends Thread {

	private Observer observer = null;
	
	private Calendarium calendarium = Calendarium.getInstance(); 
	
	@Override
	public void run() {
		
		super.run();
		
		do {
			
			// trade
			
		} while(true);
	}
}
