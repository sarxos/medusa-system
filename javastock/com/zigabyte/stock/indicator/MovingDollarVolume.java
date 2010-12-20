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

/** Compute the total of (volume * price) over the last dayCount days. **/
public class MovingDollarVolume extends AbstractMovingIndicator {
  //// CONSTRUcTORS
  /** @see AbstractMovingIndicator#AbstractMovingIndicator(int) **/
  public MovingDollarVolume(int dayCount) {
    super(dayCount);
  }
  /** @see AbstractMovingIndicator#AbstractMovingIndicator(int,boolean) **/
  public MovingDollarVolume(int dayCount, boolean countCalendarDays) {
    super(dayCount, countCalendarDays);
  }
  /** @see AbstractMovingIndicator#AbstractMovingIndicator(int,boolean,StockDataPoint.FieldID) **/
  public MovingDollarVolume(int dayCount, boolean countCalendarDays,
			    StockDataPoint.FieldID fieldID) {
    super(dayCount, countCalendarDays, fieldID);
  }

  //// AbstractMovingIndicator
  /** Result is initially zero **/
  public double initialCombinationValue() {
    return 0.0;
  } 
  /** Return price * volume of history item **/
  public double getItemValue(StockHistory history, int item) {
    double price = getField(history, item, this.fieldID);
    double volume = getField(history, item, StockDataPoint.FieldID.VOLUME);
    return price * volume;
  }
  /** Combine by summing item values. **/
  public double combine(double combination, double datum) {
    return combination + datum;
  }

  //// OBJECT toString
  public String toString() {
    return super.toString("TTL$Vol");
  }
}
