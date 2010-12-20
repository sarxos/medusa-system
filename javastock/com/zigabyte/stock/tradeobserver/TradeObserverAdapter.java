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

/** Provides empty {@link TradeObserver} methods.
    Derived classes need implement just the ones they need. **/
public abstract class TradeObserverAdapter implements TradeObserver {
  public void initialized(Date date, TradingAccount account) {
  }
  public void orderBought(TradeOrder order, Date date,
			  StockPosition position, TradingAccount account) {
  }
  public void orderSold(TradeOrder order, Date date,
			StockPosition position, TradingAccount account) {
  }
  public void orderCancelled(TradeOrder order, Date date,
			     StockPosition position, TradingAccount account) {
  }
  public void ordersCompleted(Date date, TradingAccount account) {
  }
}
