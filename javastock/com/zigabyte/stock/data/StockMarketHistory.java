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
package com.zigabyte.stock.data;

import java.util.Date;

/** Interface to access a collection of stock histories by index
    or stock symbol. **/
public interface StockMarketHistory extends Iterable<StockHistory> {
  /** The number of stock histories in this market history. **/
  public int size();
  /** Get a stock history by index. **/
  public StockHistory get(int index);
  /** Get a stock history by stock symbol, or null if none found. **/
  public StockHistory get(String stockSymbol);

  /** Return true if (some) data is available for date.
      Returns false if market was closed on date, or if data does
      not cover date. **/
  public boolean hasTradingData(Date date);

  /** If up is true, return next date for which there is (some) trading data.
      If up is false, return previous date for
      which there is (some) trading data.
      Returns null if there are no further dates. **/
  public Date nextTradingDate(Date date, boolean up);
}
