package com.sarxos.medusa.market;

/**
 * This class represents bid / ask order pairs.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class BidAsk {

	/**
	 * Bid price.
	 */
	private double bid = 0;

	/**
	 * Ask price.
	 */
	private double ask = 0;

	/**
	 * Bid orders number.
	 */
	private int bidCount = 0;

	/**
	 * Ask orders number.
	 */
	private int askCount = 0;

	/**
	 * Create bid / ask pair with all properties set to zero.
	 */
	public BidAsk() {
		this(0, 0);
	}

	/**
	 * Create bid / ask pair with particular prices
	 * 
	 * @param bid - bid price to set
	 * @param ask - ask price to set
	 */
	public BidAsk(double bid, double ask) {
		this(bid, ask, 0, 0);
	}

	/**
	 * Create bid / ask pair with particular prices and with particular number
	 * of bid / ask orders.
	 * 
	 * @param bid - bid price to set
	 * @param ask - ask price to set
	 * @param bc - bid orders count to set
	 * @param ac - ask orders count to set
	 */
	public BidAsk(double bid, double ask, int bc, int ac) {
		super();
		this.bid = bid;
		this.ask = ask;
		this.bidCount = bc;
		this.askCount = ac;
	}

	/**
	 * @return Return bid price
	 */
	public double getBid() {
		return bid;
	}

	/**
	 * @param bid - new bid price to set
	 */
	public void setBid(double bid) {
		this.bid = bid;
	}

	/**
	 * @return Return ask price
	 */
	public double getAsk() {
		return ask;
	}

	/**
	 * @param ask - new ask price to set
	 */
	public void setAsk(double ask) {
		this.ask = ask;
	}

	/**
	 * @return Number of bid orders
	 */
	public int getBidCount() {
		return bidCount;
	}

	/**
	 * @param bidCount - new number of bid orders to set
	 */
	public void setBidCount(int bidCount) {
		this.bidCount = bidCount;
	}

	/**
	 * @return Number of ask orders
	 */
	public int getAskCount() {
		return askCount;
	}

	/**
	 * @param askCount - new number of ask orders to set
	 */
	public void setAskCount(int askCount) {
		this.askCount = askCount;
	}

	/**
	 * The amount by which the ask price exceeds the bid. This is essentially
	 * the difference in price between the highest price that a buyer is willing
	 * to pay for an asset and the lowest price for which a seller is willing to
	 * sell it.<br>
	 * <br>
	 * For example, if the bid price is $20 and the ask price is $21 then the
	 * "bid-ask spread" is $1.
	 * 
	 * @return Absolute difference between ask and bid prices
	 */
	public double getSpread() {
		return Math.abs(ask - bid);
	}
}
