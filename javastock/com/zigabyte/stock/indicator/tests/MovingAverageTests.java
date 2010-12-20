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

public class MovingAverageTests extends AbstractMovingIndicatorTests {
  public static void main(String[] ignore) {
    // Run: -s thisSuite, -oFBAR means report: -o: to System.out,
    //  F: failed test, B: aborted suite, A: aborted run, R: run completed
    Runner.main(new String[]
      {"-oFBAR","-s","com.zigabyte.stock.indicator.tests.MovingAverageTests"});
  }
  // TEST DATA
  static final int[] MOVING_WINDOW_SIZES = {1,2,3,4};
  static final DefaultStockHistory HISTORY =
    new DefaultStockHistory("HST1","History");
  static { 
    Date[] nov = new Date[31];
    for (int i = 1; i <= 30; i++)
      nov[i] = new GregorianCalendar(2004, Calendar.NOVEMBER, i).getTime();
    HISTORY.add(new DefaultStockDataPoint(nov[1], 115, 119, 111, 115, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[2], 125, 129, 121, 125, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[3], 135, 139, 131, 135, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[4], 145, 149, 141, 145, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[5], 155, 159, 151, 155, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[8], 165, 169, 161, 165, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[9], 175, 179, 171, 175, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[10],185, 189, 181, 185, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[12],195, 199, 191, 195, 1000));
  }
  // CONSTRUCTOR
  public MovingAverageTests() {
    super(MOVING_WINDOW_SIZES, HISTORY);
  }

  // METHODS required by AbstractMovingIndicatorTests
  protected Indicator createIndicator(int movingWindowSize) {
    return new MovingAverage(movingWindowSize);
  }
  protected Indicator createIndicator(int movingWindowSize, boolean isCalDay) {
    return new MovingAverage(movingWindowSize, isCalDay);
  }

  protected double tradeDaysExpect(int movingWindowSize, int item) {
    double total = 0.0;
    for (int i = 0; i < movingWindowSize; i++)
      total += this.history.get(item - i).getAdjustedClose();
    return total / movingWindowSize;
  }

  protected double calendarDaysExpect(int movingWindowSize,int item) {
    double total = 0.0;
    int count = 0;
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(history.get(item).getDate());
    for (int i = 0; i < movingWindowSize; i++) { 
      if (histories.hasTradingData(calendar.getTime())) { 
	total += history.get(calendar.getTime()).getAdjustedClose();
	count++;
      }
      calendar.add(Calendar.DAY_OF_MONTH, -1);
    }
    return total/count;
  }
}
