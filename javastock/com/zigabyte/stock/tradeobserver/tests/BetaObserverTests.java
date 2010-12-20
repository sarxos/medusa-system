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

public class BetaObserverTests extends Suite {
  public static void main(String[] ignore) {
    // Run: -s thisSuite, -oFBAR means report: -o: to System.out,
    //  F: failed test, B: aborted suite, A: aborted run, R: run completed
    Runner.main(new String[]
      {"-oFBAR", "-s",
       "com.zigabyte.stock.tradeobserver.tests.BetaObserverTests"});
  }
  // TEST DATA
  static OrderTiming NDO = OrderTiming.NEXT_DAY_OPEN;
  static Date NOV1 = new GregorianCalendar(2004,Calendar.NOVEMBER,1).getTime();
  static Date NOV2 = new GregorianCalendar(2004,Calendar.NOVEMBER,2).getTime();
  static Date NOV3 = new GregorianCalendar(2004,Calendar.NOVEMBER,3).getTime();
  static Date NOV4 = new GregorianCalendar(2004,Calendar.NOVEMBER,4).getTime();
  
  public int getTestCount() {
    return super.getTestCount() + 3*6;
  }

  public void executeTestMethods(Reporter reporter) {
    super.executeTestMethods(reporter);
    constantPlus1Test(reporter);
    constantMinus2Test(reporter);
    mimic1Test(reporter);
    mimic1000Test(reporter);
    offsetPlus1Test(reporter);
    offsetMinus2Test(reporter);
  }

  public void constantPlus1Test(Reporter reporter) {
    StockDataPoint[] index = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,0),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,0),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,0),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,0)
    };
    // prices are constantly increasing by 1.00
    StockDataPoint[] prices = {
      new DefaultStockDataPoint(NOV1,1.00f,1.00f,1.00f,1.00f,1000),
      new DefaultStockDataPoint(NOV2,2.00f,2.00f,2.00f,2.00f,1000),
      new DefaultStockDataPoint(NOV3,3.00f,3.00f,3.00f,3.00f,1000),
      new DefaultStockDataPoint(NOV4,4.00f,4.00f,4.00f,4.00f,1000),
    };

    // Initial cash is enough to buy 1 share.
    // dPrice is constant 1.
    // Therefore intercept is 1, slope is 0.
    runTest(reporter, "constant+1", index, prices, 1.00, 
	    1.0, 0.0);
  }
  public void constantMinus2Test(Reporter reporter) {
    StockDataPoint[] index = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,0),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,0),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,0),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,0)
    };
    // prices are constantly decreasing by 2.00
    StockDataPoint[] prices = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,1000),
      new DefaultStockDataPoint(NOV2,8.00f,8.00f,8.00f,8.00f,1000),
      new DefaultStockDataPoint(NOV3,6.00f,6.00f,6.00f,6.00f,1000),
      new DefaultStockDataPoint(NOV4,4.00f,4.00f,4.00f,4.00f,1000),
    };

    // Initial cash is enough to buy 1 share.
    // dPrice is constant -2.
    // Therefore intercept is -2, slope is 0.
    runTest(reporter, "constant-2", index, prices, 10.00, 
	    -2.0, 0.0);
  }
  public void mimic1Test(Reporter reporter) {
    StockDataPoint[] index = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,0),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,0),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,0),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,0)
    };
    // prices mimic index
    StockDataPoint[] prices = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,1000),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,1000),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,1000),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,1000)
    };

    // Initial cash is enough to buy one share.
    // History has price = index, so always dValue = dIndex.
    // Therefore intercept is 0, slope is 1.
    runTest(reporter, "mimic1", index, prices, 10.00, 
	    0.0, 1.0);
  }
  public void mimic1000Test(Reporter reporter) {
    StockDataPoint[] index = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,0),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,0),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,0),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,0)
    };
    // prices mimic index
    StockDataPoint[] prices = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,1000),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,1000),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,1000),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,1000)
    };

    // Initial cash is enough to buy 1000 shares.
    // History has price = index, so always dValue = 1000 * dIndex.
    // Therefore intercept is 0, slope is 1000.
    runTest(reporter, "mimic1000", index, prices, 10000.00, 
	    0.0, 1000.0);
  }

  public void offsetPlus1Test(Reporter reporter) {
    StockDataPoint[] index = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,0),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,0),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,0),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,0)
    };
    // prices mimic index, plus increasing by 1
    StockDataPoint[] prices = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,1000),
      new DefaultStockDataPoint(NOV2,21.00f,21.00f,21.00f,21.00f,1000),
      new DefaultStockDataPoint(NOV3,52.00f,52.00f,52.00f,52.00f,1000),
      new DefaultStockDataPoint(NOV4,33.00f,33.00f,33.00f,33.00f,1000)
    };

    // Initial cash is enough to buy 1 share.
    // History has price = Index + day, so dValue = dIndex + 1
    // Therefore intercept is 0, slope is 1000.
    runTest(reporter, "offset+1", index, prices, 10.00, 
	    1.0, 1.0);
  }
  public void offsetMinus2Test(Reporter reporter) {
    StockDataPoint[] index = {
      new DefaultStockDataPoint(NOV1,10.00f,10.00f,10.00f,10.00f,0),
      new DefaultStockDataPoint(NOV2,20.00f,20.00f,20.00f,20.00f,0),
      new DefaultStockDataPoint(NOV3,50.00f,50.00f,50.00f,50.00f,0),
      new DefaultStockDataPoint(NOV4,30.00f,30.00f,30.00f,30.00f,0)
    };
    // prices mimic index, plus increasing by 1
    StockDataPoint[] prices = {
      new DefaultStockDataPoint(NOV1,19.00f,19.00f,19.00f,19.00f,1000),
      new DefaultStockDataPoint(NOV2,27.00f,27.00f,27.00f,27.00f,1000),
      new DefaultStockDataPoint(NOV3,55.00f,55.00f,55.00f,55.00f,1000),
      new DefaultStockDataPoint(NOV4,33.00f,33.00f,33.00f,33.00f,1000)
    };

    // Initial cash is enough to buy 1 share.
    // History has price = Index + day, so dValue = dIndex + 1
    // Therefore intercept is 0, slope is 1000.
    runTest(reporter, "offset-2", index, prices, 19.00, 
	    -2.0, 1.0);
  }


  private void runTest(Reporter reporter, String name,
		       StockDataPoint[] indexData, StockDataPoint[] priceData,
		       double initialCash, 
		       double expectAlpha, double expectBeta) {
    // setup index history 
    DefaultStockMarketHistory histories = new DefaultStockMarketHistory();
    DefaultStockHistory spHistory = new DefaultStockHistory("$SP","$SP");
    for (StockDataPoint indexDatum : indexData) {
      spHistory.add(indexDatum);
    }
    histories.add(spHistory);
    // setup stock history to match prices
    DefaultStockHistory abcHistory = new DefaultStockHistory("ABC","ABC");
    for (StockDataPoint priceDatum : priceData) {
      abcHistory.add(priceDatum);
    }
    histories.add(abcHistory);

    // set up account
    DefaultTradingAccount account =
      new DefaultTradingAccount(histories, 0.00, 0.00);
    BetaObserver obs = new BetaObserver("$SP");
    account.addTradeObserver(obs);

    // run 
    account.initialize(priceData[0].getDate(), initialCash);
    account.buyStock("ABC", (int)(initialCash/priceData[0].getAdjustedOpen()),
		     OrderTiming.NEXT_DAY_OPEN, 0);
    account.executeOrders(priceData[0].getDate());

    for (StockDataPoint indexDatum : indexData) {
      account.executeOrders(indexDatum.getDate());
    }
    // verify result
    reportEquals(reporter, name+" alpha",
		 expectAlpha, obs.computeAlpha());
    reportEquals(reporter, name+" beta",
		 expectBeta, obs.computeBeta());
    reportEquals(reporter, name+" count",
		 indexData.length, obs.getDataCount());
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
