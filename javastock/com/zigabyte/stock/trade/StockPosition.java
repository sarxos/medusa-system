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

/** Stores the shares of a stock held by a {@link TradingAccount} **/
public interface StockPosition {
	/** Stock symbol **/
	public String getSymbol();

	/** Number of shares held **/
	public int getShares();

	/** Average cost basis PER SHARE **/
	public double getCostBasis();

	/** Get highest price attained during hold period **/
	public double getHighWaterLine();

	/** Get lowest price attained during hold period **/
	public double getLowWaterLine();

	/** Update the high/low waterlines **/
	public void updateWaterLines(StockDataPoint sdp);
	
	/** Date of (earliest) purchase. **/
	public Date getDatePurchased();

	/** Add purchased shares to the position. **/
	public void addShares(Date date, int shares, double perShareCostBasis);

	/** Remove sold shares from the position. **/
	public void removeShares(Date date, int shares, double perShareCashOut);
}
