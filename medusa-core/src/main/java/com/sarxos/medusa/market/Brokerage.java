package com.sarxos.medusa.market;

import java.util.List;

/**
 * Brokerage abstraction 
 * 
 * @author Bartosz Firyn (SarXos)
 */
public abstract class Brokerage {

	private String name = null;
	
	public static Brokerage getDefault() {
		// TODO: get from configuration
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract List<Account> getAccounts();

	public abstract Account getAccount(Order order);
}
