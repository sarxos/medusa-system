package com.sarxos.medusa.market;


/**
 * Securities groups.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public enum SecuritiesGroup {

	/**
	 * Stocks from WIG20 - 1'st group
	 */
	WIG20_GROUP_1("C1"),
	
	/**
	 * Stocks from WIG20 - 2'nd group
	 */
	WIG20_GROUP_2("C2"),
	
	/**
	 * Remaining stocks from the continuous trading - 1'st group
	 */
	REMAINING_GROUP_1("C3"),
	
	/**
	 * Remaining stocks from the continuous trading - 2'nd group
	 */
	REMAINING_GROUP_2("C4"),
	
	/**
	 * Stocks from mWIG40 - 1'st group
	 */
	MWIG40_GROUP_1("C5"),
	
	/**
	 * Stocks from mWIG40 - 2'nd group
	 */
	MWIG40_GROUP_2("C6"),
	
	/**
	 * Stocks from sWIG80 - 1'st group
	 */
	SWIG80_GROUP_1("C7"),
	
	/**
	 * Stocks from sWIG80 - 2'nd group
	 */
	SWIG80_GROUP_2("C8"),
	
	/**
	 * Rights to shares for companies listed in the continuous system
	 */
	SHARE_RIGHTS("C9"),
	
	/**
	 * Treasury bonds
	 */
	TREASURY_BONDS("CO"), 
	
	/**
	 * Investment certificates
	 */
	INVESTMENT_CERTS("CI"),
	
	/**
	 * ETF certificates
	 */
	ETF_CERTS("CE"), 
	
	/**
	 * Structured certificates - quoted in the currency
	 */
	STRUCTURED_CERTS("CV"),
	
	/**
	 * Futures contracts on indexes
	 */
	FUTURES_INDEXES("CF"), 
	
	/**
	 * Futures contracts on exchange rates (currency)
	 */
	FUTURES_CURRENCY("CC"), 
	
	/**
	 * Futures contracts on stock prices
	 */
	FUTURES_QUOTES("CA"),
	
	/**
	 * Option contracts of MiniWig type
	 */
	OPTIONS_MINIWIG("CM"), 
	
	/**
	 * Option contracts on the indexes
	 */
	OPTIONS_INDEXES("CY");
	
	
	/**
	 * Group name.
	 */
	private String name = null;
	
	/**
	 * Securities group.
	 * 
	 * @param name
	 */
	private SecuritiesGroup(String name) {
		this.name = name;
	}

	/**
	 * @return Name of the securities group.
	 */
	public String getName() {
		return name;
	}
}
