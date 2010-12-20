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
import org.jfree.data.xy.OHLCDataset;

/** An {@link OHLCDataset} based on a {@link StockHistory}.  

    Defines a single series.  Methods getY and getYValue return the
    adjusted close price.
 **/
public class OHLCDatasetOfStockHistory 
extends XYDatasetOfStockHistory implements OHLCDataset {
  public OHLCDatasetOfStockHistory(StockHistory history) {
    super(history);
  }
  /** return close value **/
  public double getYValue(int series, int item) {
    return getCloseValue(series, item);
  }
  /** return close value **/
  public Number getY(int series, int item) {
    return getClose(series, item);
  }

  public double getOpenValue(int series, int item) {
    return this.history.get(item).getAdjustedOpen();
  }
  public Number getOpen(int series, int item) {
    return new Float(this.history.get(item).getAdjustedOpen());
  }

  public double getHighValue(int series, int item) {
    return this.history.get(item).getAdjustedHigh();
  }
  public Number getHigh(int series, int item) {
    return new Float(this.history.get(item).getAdjustedHigh());
  }

  public double getLowValue(int series, int item) {
    return this.history.get(item).getAdjustedLow();
  }
  public Number getLow(int series, int item) {
    return new Float(this.history.get(item).getAdjustedLow());
  }

  public double getCloseValue(int series, int item) {
    return this.history.get(item).getAdjustedClose();
  }
  public Number getClose(int series, int item) {
    return new Float(this.history.get(item).getAdjustedClose());
  }

  public double getVolumeValue(int series, int item) {
    return this.history.get(item).getVolumeLots();
  }
  public Number getVolume(int series, int item) {
    return new Float(this.history.get(item).getVolumeLots());
  }

}
