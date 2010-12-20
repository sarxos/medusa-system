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

public class MovingDollarVolumeTests extends AbstractMovingIndicatorTests {
  public static void main(String[] ignore) {
    // Run: -s thisSuite, -oFBAR means report: -o: to System.out,
    //  F: failed test, B: aborted suite, A: aborted run, R: run completed
    Runner.main(new String[]
      {"-oFBAR",
       "-s","com.zigabyte.stock.indicator.tests.MovingDollarVolumeTests"});
  }
  // TEST DATA
  static int[] MOVING_WINDOW_SIZES = {1,2,3,4};
  static DefaultStockHistory HISTORY =
    new DefaultStockHistory("HST1","History");
  static { 
    Date[] nov = new Date[31];
    for (int i = 1; i <= 30; i++)
      nov[i] = new GregorianCalendar(2004, Calendar.NOVEMBER, i).getTime();
    HISTORY.add(new DefaultStockDataPoint(nov[1], 1, 1, 1, 1, 1000));
    HISTORY.add(new DefaultStockDataPoint(nov[2], 2, 2, 2, 2, 2000));
    HISTORY.add(new DefaultStockDataPoint(nov[3], 3, 3, 3, 3, 3000));
    HISTORY.add(new DefaultStockDataPoint(nov[4], 4, 4, 4, 4, 4000));
    HISTORY.add(new DefaultStockDataPoint(nov[5], 5, 5, 5, 5, 5000));
    HISTORY.add(new DefaultStockDataPoint(nov[8], 6, 6, 6, 6, 6000));
    HISTORY.add(new DefaultStockDataPoint(nov[9], 7, 7, 7, 7, 7000));
    HISTORY.add(new DefaultStockDataPoint(nov[10],8, 8, 8, 8, 8000));
    HISTORY.add(new DefaultStockDataPoint(nov[12],9, 9, 9, 9, 9000));
  }
  // CONSTRUCTOR
  public MovingDollarVolumeTests() {
    super(MOVING_WINDOW_SIZES, HISTORY);
  }
  // METHODS required by AbstractMovingIndicatorTests
  protected Indicator createIndicator(int movingWindowSize) {
    return new MovingDollarVolume(movingWindowSize);
  }
  protected Indicator createIndicator(int movingWindowSize, boolean isCalDay) {
    return new MovingDollarVolume(movingWindowSize, isCalDay);
  }

  protected double tradeDaysExpect(int movingWindowSize, int item) {
    double total = 0.0;
    for (int i = 0; i < movingWindowSize; i++)
      total += (history.get(item - i).getAdjustedClose() *
		history.get(item - i).getVolumeLots());
    return total;
  }

  protected double calendarDaysExpect(int movingWindowSize,int item) {
    double total = 0.0;
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(history.get(item).getDate());
    for (int i = 0; i < movingWindowSize; i++) { 
      Date date = calendar.getTime();
      if (histories.hasTradingData(date)) { 
	total += (history.get(date).getAdjustedClose() *
		  history.get(date).getVolumeLots());
      }
      calendar.add(Calendar.DAY_OF_MONTH, -1);
    }
    return total;
  }
}
