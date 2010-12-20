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

import com.zigabyte.stock.trade.*;
import java.util.Date;

/** A TradeObserver is informed by a TradingAccount after each trade executes,
    and after orders are completed each trading day. **/
public interface TradeObserver {
  /** The TradingAccount was initialized.
      @param date initial date for simulation.
      @param account account that was initialized. **/
  public void initialized(Date date, TradingAccount account);
  /** The TradingAccount executed the BUY order.
      @param order The order executed.  Its executed price has been set.
      @param tradingDate The trading date.
      @param position The resulting position in the stock after the trade.
      @param account The account making the trade. **/
  public void orderBought(TradeOrder order, Date tradingDate,
			  StockPosition position, TradingAccount account);
  /** The TradingAccount executed the SELL order.
      @param order The order executed.  Its executed price has been set.
      @param tradingDate The trading date.
      @param position The resulting position in the stock after the trade.
      @param account The account making the trade. **/
  public void orderSold(TradeOrder order, Date tradingDate,
			StockPosition position, TradingAccount account);
  /** The TradingAccount cancelled the order (no price available).
      @param order The order cancelled.  Its executed price is 0.
      @param tradingDate The trading date.
      @param position The current position in the stock.
      @param account The account making the trade. **/
  public void orderCancelled(TradeOrder order, Date tradingDate,
			     StockPosition position, TradingAccount account);
  /** The TradingAccount has finished executing its orders for tradingDate.
      This is called only on trading days.  It is called whether or not
      the account executed any trades that day.
      @param tradingDate The trading date.
      @param account The observed account. **/
  public void ordersCompleted(Date tradingDate, TradingAccount account);
}
