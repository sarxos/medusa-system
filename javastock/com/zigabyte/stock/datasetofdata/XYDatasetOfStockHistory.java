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
package com.zigabyte.stock.datasetofdata;

import com.zigabyte.stock.data.StockHistory;

import org.jfree.data.xy.AbstractXYDataset;

/** An {@link org.jfree.data.xy.XYDataset XYDataset}
    based on a {@link StockHistory}.  

    Defines a single series.  Methods getY and getYValue return the
    adjusted close price.
 **/
public class XYDatasetOfStockHistory extends AbstractXYDataset {
  protected final StockHistory history;
  public XYDatasetOfStockHistory(StockHistory history) {
    this.history = history;
  }
  /** Return 1. **/
  public int getSeriesCount() {
    return 1;
  }
  /** Return {@link StockHistory#getSymbol history symbol} **/
  public String getSeriesName(int series) {
    return this.history.getSymbol();
  }
  /** Return {@link StockHistory#size history size} **/
  public int getItemCount(int series) {
    return this.history.size();
  }

  /** Return item date.getTime() **/
  public double getXValue(int series, int item) {
    return this.history.get(item).getDate().getTime();
  }
  /** Return item date.getTime() **/
  public Number getX(int series, int item) {
    return new Double(getXValue(series, item));
  }
  /** Return item adjusted close value **/
  public double getYValue(int series, int item) {
    return this.history.get(item).getAdjustedClose();
  }
  /** Return item adjusted close value **/
  public Number getY(int series, int item) {
    return new Float(this.history.get(item).getAdjustedClose());
  }
}
