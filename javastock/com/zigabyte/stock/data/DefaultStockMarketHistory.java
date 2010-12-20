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

import java.util.*;
import java.io.*;

/** Default implementation of {@link StockMarketHistory}.
    Stores {@link StockHistory}s in an {@link ArrayList} for fast access
    by index, and maintains a {@link HashMap} for access by stock symbol. **/
public class DefaultStockMarketHistory
implements StockMarketHistory, Serializable, ObjectInputValidation {
  // FIELDS
  /** List of stock histories for fast access by integer index.
      Histories are stored in the order in which they are added. **/
  private final ArrayList<StockHistory> stockHistories =
    new ArrayList<StockHistory>();
  /** Map from stock symbol to integer, for access by stock symbol. **/
  private transient Map<String,Integer> symbolIndex =
    new HashMap<String,Integer>();
  private String name;
  // CONSTRUCTOR
  /** Create an empty history with no stocks. **/
  public DefaultStockMarketHistory() {
    this(null);
  }
  public DefaultStockMarketHistory(String name) {
    this.name = name;
  }

  // FIELD ACCESSORS
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }

  // MUTATOR METHODS
  /** Add stockHistory.  If there is a previous stock history with the same
      stock symbol, the new stockHistory replaces the previous history, and the
      old stockHistory is returned.  Otherwise the new history is added at
      the end. **/
  public StockHistory add(StockHistory stockHistory) {
    String symbol = stockHistory.getSymbol();
    Integer index = this.symbolIndex.get(symbol);
    if (index != null) {
      // replace old history
      return this.stockHistories.set(index, stockHistory);
    } else {
      // add new history
      index = stockHistories.size();
      this.stockHistories.add(stockHistory);
      this.symbolIndex.put(symbol, index);
      return null;
    }
  }
  /** Remove the stock history at given index and return it.
      @see #remove(String) **/
  public StockHistory remove(int index) {
    StockHistory stockHistory = this.stockHistories.remove(index);
    if (stockHistory != null) {
      this.symbolIndex.remove(stockHistory.getSymbol());
    }
    return stockHistory;
  }
  /** Remove the stock history with the given stock symbol and return it,
      or return null if there is none. **/
  public StockHistory remove(String stockSymbol) {
    Integer index = this.symbolIndex.remove(stockSymbol);
    if (index != null) {
      return this.stockHistories.remove(index.intValue());
    } else {
      return null;
    }
  }
  /** Remove all stock histories. **/
  public void clear() {
    stockHistories.clear();
    symbolIndex.clear();
  }

  // INTERFACE StockMarketHistory

  public int size() {
    return stockHistories.size();
  }
  public StockHistory get(int index) {
    return stockHistories.get(index);
  }
  public StockHistory get(String stockSymbol) {
    Integer index = symbolIndex.get(stockSymbol);
    if (index != null) {
      return stockHistories.get(index.intValue());
    } else {
      return null;
    }
  }
  public Iterator<StockHistory> iterator() {
    return stockHistories.iterator();
  }

  public Date nextTradingDate(Date date, boolean up) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, up? 1 : -1);
    // add at most 13 days to avoid infinite loop 
    for (int count = 0; count < 13; count++) { 
      Date newDate = calendar.getTime();
      if (hasTradingData(newDate))
	return newDate;
      calendar.add(Calendar.DATE, up? 1 : -1);
    }
    return null;
  }

  public boolean hasTradingData(Date date) {
    for (StockHistory history : this) {
      if (history.get(date) != null)
	return true;
    }
    return false;
  }
  // INTERFACE Serializable

  /** Called by serialization (even though it is private). **/
  private void readObject(ObjectInputStream in)
  throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    // restore transient field
    this.symbolIndex = new HashMap<String,Integer>();
    // register to have validateObject called when all data read
    in.registerValidation(this, 0); 
  }

  /** Called when deserialized.  Recomputes index after all data read. **/
  public void validateObject() {
    this.symbolIndex.clear();
    for (int i = 0; i < this.stockHistories.size(); i++)
      this.symbolIndex.put(this.stockHistories.get(i).getSymbol(), i);
  }

  // OBJECT
  /** Returns name if not null, else super.toString() **/
  public String toString() {
    return (this.name != null? this.name : super.toString());
  }

}

