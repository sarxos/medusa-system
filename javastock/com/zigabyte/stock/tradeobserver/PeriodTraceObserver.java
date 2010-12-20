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

/** Observes a trading account and periodically writes a summary of
    the account value.

    @see TradeTraceObserver
**/
public class PeriodTraceObserver extends AbstractPeriodObserver {
  //// CONSTANT FORMATTERS
  static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMMyyyy");
  static final NumberFormat DOLLAR_FORMAT = new DecimalFormat("$#,##0.00");

  //// FIELDS
  double lastCashBalance = Double.NaN, lastStockValue = Double.NaN;
  PrintWriter writer;

  //// CONSTRUCTORS
  /** Creates an unaligned periodic observer that outputs to {@link System#out}.
      @param unitCount number of calendar units in period
      @param calendarUnit unit counted, such as {@link Calendar#YEAR}
      or {@link Calendar#MONTH} or {@link Calendar#WEEK_OF_YEAR} or
      {@link Calendar#DATE}.
  **/
  public PeriodTraceObserver(int unitCount, int calendarUnit) {
    this(unitCount, calendarUnit, false,
	 new PrintWriter(new OutputStreamWriter(System.out), true));
  }
  /** Creates a periodic observer that outputs to {@link System#out}.
      @param unitCount number of calendar units in period
      @param calendarUnit unit counted, such as {@link Calendar#YEAR}
      or {@link Calendar#MONTH} or {@link Calendar#WEEK_OF_YEAR} or
      {@link Calendar#DATE}.
      @param align whether to align to calendar unit, so for example
      monthly periods start on 1st day of month.
  **/
  public PeriodTraceObserver(int unitCount, int calendarUnit, boolean align) {
    this(unitCount, calendarUnit, align,
	 new PrintWriter(new OutputStreamWriter(System.out), true));
  }
  /** Creates a trace observer that outputs to writer
      @param unitCount number of calendar units in period
      @param calendarUnit unit counted, such as {@link Calendar#YEAR}
      or {@link Calendar#MONTH} or {@link Calendar#WEEK_OF_YEAR} or
      {@link Calendar#DATE}.
      @param align whether to align to calendar unit, so for example
      monthly periods start on 1st day of month.
      @param writer where to write trace. **/
  public PeriodTraceObserver(int unitCount, int calendarUnit, boolean align,
			     PrintWriter writer) {
    super(unitCount, calendarUnit, align);
    this.writer = writer;
  }
  
  /** Write initial cash balance and save value. **/
  public void initialized(Date date, TradingAccount account) {
    super.initialized(date, account);
    this.lastCashBalance = account.getCurrentCashBalance();
    this.lastStockValue = account.getCurrentStockValue();
    writer.println(DATE_FORMAT.format(date)+": "+
		   "cash="+DOLLAR_FORMAT.format(this.lastCashBalance));
  }
  /** update values for current period. **/
  public void ordersCompleted(Date date, TradingAccount account) {
    super.ordersCompleted(date, account);
    // update values for current period, after super (may call periodStarted).
    this.lastCashBalance = account.getCurrentCashBalance();
    this.lastStockValue = account.getCurrentStockValue();
    this.lastDate = date;
  }
  /** Write [DATE]: cash=[CASHVALUE], stocks=[STOCKVALUE], total=[TOTALVALUE]
      based on values for last period. **/
  protected void periodStarted(Date lastPeriodStartDate, Date lastPeriodEndDate,
			       Date tradingDate, TradingAccount account) {
    // reached beginning of next period, so write values for last period.
    double total = this.lastCashBalance + this.lastStockValue;
    this.writer.println
      (DATE_FORMAT.format(lastDate)+": "+
       "cash="+DOLLAR_FORMAT.format(this.lastCashBalance)+", "+
       "stocks="+DOLLAR_FORMAT.format(this.lastStockValue)+", "+
       "total="+DOLLAR_FORMAT.format(total));
  }
}
