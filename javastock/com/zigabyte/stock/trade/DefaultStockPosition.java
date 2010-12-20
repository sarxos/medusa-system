/*
 *  javastock - Java MetaStock parser and Stock Portfolio Simulator
 *  Copyright (C) 2005 Zigabyte Corporation. ALL RIGHTS RESERVED.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.zigabyte.stock.trade;

import java.util.Date;

import com.zigabyte.stock.data.StockDataPoint;

/** Default implementation of {@link StockPosition}. * */
public class DefaultStockPosition implements StockPosition {

	private final String symbol;

	private int shares = 0;

	private double avgCostBasis = 0.0;

	private Date initialPurchaseDate = null;

	private double highWaterLine = 0;

	private double lowWaterLine = 0;

	public DefaultStockPosition(String symbol) {
		this.symbol = symbol;
	}

	// // INTERFACE StockPosition

	public String getSymbol() {
		return this.symbol;
	}

	public int getShares() {
		return this.shares;
	}

	public double getCostBasis() {
		return this.avgCostBasis;
	}

	public Date getDatePurchased() {
		return this.initialPurchaseDate;
	}

	public void addShares(Date date, int buyShares, double costBasisPerShare) {
		if (buyShares == (-1) * this.shares) {
			// buy-to-cover
			this.shares = 0;
		} else if (this.initialPurchaseDate == null) {
			// initial purchase
			this.initialPurchaseDate = date;
			this.avgCostBasis = costBasisPerShare;
			this.shares = buyShares;
			this.highWaterLine = avgCostBasis;
			this.lowWaterLine = avgCostBasis;
		} else {
			// add to existing position
			double addedValue = buyShares * costBasisPerShare;
			double previousValue = this.shares * this.avgCostBasis;
			int totalShares = this.shares + buyShares;
			this.avgCostBasis = (previousValue + addedValue) / totalShares;
			this.shares = totalShares;
		}
	}

	public void removeShares(Date date, int sellShares, double price) {
		this.shares -= sellShares;
		if (this.shares < 0) {
			// update cost info when short-selling.
			if (this.initialPurchaseDate == null) {
				this.initialPurchaseDate = date;
				this.avgCostBasis = price;
				this.highWaterLine = avgCostBasis;
				this.lowWaterLine = avgCostBasis;
			} else {
				// leave initialPurchaseDate alone
				double addedValue = sellShares * price;
				double previousValue = this.shares * this.avgCostBasis;
				int totalShares = this.shares - sellShares;
				this.avgCostBasis = (previousValue - addedValue) / totalShares;
			}
		}
	}

	public String toString() {
		return getShares() + getSymbol() + "@" + getCostBasis();
	}

	public double getHighWaterLine() {
		return highWaterLine;
	}

	public double getLowWaterLine() {
		return lowWaterLine;
	}

	public void updateWaterLines(StockDataPoint sdp) {
		if(sdp.getAdjustedHigh() > this.highWaterLine) {
			this.highWaterLine = sdp.getAdjustedHigh(); 
		}
		if(sdp.getAdjustedLow() < this.lowWaterLine) {
			this.lowWaterLine = sdp.getAdjustedLow(); 
		}
	}
	
}
