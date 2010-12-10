package com.sarxos.gpwnotifier.entities;

import com.sarxos.gpwnotifier.data.stoq.StoqColumn;


/**
 * Future class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Future extends Quote {

	@StoqColumn("OpenInt")
	private double interests;

	/**
	 * @return Amount of open interests.
	 */
	public double getInterests() {
		return interests;
	}

	/**
	 * Set number of open interests.
	 * 
	 * @param open
	 */
	public void setInterests(double open) {
		this.interests = open;
	}
}
