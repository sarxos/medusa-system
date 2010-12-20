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

public class TradeWinLossObserverTests extends Suite {
  public static void main(String[] ignore) {
    // Run: -s thisSuite, -oFBAR means report: -o: to System.out,
    //  F: failed test, B: aborted suite, A: aborted run, R: run completed
    Runner.main(new String[]
      {"-oFBAR", "-s",
       "com.zigabyte.stock.tradeobserver.tests.TradeWinLossObserverTests"});
  }
  // TEST DATA
  static OrderTiming NDO = OrderTiming.NEXT_DAY_OPEN;
  static Date NOV1 = new GregorianCalendar(2004,Calendar.NOVEMBER,1).getTime();
  static Date NOV2 = new GregorianCalendar(2004,Calendar.NOVEMBER,2).getTime();
  static Date NOV3 = new GregorianCalendar(2004,Calendar.NOVEMBER,3).getTime();
  static Date NOV4 = new GregorianCalendar(2004,Calendar.NOVEMBER,4).getTime();
  static Date NOV5 = new GregorianCalendar(2004,Calendar.NOVEMBER,5).getTime();
  static Date NOV6 = new GregorianCalendar(2004,Calendar.NOVEMBER,6).getTime();

  public int getTestCount() {
    return super.getTestCount() + 5*9;
  }

  public void executeTestMethods(Reporter reporter) {
    super.executeTestMethods(reporter);
    noFeesTest1W(reporter); feesTest1W(reporter);
    noFeesTest1L(reporter); feesTest1L(reporter);
    noFeesTest1E(reporter); feesTest1E(reporter);
    noFeesTest2W(reporter);
    noFeesTest2L(reporter);
    noFeesTest1W1L(reporter);
  }

  public void noFeesTest1W(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,NOV2,20.00)
    };
    runTest(reporter, "noFees1W", 0.00, 0.00, 10000.00, orders,
	    1, 0, 0, 1000.00, 0.00);
  }
  public void feesTest1W(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,NOV2,20.00)
    };
    runTest(reporter, "fees1W", 10.00, 0.01, 10000.00, orders,
	    1, 0, 0, 978.00, 0.00);
  }
  public void noFeesTest1L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,20.00,NDO,0,NOV1,20.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,NOV2,10.00)
    };
    runTest(reporter, "noFees1L", 0.00, 0.00, 10000.00, orders,
	    0, 1, 0, 0.00, -1000.00);
  }
  public void feesTest1L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,20.00,NDO,0,NOV1,20.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,NOV2,10.00)
    };
    runTest(reporter, "fees1L", 10.00, 0.01, 10000.00, orders,
	    0, 1, 0, 0.00, -1022.00);
  }
  public void noFeesTest1E(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,NOV2,10.00)
    };
    runTest(reporter, "noFees1E", 0.00, 0.00, 10000.00, orders,
	    0, 0, 1, 0.00, 0.00);
  }
  public void feesTest1E(Reporter reporter) {
    TradeOrder[] orders = { // avoid fractions (rounding errors)
      new TradeOrder(TradeType.BUY, "ABC",1,1000,NDO,0,NOV5,1000),
      new TradeOrder(TradeType.SELL,"ABC",1,1022,NDO,0,NOV6,1022)
    };
    runTest(reporter, "fees1E", 10.00, 1.00, 10000.00, orders,
	    0, 0, 1, 0.00, 0.00);
  }
  public void noFeesTest2W(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,NOV2,20.00),
      new TradeOrder(TradeType.BUY, "ABC",100,30.00,NDO,0,NOV3,30.00),
      new TradeOrder(TradeType.SELL,"ABC",100,50.00,NDO,0,NOV4,50.00)
    };
    runTest(reporter, "noFees2w", 0.00, 0.00, 10000.00, orders,
	    2, 0, 0, 1500.00, 0.00);
  }
  public void noFeesTest2L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,20.00,NDO,0,NOV1,20.00),
      new TradeOrder(TradeType.SELL,"ABC",100,10.00,NDO,0,NOV2,10.00),
      new TradeOrder(TradeType.BUY, "ABC",100,50.00,NDO,0,NOV3,50.00),
      new TradeOrder(TradeType.SELL,"ABC",100,30.00,NDO,0,NOV4,30.00)
    };
    runTest(reporter, "noFees2L", 0.00, 0.00, 10000.00, orders,
	    0, 2, 0, 0.00, -1500.00);
  }
  public void noFeesTest1W1L(Reporter reporter) {
    TradeOrder[] orders = {
      new TradeOrder(TradeType.BUY, "ABC",100,10.00,NDO,0,NOV1,10.00),
      new TradeOrder(TradeType.SELL,"ABC",100,20.00,NDO,0,NOV2,20.00),
      new TradeOrder(TradeType.BUY, "ABC",100,50.00,NDO,0,NOV3,50.00),
      new TradeOrder(TradeType.SELL,"ABC",100,30.00,NDO,0,NOV4,30.00)
    };
    runTest(reporter, "noFees1W1L", 0.00, 0.00, 10000.00, orders,
	    1, 1, 0, 1000.00, -2000.00);
  }


  private void runTest(Reporter reporter, String name,
		       double perTradeFee, double perShareCommission,
		       double initialCash, TradeOrder[] orders,
		       int expectWinCount, int expectLoseCount,
		       int expectEvenCount,
		       double expectAvgWinProfit,
		       double expectAvgLoseLoss) {
    // setup history to match test orders
    DefaultStockMarketHistory histories = new DefaultStockMarketHistory();
    DefaultStockHistory abcHistory = new DefaultStockHistory("ABC","ABC");
    for (TradeOrder order : orders) {
      float price = (float) order.getExecutedPrice();
      abcHistory.add(new DefaultStockDataPoint
		     (order.getExecutedDate(),
		      price, price, price, price, 1000));
    }
    histories.add(abcHistory);

    // set up account
    DefaultTradingAccount account =
      new DefaultTradingAccount(histories, perTradeFee, perShareCommission);
    TradeWinLossObserver obs = new TradeWinLossObserver();
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
		 expectWinCount, obs.getWinningTradeCount());
    reportEquals(reporter, name+" losingTradeCount",
		 expectLoseCount, obs.getLosingTradeCount());
    reportEquals(reporter, name+" evenTradeCount",
		 expectEvenCount, obs.getEvenTradeCount());
    reportEquals(reporter, name+" averageWinningTradeProfit",
		 expectAvgWinProfit, obs.getAverageWinningTradeProfit());
    reportEquals(reporter, name+" averageLosingTradeLoss",
		 expectAvgLoseLoss, obs.getAverageLosingTradeLoss());
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
}
