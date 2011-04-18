package com.sarxos.medusa.market.order;

import com.sarxos.medusa.market.AbstractOrder;
import com.sarxos.medusa.market.OrderDirection;
import com.sarxos.medusa.market.Paper;


/**
 * Trailing stop order.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class TrailingStop extends AbstractOrder {

	/**
	 * Sell threshold - 5% by default.
	 */
	private double threshold = 0.05; // 5%

	public TrailingStop(Paper paper, double threshold) {
		super(paper, OrderDirection.SELL);
		this.threshold = threshold;
	}

	/**
	 * @return Stop threshold, 0.05 (5%) by default.
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold - new threshold to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
