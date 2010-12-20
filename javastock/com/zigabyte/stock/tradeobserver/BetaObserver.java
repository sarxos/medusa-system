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

/** Computes 'beta', the slope of linear least squares fit
    of (comparedIndexValue, accountValue) points over observed dates.

    On each date, collects data point (comparedIndexValue, accountValue).
    Consider a scatter plot of those data points where x is compared value
    and y is account value.   The {@link #computeBeta} fits a line
    to those points using a linear least squares fit, and returns the
    slope of this line.
**/
public class BetaObserver extends TradeObserverAdapter {
  //// FIELDS
  final String compareSymbol;
  Date lastDate;
  List<DataPoint> points = new ArrayList<DataPoint>();

  // CONSTRUCTORS
  /** Compare against "$SP": the S&P500 Index. **/
  public BetaObserver() {
    this("$SP");
  }
  public BetaObserver(String symbolOfComparedIndex) {
    this.compareSymbol = symbolOfComparedIndex;
  }

  //// INTERFACE TradeObserverAdapter
  /** Record initial data point with values for date. **/
  public void initialized(Date date, TradingAccount account) {
    recordDataPoint(date, account);
  }
  /** Record data point with values for date (skip if date repeated). **/
  public void ordersCompleted(Date date, TradingAccount account) {
    recordDataPoint(date, account);
  }
  /** If a value for compared index is available on date, 
      records compared index value and current value of account.
      (skip if date repeated). **/
  protected void recordDataPoint(Date date, TradingAccount account) {
    if (this.lastDate == null || !this.lastDate.equals(date)) { 
      // don't repeat on same date
      StockMarketHistory histories = account.getStockMarketHistory();
      StockHistory compareHistory = histories.get(compareSymbol);
      if (compareHistory != null) { 
	StockDataPoint dataPoint = compareHistory.get(date);
	if (dataPoint != null) {
	  double compareValue = dataPoint.getAdjustedClose();
	  double accountValue = (account.getCurrentCashBalance()+
				 account.getCurrentStockValue());
	  this.points.add(new DataPoint(compareValue, accountValue));
	  this.lastDate = date;
	}
      } else {
	throw new IllegalArgumentException("No history for "+
					   this.compareSymbol);
      }
    }
  }

  //// LEAST SQUARES FIT
  /** Compute intercept of linear least squares fit of 
      (index value, account value) points over observed dates. **/
  public double computeAlpha() {
    return computeAlphaBeta()[0];
  }
  /** Compute slope of linear least squares fit of 
      (index value, account value) points over observed dates. **/
  public double computeBeta() {
    return computeAlphaBeta()[1];
  }
  /** Compute intercept and slope of linear least squares fit of 
      (index value, account value) points over observed dates.
      @return array containing alpha (intercept), beta (slope),
      alpha error estimate, and beta error estimate. **/
  public double[] computeAlphaBeta() {
    double lastCompareValue = 0, lastAccountValue = 0;
    double sumX = 0, sumY = 0, sumXX = 0, sumXY = 0;
    for (DataPoint point : this.points) {
      if (lastCompareValue == 0) { 
	lastCompareValue = point.compareValue;
	lastAccountValue = point.accountValue;
      } else { 
	double x = point.compareValue - lastCompareValue;
	double y = point.accountValue - lastAccountValue;
	sumX += x;
	sumY += y;
	sumXX += x*x;
	sumXY += x*y;
	lastCompareValue = point.compareValue;
	lastAccountValue = point.accountValue;
      }
    }
    double count = this.points.size() - 1; // differences
    double delta = (count*sumXX - sumX*sumX);

    // intercept
    double alpha = (sumXX*sumY - sumX*sumXY)/delta;
    // slope
    double beta = (count*sumXY - sumX*sumY)/delta;

    // intercept error estimate
    double alphaError = sumXX/delta;
    // slope error estimate
    double betaError = count/delta;
      
    return new double[]{alpha, beta, alphaError, betaError};
  }
  /** Number of data points collected. **/
  public int getDataCount() {
    return points.size();
  }

  /** DataPoint holds two values, (compareValue, accountValue). **/
  private static class DataPoint {
    double compareValue, accountValue;
    DataPoint(double compareValue, double accountValue) {
      this.compareValue = compareValue;
      this.accountValue = accountValue;
    }
  }
    
}
