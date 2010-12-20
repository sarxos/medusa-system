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
package com.zigabyte.stock.indicator.tests;

import com.zigabyte.stock.data.*;

import com.zigabyte.stock.indicator.*;
import org.suiterunner.*;

import java.util.*;

public abstract class AbstractMovingIndicatorTests extends Suite {
  // TEST DATA
  protected final String indicatorName;
  private final int[] movingWindowSizes;
  protected final DefaultStockMarketHistory histories = new DefaultStockMarketHistory();
  protected final StockHistory history;
  public AbstractMovingIndicatorTests(int[] movingWindowSizes,
				      StockHistory history) {
    this.movingWindowSizes = movingWindowSizes;
    this.history = history;
    histories.add(history);
    String className = createIndicator(1).getClass().getName();
    this.indicatorName = className.substring(className.lastIndexOf('.')+1);
  }
  // 
  public int getTestCount() {
    return super.getTestCount() + 2 * movingWindowSizes.length * history.size();
  }
  /** Add calls to methods with multiple tests **/
  public void executeTestMethods(Reporter reporter) { 
    super.executeTestMethods(reporter);
    for (boolean isCalDay : new boolean[]{false, true}) { 
      for (int movingWindowSize : movingWindowSizes) {
	for (int item = 0; item < history.size(); item++) {
	  String name =
	    "new "+indicatorName+"("+(movingWindowSize+","+
				      isCalDay)+").compute(h,"+item+")";
	  reporter.testStarting(new Report(this, name, ""));
	  try { 
	    if (!isCalDay) { 
	      if (item < movingWindowSize - 1) { 
		tradeDaysBadIndexTest(movingWindowSize, item);
	      } else {
		tradeDaysTest(movingWindowSize, item);
	      }
	    } else {
	      Calendar calendar = new GregorianCalendar();
	      calendar.setTime(history.get(item).getDate());
	      calendar.add(Calendar.DAY_OF_MONTH, -(movingWindowSize - 1));
	      if (history.getAtOrBefore(calendar.getTime()) == null) { 
		calendarDaysBadIndexTest(movingWindowSize, item);
	      } else {
		calendarDaysTest(movingWindowSize, item);
	      }
	    }	      
	    reporter.testSucceeded(new Report(this, name, ""));
	  } catch (Exception e) {
	    reporter.testFailed(new Report(this, name, "", e));
	  }
	}
      }
    }
  }
  protected abstract Indicator createIndicator(int movingWindowSize);
  protected abstract Indicator createIndicator(int movingWindowSize,
					       boolean isCalDay);

  private void tradeDaysBadIndexTest(int movingWindowSize, int item) {
    Indicator avgTraDay = createIndicator(movingWindowSize);
    double result = avgTraDay.compute(history, item);
    double expect = Double.NaN;
    verify(Double.isNaN(result), "result="+result+" expect="+expect);
  }

  private void tradeDaysTest(int movingWindowSize, int item) {
    Indicator avgTraDay = createIndicator(movingWindowSize);
    double result = avgTraDay.compute(history, item);
    double expect = tradeDaysExpect(movingWindowSize, item);
    verify(result == expect, "result="+result+" expect="+expect);
  }
  /** Compute expected value **/
  protected abstract double tradeDaysExpect(int movingWindowSize, int item);

  public void calendarDaysBadIndexTest(int movingWindowSize,int item) {
    Indicator avgCalDay = createIndicator(movingWindowSize, true);
    double result = avgCalDay.compute(history, item);
    double expect = Double.NaN;
    verify(Double.isNaN(result), "result="+result+" expect="+expect);
  }
  public void calendarDaysTest(int movingWindowSize,int item) {
    Indicator avgCalDay = createIndicator(movingWindowSize, true);
    double result = avgCalDay.compute(history, item);
    double expect = calendarDaysExpect(movingWindowSize, item);
    verify(result == expect,
	   "item"+item+": result="+result+" expect="+expect);
  }
  /** Compute expected value **/
  protected abstract double calendarDaysExpect(int movingWindowSize, int item);
}
