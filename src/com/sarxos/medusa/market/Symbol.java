package com.sarxos.medusa.market;


public enum Symbol {
	
//	/**
//	 * PL
//	 */
	WIG20("WIG20"),
//	
//	/**
//	 * UK 
//	 */
//	FTSE100,
//	
//	/**
//	 * DE
//	 */
//	DAX,
//	
//	/**
//	 * Wegry
//	 */
//	BUX,
//	
//	/**
//	 * US
//	 */
//	NASDAQ,
//	DJI,
//	SP,
//	
//	/**
//	 * FR
//	 */
//	CAC,
//	
//	/**
//	 * JP
//	 */
//	NIKKEI,
//	
//	// quotes
	
	/**
	 * KGHM
	 */
	KGH("KGHM");
	
	private String name = null;
	

	private Symbol(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
