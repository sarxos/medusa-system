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
import java.util.*;
import java.text.*;
import java.io.*;

/** Observes a trading account and stores the values of the
    account balances (cash, stock value) at the end of every trading day.

    @see TradeTraceObserver
**/
public class BalanceHistoryObserver extends TradeObserverAdapter {
  //// DATAPOINT
  public static class DataPoint {
    private final Date date;
    private final double cashBalance;
    private final double stockValue;
    public DataPoint(Date date, double cashBalance, double stockValue) {
      this.date = date;
      this.cashBalance = cashBalance;
      this.stockValue = stockValue;
    }
    public Date getDate() { return this.date; }
    public double getCashBalance() { return this.cashBalance; } 
    public double getStockValue() { return this.stockValue; }
    public double getTotalValue() { return getCashBalance() + getStockValue(); }
  }
  //// FIELDS
  final List<DataPoint> data =
    Collections.synchronizedList(new ArrayList<DataPoint>());

  /** Record datapoint **/
  public void initialized(Date tradingDate, TradingAccount account) {
    record(tradingDate, account);
  }    

  /** Record datapoint **/
  public void ordersCompleted(Date tradingDate, TradingAccount account) {
    record(tradingDate, account);
  }
  
  /** Record datapoint **/
  protected void record(Date tradingDate, TradingAccount account) {
    this.data.add(new DataPoint(tradingDate,
				account.getCurrentCashBalance(),
				account.getCurrentStockValue()));
  }
  public List<DataPoint> getDataPoints() {
    return this.data;
  }
}
