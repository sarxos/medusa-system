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
package com.zigabyte.stock.trade;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

/** Represents a placed order to buy or sell stocks on the next trading day. **/
public class TradeOrder {
  //// FIELDS
  public final TradeType type;
  public final String symbol;
  public final int shares;
  public final OrderTiming orderTiming;
  public final double limit;
  public final double projectedPrice;
  /** Set to non-zero if/when trade is executed **/
  private double executedPrice = 0;
  private Date executedDate = null;
  //// CONSTRUCTOR
  /** Construct a trading order.  The executed price not known yet
      so is initialize to 0.
      @param type Buy or Sell.
      @param symbol Stock symbol.
      @param shares number of shares.
      @param projectedPrice usually the last traded price at time of order.
      @param orderTiming when to execute the order
      NEXT_DAY_OPEN, NEXT_DAY_CLOSE, or NEXT_DAY_LIMIT
      @param limit limit if this is a limit order.
   **/
  public TradeOrder(TradeType type, String symbol, int shares,
		    double projectedPrice,
		    OrderTiming orderTiming, double limit) {
    this(type, symbol, shares, projectedPrice, orderTiming, limit, null, 0);
  }
  public TradeOrder(TradeType type, String symbol, int shares,
		    double projectedPrice,
		    OrderTiming orderTiming, double limit,
		    Date executedDate, double executedPrice) {
    if (type != null && symbol != null && orderTiming != null) {
      this.type = type;
      this.symbol = symbol;
      this.shares = shares;
      this.projectedPrice = projectedPrice;
      this.orderTiming = orderTiming;
      this.limit = limit;
      this.executedDate = executedDate;
      this.executedPrice = executedPrice;
    } else throw new NullPointerException("type="+type+", symbol="+symbol+
					  ", orderTiming="+orderTiming);
  }

  //// METHODS
  /** Projected value: nShares * projectedPrice. **/
  public double getProjectedValue() {
    return shares * projectedPrice;
  }
  /** Executed value: nShares * executedPrice. **/
  public double getExecutedValue() {
    return shares * executedPrice;
  }
  /** Price at which order was executed, or 0 if it has not been executed. **/
  public double getExecutedPrice() {
    return executedPrice;
  }
  /** Price at which order was executed, or 0 if it has not been executed. **/
  public Date getExecutedDate() {
    return executedDate;
  }
  /** Sets the price at which order was executed.
      May only be called once. **/
  public void executed(Date executedDate, double executedPrice) {
    if (this.executedDate == null) { 
      this.executedDate = executedDate;
      this.executedPrice = executedPrice;
    } else throw new IllegalStateException("Already executed");
  }
  /** Provides a {@link PriceTiming} based on {@link TradeType} and
      {@link OrderTiming}. **/
  public PriceTiming getPriceTiming() {
    switch(this.orderTiming) {
    case NEXT_DAY_OPEN: return PriceTiming.OPEN;
    case NEXT_DAY_CLOSE: return PriceTiming.CLOSE;
    case NEXT_DAY_LIMIT:
      switch(this.type) {
      case BUY: return PriceTiming.LIMIT_OR_BELOW;
      case SELL: return PriceTiming.LIMIT_OR_ABOVE;
      default:
	throw new IllegalStateException(String.valueOf(this.type));
      }
    case NEXT_DAY_STOP:
      switch(this.type) {
      case BUY: return PriceTiming.LIMIT_OR_ABOVE;
      case SELL: return PriceTiming.LIMIT_OR_BELOW;
      default:
	throw new IllegalStateException(String.valueOf(this.type));
      }
    default:
      throw new IllegalStateException(String.valueOf(this.orderTiming));
    }
  }
  public String toString() {
    return ((this.executedDate==null
	     ? orderTimingString()
	     : DATE_FORMAT.format(executedDate)+" ")+
	    this.type+" "+this.symbol+" "+this.shares+"sh @"+
	    DOLLAR_FORMAT.format(this.executedDate==null
				     ? this.projectedPrice
				     : this.executedPrice));
	    
       
  }
  private String orderTimingString() {
    switch(this.orderTiming) {
    case NEXT_DAY_OPEN:  return "(Next Open)";
    case NEXT_DAY_CLOSE: return "(NextClose)";
    case NEXT_DAY_LIMIT: return "(Lim"+DOLLAR_FORMAT.format(this.limit)+")";
    default: throw new IllegalStateException(String.valueOf(this.orderTiming));
    }
  }

  private static final SimpleDateFormat DATE_FORMAT =
    new SimpleDateFormat("ddMMMyyyy");
  private static final DecimalFormat DOLLAR_FORMAT =
    new DecimalFormat("$#,#0.00");
}
