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

import java.util.*;

/** Combines a value from {@link StockDataPoint}s over the last n days.
    Days may be counted using either trading days or calendar days. **/
public abstract class AbstractMovingIndicator extends AbstractIndicator {
  //// FIELDS
  /** Number of days to combine, ending with current day. **/
  protected int dayCount;
  /** If false, count trading days; if true,
      count calendar days, so there may be fewer trading days included
      if there is a holiday or weekend. **/
  protected boolean isCountCalendarDays;
  /** which field value to combine. **/
  protected StockDataPoint.FieldID fieldID;

  //// CONSTRUCTORS
  /** Combines closing values over n trading days. **/
  public AbstractMovingIndicator(int tradingDayCount) {
    this(tradingDayCount, false);
  }
  /** Combines closing values over n days, counting dayCount trading days
      if isCountCalendarDays is false, otherwise counting dayCount calendar
      days. **/
  public AbstractMovingIndicator(int dayCount, boolean isCountCalendarDays)
  {
    this(dayCount, isCountCalendarDays, StockDataPoint.FieldID.CLOSE);
  }
  /** @param dayCount number of days to combine, ending with current day.
      @param isCountCalendarDays If false, count trading days; if true,
      count calendar days, so there may be fewer trading days included
      if there is a holiday or weekend.
      @param fieldID which field value to combine. **/
  public AbstractMovingIndicator(int dayCount, boolean isCountCalendarDays,
				 StockDataPoint.FieldID fieldID) {
    if (dayCount <= 0)
      throw new IllegalArgumentException("dayCount: "+dayCount);
    this.dayCount = dayCount;
    this.fieldID = fieldID;
    this.isCountCalendarDays = isCountCalendarDays;
  }

  //// Interface INDICATOR
  /** Returns NaN if not enough days data available **/
  public double compute(StockHistory history, int item) {
    try { 
      if (!this.isCountCalendarDays) { 
	double combination = initialCombinationValue();
	for (int i = 0; i < this.dayCount; i++) {
	  // allow negative index to access history before subrange
	  double itemValue = getItemValue(history, item - i);
	  combination = combine(combination, itemValue);
	}
	return finalCombinationValue(combination, this.dayCount);
      } else {
	Calendar calendar = new GregorianCalendar();
	calendar.setTime(history.get(item).getDate());
	calendar.add(Calendar.DATE, -(this.dayCount - 1));
	long earlyLimit = calendar.getTimeInMillis();

	double combination = initialCombinationValue();
	int i = 0;
	long msec;
	while(earlyLimit <= (msec = history.get(item - i).getDate().getTime())){
	  // allow negative index to access history before subrange
	  double itemValue = getItemValue(history, item - i);
	  combination = combine(combination, itemValue);
	  i++;
	  if (msec == earlyLimit)
	    break; // done, don't access prior item, may not exist
	}
	return finalCombinationValue(combination, i);
      }
    } catch (IndexOutOfBoundsException e) {
      return Double.NaN; // not enough data available
    }
  }
  /** Compute initial value for combination (value for zero-day indicator).
      This implemenation returns zero. **/ 
  protected double initialCombinationValue() {
    return 0.0;
  }
  /** Get the value for this item.
      This implementation calls
      <pre>
        getField(history, item, this.fieldID);
      </pre> **/
  protected double getItemValue(StockHistory history, int item) {
    return getField(history, item, this.fieldID);
  }
  /** Combine the combination so far with item Value.
      Default implementation adds them. **/
  protected double combine(double combination, double itemValue) {
    return combination + itemValue;
  }
  /** Perform any final computations on the combination.
      Default implementation just returns the combination. **/
  protected double finalCombinationValue(double combination, int itemCount) {
    return combination;
  }

  //// OBJECT toString
  /** Returns dayCount + ("calDay" or "traDay") + shortFunctionName +
      capitalize(fieldId), depending on whether it is counting
      calendar days or trading days. **/
  protected String toString(String shortFunctionName) {
    return (dayCount + (isCountCalendarDays? "cal" : "tra")
	    +"Day"+shortFunctionName+capitalize(this.fieldID));
  }
  //// ACCESSORS
  public int getDayCount() {
    return this.dayCount;
  } 
  public StockDataPoint.FieldID getFieldID() {
    return this.fieldID;
  } 
  public boolean isCountCalendarDays() {
    return this.isCountCalendarDays;
  }
}
