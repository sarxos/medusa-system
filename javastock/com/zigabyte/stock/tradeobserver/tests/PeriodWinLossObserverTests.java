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
package com.zigabyte.stock.tradeobserver.tests;

import com.zigabyte.stock.tradeobserver.*;
import com.zigabyte.stock.data.*;
import com.zigabyte.stock.trade.*;

import org.suiterunner.*;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

public class PeriodWinLossObserverTests extends Suite {
  public static void main(String[] ignore) {
    // Run: -s thisSuite, -oFBAR means report: -o: to System.out,
    //  F: failed test, B: aborted suite, A: aborted run, R: run completed
    Runner.main(new String[]
      {"-oFBAR", "-s",
       "com.zigabyte.stock.tradeobserver.tests.PeriodWinLossObserverTests"});
  }
  // TEST DATA
  static OrderTiming NDO = OrderTiming.NEXT_DAY_OPEN;
  static Date NOV1 = new GregorianCalendar(2004,Calendar.NOVEMBER,1).getTime();
  static Date NOV2 = new GregorianCalendar(2004,Calendar.NOVEMBER,2).getTime();
  static Date DEC1 = new GregorianCalendar(2004,Calendar.DECEMBER,1).getTime();
  static Date DEC2 = new GregorianCalendar(2004,Calendar.DECEMBER,2).getTime();
  static Date JAN1 = new GregorianCalendar(2005,Calendar.JANUARY, 1).getTime();
  static Date JAN3 = new GregorianCalendar(2005,Calendar.JANUARY, 3).getTime();
  static Date JAN4 = new GregorianCalendar(2005,Calendar.JANUARY, 4).getTime();
  static Date FEB1 = new GregorianCalendar(2005,Calendar.FEBRUARY,1).getTime();
  static Date FEB2 = new GregorianCalendar(2005,Calendar.FEBRUARY,2).getTime();
  
  public int getTestCount() {
    return super.getTestCount() + 6*7;
  }

  public void executeTestMethods(Reporter reporter) {
    super.executeTestMethods(reporter);
    monthTest1W(reporter); 
    monthTest1L(reporter); 
    monthTest1E(reporter); 
    monthTest2W(reporter);
    monthTest2L(reporter);
    monthTest1W1L(reporter);
    monthTest2W2L(reporter);
  }

  public void monthTest1W(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,DEC1,20.00)
    };
    runTest(reporter, "month1W", 10000.00, orders,
	    1, 0, DEC1, null, 1000.00, 0.00);
  }
  public void monthTest1L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,20.00,NDO,0,NOV1,20.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,DEC1,10.00)
    };
    runTest(reporter, "month1L", 10000.00, orders,
	    0, 1, null, DEC1, 0.00, -1000.00);
  }
  public void monthTest1E(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,DEC1,10.00)
    };
    runTest(reporter, "month1E", 10000.00, orders,
	    0, 0, null, null, 0.00, 0.00);
  }
  public void monthTest2W(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,NOV2,20.00),
      new TradeOrder(TradeType.BUY, "ABC",100,30.00,NDO,0,DEC2,30.00),
      new TradeOrder(TradeType.SELL,"ABC",100,50.00,NDO,0,JAN3,50.00)
    };
    runTest(reporter, "month2w", 10000.00, orders,
	    2, 0, JAN3, null, 2000.00, 0.00);
  }
  public void monthTest2L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,20.00,NDO,0,NOV1,20.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,NOV2,10.00),
      new TradeOrder(TradeType.BUY, "ABC",100,50.00,NDO,0,DEC2,50.00),
      new TradeOrder(TradeType.SELL,"ABC",100,30.00,NDO,0,JAN3,30.00)
    };
    runTest(reporter, "month2L", 10000.00, orders,
	    0, 2, null, JAN3, 0.00, -2000.00);
  }
  public void monthTest1W1L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,NOV2,20.00),
      new TradeOrder(TradeType.BUY, "ABC",100,50.00,NDO,0,DEC2,50.00),
      new TradeOrder(TradeType.SELL,"ABC",100,30.00,NDO,0,JAN3,30.00)
    };
    runTest(reporter, "month1W1L", 10000.00, orders,
	    1, 1, NOV1, JAN3, 1000.00, -2000.00);
  }
  public void monthTest2W2L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,30.00,NDO,0,NOV2,30.00),
      new TradeOrder(TradeType.BUY, "ABC",100,40.00,NDO,0,DEC1,40.00),
      new TradeOrder(TradeType.SELL,"ABC",100,50.00,NDO,0,DEC2,50.00),
      new TradeOrder(TradeType.BUY, "ABC",100,40.00,NDO,0,JAN3,40.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,JAN4,10.00),
      new TradeOrder(TradeType.BUY, "ABC",100,30.00,NDO,0,FEB1,30.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,FEB2,20.00)
    };
    runTest(reporter, "month2W2L", 10000.00, orders,
	    2, 2, NOV1, JAN3, 2000.00, -3000.00);
  }


  private void runTest(Reporter reporter, String name,
		       double initialCash, TradeOrder[] orders,
		       int expectWinCount, int expectLoseCount,
		       Date expectBestPeriodStart, Date expectWorstPeriodStart,
		       double expectBestPeriodProfit,
		       double expectWorstPeriodLoss) {
    // setup history to match test orders
    DefaultStockMarketHistory histories = new DefaultStockMarketHistory();
    DefaultStockHistory abcHistory = new DefaultStockHistory("ABC","ABC");
    for (TradeOrder order : orders) {
      float price = (float) order.getExecutedPrice();
      assert order.getExecutedDate() != null : order;
      abcHistory.add(new DefaultStockDataPoint
		     (order.getExecutedDate(),
		      price, price, price, price, 1000));
    }
    histories.add(abcHistory);

    // set up account
    DefaultTradingAccount account =
      new DefaultTradingAccount(histories, 0.00, 0.00);
    PeriodWinLossObserver obs =
      new PeriodWinLossObserver(1, Calendar.MONTH, true);
    account.addTradeObserver(obs);

    // run 
    account.initialize(orders[0].getExecutedDate(), initialCash);
    for (TradeOrder order : orders) {
      switch(order.type) {
      case BUY:
	account.buyStock(order.symbol,  order.shares,
			 order.orderTiming, order.limit);
	break;
      case SELL:
	account.sellStock(order.symbol, order.shares,
			  order.orderTiming, order.limit);
	break;
      }
      account.executeOrders(order.getExecutedDate());
    }
    // verify result
    reportEquals(reporter, name+" winningTradeCount",
		 expectWinCount, obs.getWinningPeriodCount());
    reportEquals(reporter, name+" losingTradeCount",
		 expectLoseCount, obs.getLosingPeriodCount());
    reportEquals(reporter, name+" bestPeriodStartDate",
		 expectBestPeriodStart, obs.getBestProfitPeriodStartDate());
    reportEquals(reporter, name+" worstPeriodStartDate",
		 expectWorstPeriodStart, obs.getWorstLossPeriodStartDate());
    reportEquals(reporter, name+" bestWinningPeriodProfit",
		 expectBestPeriodProfit, obs.getBestPeriodProfit());
    reportEquals(reporter, name+" worstLosingPeriodLoss",
		 expectWorstPeriodLoss, obs.getWorstPeriodLoss());
  }
  private void reportEquals(Reporter reporter, String testName,
			    int expect, int result) {
    reporter.testStarting(new Report(this, testName, ""));
    String msg = "result="+result+" expect="+expect;
    if (result == expect)
      reporter.testSucceeded(new Report(this, testName, msg));
    else
      reporter.testFailed(new Report(this, testName, msg));
  }
  private void reportEquals(Reporter reporter, String testName,
			    double expect, double result) {
    reporter.testStarting(new Report(this, testName, ""));
    String msg = "result="+result+" expect="+expect;
    if (result == expect)
      reporter.testSucceeded(new Report(this, testName, msg));
    else
      reporter.testFailed(new Report(this, testName, msg));
  }
  private void reportEquals(Reporter reporter, String testName,
			    Date expect, Date result) {
    reporter.testStarting(new Report(this, testName, ""));
    String msg = "result="+formatDate(result)+" expect="+formatDate(expect);
    if (equals(result, expect))
      reporter.testSucceeded(new Report(this, testName, msg));
    else
      reporter.testFailed(new Report(this, testName, msg));
  }
  private String formatDate(Date date) {
    if (date == null) return "null";
    else return DATE_FORMAT.format(date);
  }
  private boolean equals(Object o1, Object o2) {
    return o1 == null? o1 == o2 : o1.equals(o2);
  }
  private static final SimpleDateFormat DATE_FORMAT =
    new SimpleDateFormat("ddMMMyyyy");
  
}
