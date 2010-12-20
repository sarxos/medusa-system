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
package com.zigabyte.stock.sample;

import com.zigabyte.stock.data.*;
import com.zigabyte.stock.strategy.TradingStrategy;
import com.zigabyte.stock.trade.*;
import com.zigabyte.stock.indicator.*;

import java.util.*;

/** Sample Sunday strategy to buy stocks which have reached new highs in
    the last week, and sell stocks which have fallen steadily over the past
    13 weeks.   (This strategy tends to buy high and sell low.)
    <ul>
    <li>Every day, {@link #placeSellOrders placeSellOrders}
        looks at the stocks that are in the account (if any),
        and sell next day at the Open if the stock's last Closing price
        13-day moving average is below the 50-day moving average.
	<br>
    <li>Every Sunday, {@link #placeBuyOrders placeBuyOrders}
        looks for stocks to buy, based on the following criteria:
      <ol>
      <li>Eliminate any stocks priced less than $10 or more than $300.
      <li>Find all the stocks that made an all-time high
          any time in the previous week.
      <li>Eliminate any stocks whose 13-day moving average is not higher
          than its 50-day moving average.
      <li>Sort by highest dollar volume
          (average week's volume multiplied by average week's closing price)
      <li>Use the projected cash to buy as many stocks as possible at
          the open on Monday.   If less than minCashMaxBuyFraction of
	  account value is in cash, do not make any more buy orders for
	  the day.  The number of shares of each stock to buy is to
          be determined by minCashMaxBuyFraction * (ProjectedCashValue +
          ProjectedStockValue) divided by (last Closing Price), and rounded
          down to the nearest integer.  (Recompute projected cash and stock
          value between each stock.)  </ol>
      
    </ul>
     **/
public class SundayPeaks implements TradingStrategy {
  private static final Indicator AVG13 = new MovingAverage(13);
  private static final Indicator AVG50 = new MovingAverage(50);
  private static final Indicator MAXWEEK = new MovingMaximum(7,true);
  private static final Indicator DOLLARVOLUMEWEEK = new MovingDollarVolume(7);
  private final Map<String,Double> previousMaxPriceCache =
    new HashMap<String,Double>();
  private Date lastDateCached = null;
  private final double minCashMaxBuyFraction;
  /** Create a SundayPeaks strategy with minCashBuyFraction.
      @param minCashMaxBuyFraction minimum fraction of account value that 
      must be in cash before a purchase can be made, and maximum
      that may be invested in any one new stock.  For example,
      if the fraction is 0.2 (=1/5) and the account is worth
      $10,000 in both stock and cash, then at least $2,000 must be
      in cash before any stocks can be purchased, and at most $2,000
      will be invested in any one new stock. **/
  public SundayPeaks(double minCashMaxBuyFraction) {
    this.minCashMaxBuyFraction = minCashMaxBuyFraction;
  }

  /** Calls {@link #placeSellOrders placeSellOrders} then
      {@link #placeBuyOrders placeBuyOrders} **/
  public void placeTradeOrders(final StockMarketHistory histories,
			       final TradingAccount account,
			       final Date date, int daysUntilMarketOpen) {
    placeSellOrders(histories, account, date);
    placeBuyOrders(histories, account, date);
  }

  /** Every trading day, look at the stocks that are in the account (if any),
      and sell next day at the Open if the stock's last Closing price
      13-day moving average is below the 50-day moving average. **/
  protected void placeSellOrders(final StockMarketHistory histories,
				 final TradingAccount account,
				 final Date date) {
    if (!histories.hasTradingData(date))
      return; // not a trading day
      
    for (StockPosition position : account) {
      String symbol = position.getSymbol();
      StockHistory history = histories.get(symbol);
      int item = history.getIndexAtOrBefore(date);
      // sell stock positions where 13-day avg < 50-day avg
      if (AVG13.compute(history, item) < AVG50.compute(history, item)) {
	account.sellStock(symbol, position.getShares(),
			  OrderTiming.NEXT_DAY_OPEN, Double.NaN);
      }
    }
  }
  /** Every Sunday, look for stocks to buy, based on the following criteria:
      <ol>
      <li>Eliminate any stocks priced less than $10 or more than $300.
      <li>Find all the stocks that made an all-time high
          any time in the previous week.
      <li>Eliminate any stocks whose 13-day moving average is not higher
          than its 50-day moving average.
      <li>Sort by highest dollar volume
          (average week's volume multiplied by average week's closing price)
      <li>Use the projected cash to buy as many stocks as possible at
          the open on Monday.  The number of shares of each stock to
          buy is to be determined by (ProjectedCashValue +
          ProjectedStockValue) divided by (last Closing Price) divided
          by 5, and rounded down to the nearest integer.
	  (Recompute projected cash and stock value between each stock.)
      </ol>
  **/      
  protected void placeBuyOrders(final StockMarketHistory histories,
				final TradingAccount account,
				final Date date) { 
    if (dayOfWeek(date) != Calendar.SUNDAY)
      return;
    // must have at least minCashFraction * accountValue in cash
    if (account.getProjectedCashBalance() <
	this.minCashMaxBuyFraction * 
	(account.getProjectedCashBalance() + account.getProjectedStockValue()))
      return;

    updatePreviousMaxPriceCache(histories, date);

    List<StockHistory> candidates = new ArrayList<StockHistory>();
    for (StockHistory history : histories) {
      int item = history.getIndexAtOrBefore(date);
      if (item >= 0) { 
	// 1. eliminate stocks where not 10<=price<=300
	double price = history.get(item).getAdjustedClose();
	if (!(10.00 <= price && price <= 300.00))
	  continue;
	// 2. eliminate stocks not reaching an all time high in the last week.
	Double prevAllTimeMax = previousMaxPriceCache.get(history.getSymbol());
	if (prevAllTimeMax != null &&
	    MAXWEEK.compute(history, item) <= prevAllTimeMax)
	  continue;
	// 3. eliminate stocks where 13-day avg <= 50-day avg
	if (AVG13.compute(history, item) <= AVG50.compute(history, item))
	  continue;
	// keep candidate
	candidates.add(history);
      }
    }
    // 4. sort candidates by dollar volume for the past week
    Collections.sort(candidates, new Comparator<StockHistory>() {
      public int compare(StockHistory history1, StockHistory history2) {
	double dollarVolume1 = getDollarVolumeWeek(history1);
	double dollarVolume2 = getDollarVolumeWeek(history2);
	// negate so highest sorted first
	return -(dollarVolume1 < dollarVolume2? -1 :
		 dollarVolume1 > dollarVolume2?  1 : 0);
      }
      private double getDollarVolumeWeek(StockHistory history) {
	return DOLLARVOLUMEWEEK.compute(history,
					history.getIndexAtOrBefore(date));
      }
    });

    // 5. buy as many stocks as possible
    for (StockHistory history : candidates) {
      double projectedPrice = history.getAtOrBefore(date).getAdjustedClose();
      // value before new purchases
      double projectedAccountValue = 
	account.getProjectedCashBalance() +
	account.getProjectedStockValue();
      double maxBuyValue = this.minCashMaxBuyFraction * projectedAccountValue;
      // must have at least minCashFraction * accountValue in cash
      if (account.getProjectedCashBalance() < maxBuyValue)
	break;
      int nShares = (int) (maxBuyValue / projectedPrice);
      double projectedCost =
	nShares * projectedPrice + account.getTradeFees(nShares);
      if (nShares > 0) {
	// ok to go into a little debt due to price increase or trade fees.
	account.buyStock(history.getSymbol(), nShares,
			 OrderTiming.NEXT_DAY_OPEN, Double.NaN);
      } 
    }
  }
  /** Return the day of the week as an int.
      To test if it is Sunday, use
      <pre>
        Calendar.SUNDAY == dayOfWeek(date)
      </pre> **/
  protected int dayOfWeek(Date date) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  /** 
   **/
  private void updatePreviousMaxPriceCache(StockMarketHistory histories,
					   Date untilDate) {
    // set until date to week ago
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(untilDate);
    calendar.add(Calendar.DATE, -7);
    untilDate = calendar.getTime();

    if (this.lastDateCached == null ||
	this.lastDateCached.getTime() > untilDate.getTime()) {
      // restarting, so reset cache
      this.previousMaxPriceCache.clear();
      this.lastDateCached = null;
    }
    for (StockHistory history : histories) {
      int startIndex = (this.lastDateCached == null? 0 :
			history.getIndexAtOrAfter(this.lastDateCached));
      int endIndex = history.getIndexAtOrBefore(untilDate);
      if (startIndex >= 0 && endIndex >= 0) { 
	String symbol = history.getSymbol();
	Double cachedMaxPrice = previousMaxPriceCache.get(symbol);
	double maxPrice = (cachedMaxPrice == null? 0.0 : cachedMaxPrice);

	for (int i = startIndex; i < endIndex; i++)
	  maxPrice = Math.max(maxPrice, history.get(i).getAdjustedHigh());

	previousMaxPriceCache.put(symbol, maxPrice);
      } 
    }
    this.lastDateCached = untilDate;
  }
				   
  /** returns shortClassName+"("+minCashMaxBuyFraction+")" **/
  public String toString() {
    String className = this.getClass().getName();
    String shortName = className.substring(className.lastIndexOf('.')+1);
    return shortName+"("+this.minCashMaxBuyFraction+")";
  }
}
