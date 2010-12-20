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

/** Keeps track of most profitable and worst losing calendar periods
    (months, years, etc.), and the number of winning and losing
    calendar periods.<p>

    This implementation keeps track of the counts, best profit, and
    worst loss for completed periods, and the performance for the
    current period so far.  To compute best/worst values, it compares
    the completed periods' value with the current period's performance so
    far.  At the first trading day of a new period, updates the counts
    and best/worst for completed periods.

    @see TradeWinLossObserver
**/
public class PeriodWinLossObserver extends AbstractPeriodObserver {
  // FIELDS
  private int winningPeriodCount = 0, losingPeriodCount = 0;
  private Date bestPeriodStartDate = null, worstPeriodStartDate = null;
  // Set to NaN to ensure initialized
  private double currentPeriodStartValue = Double.NaN; 
  private double currentPeriodChange = Double.NaN;
  private double bestPeriodProfit = Double.NaN;
  private double worstPeriodLoss = Double.NaN;

  /** Creates an unaligned periodic win/loss observer.
      @param unitCount number of calendar units in period
      @param calendarUnit unit counted, such as {@link Calendar#DATE}
      or {@link Calendar#MONTH} **/
  public PeriodWinLossObserver(int unitCount, int calendarUnit) {
    super(unitCount, calendarUnit, false);
  }
  /** Creates a periodic win/loss observer.
      @param unitCount number of calendar units in period
      @param calendarUnit unit counted, such as {@link Calendar#YEAR}
      or {@link Calendar#MONTH} or {@link Calendar#WEEK_OF_YEAR} or
      {@link Calendar#DATE}.
      @param align whether to align to calendar unit, so for example
      monthly periods start on 1st day of month.
  **/
  public PeriodWinLossObserver(int unitCount, int calendarUnit,
			       boolean align) {
    super(unitCount, calendarUnit, align);
  }

  // INTERFACE TradeObserver
  /** Set initial values **/
  public void initialized(Date initialDate, TradingAccount account) {
    super.initialized(initialDate, account);
    this.winningPeriodCount = this.losingPeriodCount = 0;
    this.bestPeriodStartDate = worstPeriodStartDate = null;
    this.currentPeriodStartValue = (account.getCurrentCashBalance() +
				   account.getCurrentStockValue());
    this.currentPeriodChange = 0.0;
    this.bestPeriodProfit = 0.0;
    this.worstPeriodLoss = 0.0;
  }
  public void ordersCompleted(Date date, TradingAccount account) {
    super.ordersCompleted(date, account);
    // Update change for this month.
    // Must be after periodStarted called by super.ordersCompleted.
    double currentValue = (account.getCurrentCashBalance() +
			   account.getCurrentStockValue());
    this.currentPeriodChange = currentValue - this.currentPeriodStartValue;
  }
  /** Updates values.  Updates prior months' values if a new month. **/
  protected void periodStarted(Date lastPeriodStartDate, Date lastPeriodEndDate,
			       Date tradingDate, TradingAccount account) {
    // If new month, update counts and best/worst month using last numbers.
    if (this.currentPeriodChange > 0.0) {
      this.winningPeriodCount++;
      if (this.currentPeriodChange > this.bestPeriodProfit) {
	this.bestPeriodProfit = this.currentPeriodChange;
	this.bestPeriodStartDate = lastPeriodStartDate;
      }
    }
    if (this.currentPeriodChange < 0.0) { 
      this.losingPeriodCount++;
      if (this.currentPeriodChange < this.worstPeriodLoss) {
	this.worstPeriodLoss = this.currentPeriodChange;
	this.worstPeriodStartDate = lastPeriodStartDate;
      }
    }
    // Then reset for start of new month.
    // (May have already traded this month, so update from last month change.)
    this.currentPeriodStartValue += currentPeriodChange;
  }
  // ACCESSORS
  /** Return best month's profit if simulation ended now.
      If current month so far is better than best prior month,
      returns current month's profit, else profit of best month.<p>

      (If simulation continues trading this month, result may change.
      If this month wasn't best month but it improves, it may become
      the best month.  If it was best month but degrades, it may no
      longer remain the best month.)<p>

      Returns 0 if there were no profitable months. **/
  public double getBestPeriodProfit() {
    return Math.max(this.bestPeriodProfit, this.currentPeriodChange);
  }
  /** Return worst month's loss if simulation ended now.
      If current month so far is worse than worst prior month,
      returns current month's loss, else loss of worst month.<p>

      (If simulation continues trading this month, result may change.
      If this month wasn't worst month but it degrades, it may become
      the worst month.  If it was worst month but improves, it may no
      longer remain the worst month.)<p>

      Returns 0 if there were no losing months. **/
  public double getWorstPeriodLoss() {
    return Math.min(this.worstPeriodLoss, this.currentPeriodChange);
  } 
  /** Return recorded start of best month if simulation ended now.
      If current month so far is better than best full month,
      returns start of current month, else start of best month. <p>

      (If simulation continues trading this month, result may change.
      If this month wasn't best month but it improves, it may become
      the best month.  If it was best month but degrades, it may no
      longer remain the best month.)<p>

      Date returned is initial date or first trading date of month. **/
  public Date getBestProfitPeriodStartDate() {
    if (this.currentPeriodChange > this.bestPeriodProfit)
      return getCurrentPeriodStartDate();
    else 
      return this.bestPeriodStartDate;
  }
  /** Return recorded start of worst month if simulation ended now.
      If current month so far is worse than worst full month,
      returns start of current month, else start of worst month.<p>

      (If simulation continues, result may change.  If this month wasn't worst
      month but it degrades, it may become the worst month.  If it was worst
      month but improves, it may no longer remain the worst month.)<p>
      
      Date returned is initial date or first trading date of month. **/
  public Date getWorstLossPeriodStartDate() {
    if (this.currentPeriodChange < this.worstPeriodLoss)
      return getCurrentPeriodStartDate();
    else 
      return this.worstPeriodStartDate;
  }
  /** Return number of winning months if simulation ended now.
      A calendar month is a winning month if it produces a net profit.
      The count includes possibly partial month at beginning of simulation, and
      this month so far.  <p>

      (If simulation continues trading this month, result may change.
      If this month wasn't profitable but becomes profitable, it will
      increase by 1.  If it was profitable but becomes unprofitable,
      it will decrease by 1.)<p>

      Zero net change is not a profit, so an account that never trades
      will have 0 winning months.  (Assuming cash in the account does not
      accrue interest.)
  **/
  public int getWinningPeriodCount() {
    return this.winningPeriodCount + (this.currentPeriodChange > 0.0? 1 : 0);
  }
  /** Return number of losing months if simulation ended now.
      A calendar month is a losing month if it produces a net loss.
      The count includes possibly partial month at beginning of simulation, and
      this month so far.  <p>

      (If simulation continues trading this month, result may change.
      If this month wasn't a losing month but becomes a losing month, it will
      increase by 1.  If it was a losing month but stops being a losig month,
      it will decrease by 1.)<p>

      Zero net change is not a loss, so 
      an account that never trades will have 0 losing months.
      (Assuming the account charges no fees besides trade fees.)
  **/
  public int getLosingPeriodCount() {
    return this.losingPeriodCount + (this.currentPeriodChange < 0.0? 1 : 0);
  }

}
