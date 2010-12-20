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
package com.zigabyte.stock.indicator;

import com.zigabyte.stock.data.StockHistory;
import com.zigabyte.stock.data.StockDataPoint;

import java.util.Arrays;

/** Provides method {@link #getField getField} to select which
    field of {@link StockDataPoint} to use
    (open,high,low,close,volume)**/
public abstract class AbstractIndicator implements Indicator {
  //// CONSTRUcTORS
  public AbstractIndicator() {}

  //// Interface INDICATOR
  public abstract double compute(StockHistory history, int item);

    // convenience method added by ATR
  public double compute(StockHistory history, java.util.Date date) {
      return this.compute(history, history.getIndexAtOrBefore(date));
  }
  /** Return the field identified by fieldID in history.get(item).**/
  protected double getField(StockHistory history, int item,
			    StockDataPoint.FieldID fieldID) {
    switch (fieldID) {
    case CLOSE:  return history.get(item).getAdjustedClose();
    case OPEN:   return history.get(item).getAdjustedOpen();
    case HIGH:   return history.get(item).getAdjustedHigh();
    case LOW:    return history.get(item).getAdjustedLow();
    case VOLUME: return history.get(item).getVolumeLots();
    default: throw new IllegalStateException(String.valueOf(fieldID));
    }
  }
  /** Auxiliary function for toString method **/
  protected String capitalize(StockDataPoint.FieldID fieldID) {
    StringBuffer buf = new StringBuffer(String.valueOf(fieldID));
    buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
    for (int i = 1; i < buf.length(); i++)
      buf.setCharAt(i, Character.toLowerCase(buf.charAt(i)));
    return buf.toString();
  }
}
