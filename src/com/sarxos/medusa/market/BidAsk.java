package com.sarxos.medusa.market;

public class BidAsk {

	private double bid = 0;

	private double ask = 0;

	public BidAsk() {
	}

	public BidAsk(double bid, double ask) {
		super();
		this.bid = bid;
		this.ask = ask;
	}

	/**
	 * @return the bid
	 */
	public double getBid() {
		return bid;
	}

	/**
	 * @param bid the bid to set
	 */
	public void setBid(double bid) {
		this.bid = bid;
	}

	/**
	 * @return the ask
	 */
	public double getAsk() {
		return ask;
	}

	/**
	 * @param ask the ask to set
	 */
	public void setAsk(double ask) {
		this.ask = ask;
	}

}
