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
import java.util.Date;
import java.text.*;
import java.io.*;

/** Observes a trading account and writes a trace of the buy and sell
    trades executed and a summary of the account value.  The summary
    may be written either at the end of every day, or only at the end
    of each day on which a trade was made.

    @see PeriodTraceObserver
**/
public class TradeTraceObserver implements TradeObserver {
  //// CONSTANT FORMATTERS
  static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMMyyyy");
  static final NumberFormat DOLLAR_FORMAT = new DecimalFormat("$#,##0.00");
  static final NumberFormat PERCENT_FORMAT = new DecimalFormat("#0.0%");

  //// FIELDS
  Date lastTraceDate = null;
  boolean daysTradedOnly = false;
  PrintWriter writer;

  //// CONSTRUCTORS
  /** Creates a trace observer that outputs to {@link System#out}.
      @param daysTradedOnly If true, output end-of-day summary
      only on days in which a trade is made, otherwise output every day. **/
  public TradeTraceObserver(boolean daysTradedOnly) {
    this(daysTradedOnly, 
	 new PrintWriter(new OutputStreamWriter(System.out), true));
  }
  /** Creates a trace observer that outputs to writer
      @param daysTradedOnly If true, output end-of-day summary
      only on days in which a trade is made, otherwise output every day.
      @param writer where to write trace. **/
  public TradeTraceObserver(boolean daysTradedOnly, PrintWriter writer) {
    this.daysTradedOnly = daysTradedOnly;
    this.writer = writer;
  }
  
  /** Write initial cash balance. **/
  public void initialized(Date date, TradingAccount account) {
    writer.println
      (DATE_FORMAT.format(date)+": "+
       "cash="+DOLLAR_FORMAT.format(account.getCurrentCashBalance()));
    this.lastTraceDate = date;
  }
  /** Write [DATE] Buy [SYMBOL] [NUMBER]sh @[PRICE] = [VALUE]
      --> [NUMBER]sh held **/
  public void orderBought(TradeOrder order, Date date,
			  StockPosition position, TradingAccount account) {
    double profitOrLoss =
      (order.shares*(order.getExecutedPrice() - position.getCostBasis())
       - account.getTradeFees(order.shares));

    double percent = profitOrLoss/(position.getCostBasis()*order.shares);

    this.writer.println
      (DATE_FORMAT.format(date)+" "+
       (position.getShares() != 0 ? "Buy " : "Cover ") 
       + order.symbol+" "+order.shares+"sh "+"@"+
       DOLLAR_FORMAT.format(order.getExecutedPrice())+" = "+
       DOLLAR_FORMAT.format(order.getExecutedValue())+" --> "+

       position.getShares() + "sh " +
       (position.getShares() <= 0 ? "left, " +
        DOLLAR_FORMAT.format(profitOrLoss)+
        (profitOrLoss > 0 ? " (" 
         + PERCENT_FORMAT.format(percent) +  " profit)" :
         profitOrLoss < 0 ? " (" 
         + PERCENT_FORMAT.format(percent) +  "loss)" : 
         " (even)") : "long")
       );

    this.lastTraceDate = date;
  }
  /** Write [DATE] SELL [SYMBOL] [NUMBER]sh @[PRICE] = [VALUE].
      --> [NUMBER]sh left, [PROFIT_OR_LOSS] ([profit/loss/even]) **/
  public void orderSold(TradeOrder order, Date date,
			StockPosition position, TradingAccount account) {
    double profitOrLoss =
      (order.shares*(order.getExecutedPrice() - position.getCostBasis())
       - account.getTradeFees(order.shares));

    double percent = profitOrLoss/(position.getCostBasis()*order.shares);

    this.writer.println
      (DATE_FORMAT.format(date)+" "+
       (position.getShares() == 0 ? "Sell " : "Short ")
       + order.symbol+" "+order.shares+"sh "+"@"+
       DOLLAR_FORMAT.format(order.getExecutedPrice())+" = "+
       DOLLAR_FORMAT.format(order.getExecutedValue())+" --> "+
       position.getShares() + "sh " +
       (position.getShares() >= 0 ? "left, " +
        DOLLAR_FORMAT.format(profitOrLoss)+
        (profitOrLoss > 0 ? " (" 
         + PERCENT_FORMAT.format(percent) +  " profit)" :
         profitOrLoss < 0 ? " (" 
         + PERCENT_FORMAT.format(percent) +  "loss)" : 
         " (even)") : "short"));

    this.lastTraceDate = date;
  }
  /** Write [DATE] Buy/Sell [SYMBOL] [NUMBER]sh @[PRICE] CANCELLED 
      (no price on date) **/
  public void orderCancelled(TradeOrder order, Date date,
			     StockPosition position, TradingAccount account) {
    String priceString;
    switch(order.getPriceTiming()) {
    case OPEN:
    case CLOSE:
    default:
      priceString = DOLLAR_FORMAT.format(order.projectedPrice);
      break;
    case LIMIT_OR_BELOW:
      priceString = "<="+DOLLAR_FORMAT.format(order.limit);
      break;
    case LIMIT_OR_ABOVE:
      priceString = ">="+DOLLAR_FORMAT.format(order.limit);
      break;
    }    
    // ATR removed "cancelled order" log. Doesn't make sense when
    // using lots of stop and limit orders that don't fill.
  }
  /** If writing every day, or shares traded on date,<br>
      Write [DATE]: cash=[CASHVALUE], stocks=[STOCKVALUE], total=[TOTALVALUE]**/
  public void ordersCompleted(Date date, TradingAccount account) {
    // report new balances only if they changed (buy or sell occurred on date)
    if (!this.daysTradedOnly || this.lastTraceDate.equals(date)) { 
      double cashBalance = account.getCurrentCashBalance();
      double stockValue = account.getCurrentStockValue();
      double total = cashBalance + stockValue;
      this.writer.println(DATE_FORMAT.format(date)+": "+
			  "cash="+DOLLAR_FORMAT.format(cashBalance)+", "+
			  "stocks="+DOLLAR_FORMAT.format(stockValue)+", "+
			  "total="+DOLLAR_FORMAT.format(total));
    }
  }
}
