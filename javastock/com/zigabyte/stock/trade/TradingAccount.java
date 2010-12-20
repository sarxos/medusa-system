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

import com.zigabyte.stock.data.StockMarketHistory;
import java.util.Date;

/** A TradingAccount tracks current cash and stock positions.
    It accepts buy and sell trades to be executed
    the next trading day.<p>

    A simulation starts by calling
    {@link #initialize initialize}.<p>

    A trading strategy may {@link #buyStock buyStock} or
    {@link #sellStock sellStock} each day during the simulation.

    The simulation calls
    {@link #executeOrders executeOrders} on each trading day.
 **/
public interface TradingAccount extends Iterable<StockPosition> {

  /** Compute account fees for buying or selling of nShares of one stock
      in one order. **/
  public double getTradeFees(int nShares);

  /** Current cash balance + current stock value **/
  public double getCurrentAccountValue();

  /** Current cash remaining **/
  public double getCurrentCashBalance();

  /** Current buying power - understands shorts and margin. **/
  public double getMarginBuyingPower();

  /** Total value of stocks in the account, based on last Closing price. **/
  public double getCurrentStockValue();

  /** Value of stock position, based on last closing price
      and this account's trading fees **/
  public double getCurrentStockValue(StockPosition position);

  /** Most recent trading date, or initial date if no trading yet. **/
  public Date getCurrentDate();

  /** Projected cash balance + projected stock value. **/
  public double getProjectedAccountValue();

  /** Current cash adjusted for any pending Buy/Sell orders. **/
  public double getProjectedCashBalance();

  /** Projected value of stocks in account after pending buy/sell orders
      are executed, based on projected prices. **/
  public double getProjectedStockValue();

  /** StockMarketHistory used by this account to determine prices. **/
  public StockMarketHistory getStockMarketHistory();

  /** Number of stocks currently held. **/
  public int getStockPositionCount();
  public StockPosition getStockPosition(int i); 
  public StockPosition getStockPosition(String symbol);

  /** Initialize the trading account for a simulation run.
      (Should also initialize any TradeObservers on this account.)
      Called by simulator exactly once. **/
  public void initialize(Date initialDate, double initialCash);

  /** Place an order to buy shares of stock at time specified by OrderTiming.
      Called by a strategy during a simulation run. **/
  public void buyStock(String symbol, int shares, OrderTiming t, double limit);

  /** Place an order to sell shares of stock at time specified by OrderTiming.
      Called by a strategy during a simulation run. **/
  public void sellStock(String symbol, int shares, OrderTiming t, double limit);

  /** Execute orders and update balances.
      Called by simulator only on trading dates. **/
  public void executeOrders(Date tradingDate);

    /** Added by ATR: are any orders pending? Use in the strategy to avoid
        order duplication around weekends/holidays.
     **/
  public boolean hasPendingOrders();
}
