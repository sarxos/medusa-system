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

/** Default implementation of {@link StockHistory}.
    Stores {@link StockDataPoint}s in an {@link ArrayList} sorted by date. **/
public class DefaultStockHistory extends AbstractStockHistory {
  // FIELDS
  /** The data points, sorted by date, earliest first. **/
  private final ArrayList<StockDataPoint> dataPoints =
    new ArrayList<StockDataPoint>();
  
  // CONSTRUcTOR
  /** Create a new history for the stock with the given symbol and name. **/
  public DefaultStockHistory(String symbol, String name) {
    super(symbol, name);
  }

  /** Insert data point into history. 
      @return old datapoint if there is a previous datapoint with the
      same date, else null. **/
  public StockDataPoint add(StockDataPoint dataPoint) {
    int index = binarySearch(dataPoint.getDate());
    if (index >= 0) { // replace old point at same date, return old pt
      return this.dataPoints.set(index, dataPoint); 
    } else { // add new point
      this.dataPoints.add(~index, dataPoint);
      return null;
    }
  }
  /** Remove data point at index.
      @return removed datapoint.
      @see #remove(java.util.Date) **/
  public StockDataPoint remove(int index) {
    return this.dataPoints.remove(index);
  }
  /** Remove all data points. **/
  public void clear() {
    this.dataPoints.clear();
  }
  /** Iterator for datapoints. **/
  public Iterator<StockDataPoint> iterator() {
    return this.dataPoints.iterator();
  }
  /** Number of datapoints **/
  public int size() {
    return this.dataPoints.size();
  }
  /** Get datapoint at index **/
  public StockDataPoint get(int index) {
    return this.dataPoints.get(index);
  }

}
