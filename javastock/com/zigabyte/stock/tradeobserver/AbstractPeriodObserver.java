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
import java.util.*;
import java.text.*;
import java.io.*;

/** Observes a trading account and calls {@link #periodStarted periodStarted}
    when orders are completed at the first trading day of each specified
    calendar period (such as monthly).**/
public abstract class AbstractPeriodObserver extends TradeObserverAdapter {
  //// FIELDS
  int unitCount;
  int calendarUnit;
  boolean align;
  Date currentPeriodStart = null, lastDate = null, nextPeriodStart = null;

  //// CONSTRUCTORS
  /** Creates an unaligned periodic observer.
      @param unitCount number of calendar units in period
      @param calendarUnit unit counted, such as {@link Calendar#YEAR}
      or {@link Calendar#MONTH} or {@link Calendar#WEEK_OF_YEAR} or
      {@link Calendar#DATE}.
  **/
  public AbstractPeriodObserver(int unitCount, int calendarUnit) {
    this(unitCount, calendarUnit, false);
  }
  /** Creates a periodic observer.
      @param unitCount number of calendar units in period
      @param calendarUnit unit counted, such as {@link Calendar#YEAR}
      or {@link Calendar#MONTH} or {@link Calendar#WEEK_OF_YEAR} or
      {@link Calendar#DATE}.
      @param align whether to align to calendar unit, so for example
      monthly periods start on 1st day of month.
  **/
  public AbstractPeriodObserver(int unitCount, int calendarUnit, boolean align){
    this.unitCount = unitCount;
    this.calendarUnit = calendarUnit;
    this.align = align;
  }
  
  /** Write initial cash balance. **/
  public void initialized(Date date, TradingAccount account) {
    this.currentPeriodStart = date;
    this.lastDate = date;
    this.nextPeriodStart = nextPeriodDate(alignDate(date));
  }
  /** Calls {@link #periodStarted periodStarted}
      if it is the beginning of a new period. **/
  public void ordersCompleted(Date date, TradingAccount account) {
    if (this.nextPeriodStart.getTime() <= date.getTime()) { 
      Date lastPeriodStart = this.currentPeriodStart;
      Date lastPeriodEnd = this.lastDate;
      this.nextPeriodStart = nextPeriodDate(alignDate(date));
      this.currentPeriodStart = date;
      this.lastDate = date;
      periodStarted(lastPeriodStart, lastPeriodEnd, date, account);
    } else { 
      // update value for current period.
      this.lastDate = date;
    }
  }
  /** Called at the beginning of a new period. **/
  protected abstract void periodStarted(Date lastPeriodStartDate,
					Date lastPeriodEndDate,
					Date newPeriodStartDate,
					TradingAccount account);

  public Date getCurrentPeriodStartDate() {
    return this.currentPeriodStart;
  }
  public Date getLastDate() {
    return this.lastDate;
  }
  public Date getNextPeriodStartDate() {
    return this.nextPeriodStart;
  }

  private Date nextPeriodDate(Date date) {
    // report new balances only if count units have passed.
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(this.calendarUnit, this.unitCount);
    // set time of day to zero in case there are daylight/summer time shifts.
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }
  private Date alignDate(Date date) {
    if (!this.align) {
      return date;
    } else { 
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      // fall thru to smaller units
      switch(calendarUnit) {
      case Calendar.YEAR:
	calendar.set(Calendar.MONTH, Calendar.JANUARY);
      case Calendar.MONTH:
      case Calendar.WEEK_OF_YEAR:
	if (calendarUnit == Calendar.MONTH) { 
	  calendar.set(Calendar.DAY_OF_MONTH, 1);
	} else {
	  calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	}
      case Calendar.DAY_OF_MONTH: // same as Calendar.DATE
	calendar.set(Calendar.HOUR, 0);
      case Calendar.HOUR:
	calendar.set(Calendar.MINUTE, 0);
      case Calendar.MINUTE:
	calendar.set(Calendar.SECOND, 0);
      case Calendar.SECOND:
	calendar.set(Calendar.MILLISECOND, 0);
      }
      return calendar.getTime();
    }
  }
}
