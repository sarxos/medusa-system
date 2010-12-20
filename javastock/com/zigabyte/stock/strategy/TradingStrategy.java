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
package com.zigabyte.stock.strategy;

import com.zigabyte.stock.data.StockMarketHistory;
import com.zigabyte.stock.trade.TradingAccount;
import java.util.Date;

/** A TradingStrategy is called by a
    {@linkplain com.zigabyte.stock.trade.DefaultTradingSimulator trading simulation}
    to make buy and sell orders on a
    {@linkplain com.zigabyte.stock.trade.TradingAccount trading account}. **/
public interface TradingStrategy {
  /** Place trading orders for an account during a trading simulation.
      @param histories may be used to obtain price information.
      @param account the account for which to place orders.
      @param date current date.
      @param daysUntilMarketOpen number of days delay until next market open,
      usually 0, but for markets closed on saturday and sunday it will be
      1 on a Saturday, 2 on a Friday, and may be more if there is a holiday. **/
  public void placeTradeOrders(StockMarketHistory histories,
			       TradingAccount account,
			       Date date, int daysUntilMarketOpen);
}
