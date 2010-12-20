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
package com.zigabyte.stock.tradeobserver;

import com.zigabyte.stock.data.*;
import com.zigabyte.stock.trade.*;
import java.util.Date;

/** Keeps track of winning/losing trade counts, and average profit/loss.

    @see TradeWinLossObserver
 **/
public class TradeWinLossObserver extends TradeObserverAdapter {
  // FIELDS
  private int winningTradeCount = 0, losingTradeCount = 0, evenTradeCount = 0;
  private double totalProfit = 0.0, totalLoss = 0.0;

  // INTERFACE TradeObserver
  /** Calculates profit or loss for sale, and updates counts and averages. **/
  public void orderSold(TradeOrder order, Date date,
			StockPosition position, TradingAccount account) {
    double totalCashOut =
      order.getExecutedValue() - account.getTradeFees(order.shares);
    double totalCostBasis = order.shares * position.getCostBasis();
    double profitOrLoss = totalCashOut - totalCostBasis;
    if (profitOrLoss > 0) {
      this.totalProfit += profitOrLoss;
      this.winningTradeCount++;
    } else if (profitOrLoss < 0) {
      this.totalLoss += profitOrLoss;
      this.losingTradeCount++;
    } else {
      this.evenTradeCount++;
    }
  }

  // ACCESSORS
  /** Number of sell trades that produced a net profit. **/
  public int getWinningTradeCount() {
    return this.winningTradeCount;
  }
  /** Number of sell trades that produced a net loss. **/
  public int getLosingTradeCount() {
    return this.losingTradeCount;
  }
  /** Number of sell trades that broke exactly even (no net profit or loss). **/
  public int getEvenTradeCount() {
    return this.evenTradeCount;
  }
  /** Average profit among sell trades that produced a profit. **/
  public double getAverageWinningTradeProfit() {
    return (this.winningTradeCount == 0? 0.00 : 
	    this.totalProfit/this.winningTradeCount);
  }
  /** Average loss among sell trades that produced a loss. **/
  public double getAverageLosingTradeLoss() {
    return (this.losingTradeCount == 0? 0.00 : 
	    this.totalLoss/this.losingTradeCount);
  } 
}
