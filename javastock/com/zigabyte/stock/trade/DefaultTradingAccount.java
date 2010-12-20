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

import com.zigabyte.stock.data.*;
import com.zigabyte.stock.tradeobserver.TradeObserver;
import java.util.*;

/** Default implementation of {@link TradingAccount}.
    To monitor an account, {@link TradeObserver}s may be added,
    and will be informed whenever a buy or sell trade is executed.
 **/
public class DefaultTradingAccount implements TradingAccount {
  //// FIELDS
  private Date currentDate = null;
  private double cashBalance = Double.NaN;
  private final List<StockPosition> positions = new ArrayList<StockPosition>();
  /** Index from stock symbol to stock position **/
  private final Map<String,StockPosition> positionIndex =
    new HashMap<String,StockPosition>();
  private final List<TradeOrder> pendingBuys = new ArrayList<TradeOrder>();
  private final List<TradeOrder> pendingSells = new ArrayList<TradeOrder>();
  private final Set<TradeObserver> observers = new HashSet<TradeObserver>();
  private final StockMarketHistory histories;
  private final double perTradeFee, perShareTradeCommission;

  //// CONSTRUCTOR
  /** @param perTradeFee flat fee charged for each trade.
      @param perShareTradeCommission per share commission charged for trade.
   **/
  public DefaultTradingAccount(StockMarketHistory histories,
			       double perTradeFee,
			       double perShareTradeCommission) {
    this.histories = histories;
    this.perTradeFee = perTradeFee;
    this.perShareTradeCommission = perShareTradeCommission;
  }

  //// GETTERS
  public double getTradeFees(int shares) {
    return this.perTradeFee + shares * this.perShareTradeCommission;
  } 

  public double getCurrentAccountValue() {
    return getCurrentCashBalance() + getCurrentStockValue();
  }

  public double getCurrentCashBalance() {
    return cashBalance;
  }

  // total value of stocks in the account, based on last Closing price.
  public double getCurrentStockValue() {
    double total = 0.0;
    for (StockPosition position : this.positions) {
      total += getCurrentStockValue(position);
    }
    return total;
  }
  public double getCurrentStockValue(StockPosition position) {
    double projectedPrice = PriceUtils.getProjectedPrice(this.histories,
							 position.getSymbol(),
							 this.currentDate);
    if (projectedPrice > 0) { 
      return (position.getShares() * projectedPrice 
	      - getTradeFees(position.getShares())); // cost to sell stock
    } else {
      throw new IllegalStateException("No price for "+position.getSymbol()+
				      " as of "+this.currentDate);
    }
  }

  public Date getCurrentDate() {
    return this.currentDate;
  }

  public double getProjectedAccountValue() {
    return getProjectedCashBalance() + getProjectedStockValue();
  }

  public double getProjectedCashBalance() {
    double projectedChange = 0.00;
    for (TradeOrder order : pendingSells) {
      projectedChange += order.shares * order.projectedPrice;
      projectedChange -= getTradeFees(order.shares);
    }
    for (TradeOrder order : pendingBuys) {
      projectedChange -= order.shares * order.projectedPrice;
      projectedChange -= getTradeFees(order.shares);
    }
    return getCurrentCashBalance() + projectedChange;
  }

  public double getProjectedStockValue() {
    double projectedChange = 0.00;
    for (TradeOrder order : pendingSells) {
      projectedChange -= order.shares * order.projectedPrice;
    }
    for (TradeOrder order : pendingBuys) {
      projectedChange += order.shares * order.projectedPrice;
    }
    return getCurrentStockValue() + projectedChange;
  }

  public StockMarketHistory getStockMarketHistory() {
    return this.histories;
  }

  public int getStockPositionCount() {
    return positions.size();
  }
  public StockPosition getStockPosition(int i) {
    return positions.get(i);
  }
  public StockPosition getStockPosition(String symbol) {
    return positionIndex.get(symbol);
  }
  public Iterator<StockPosition> iterator() {
    return this.positions.iterator();
  }

  // SIMULATION
  public void initialize(Date initialDate, double cash) {
    if (Double.isNaN(this.cashBalance) && this.currentDate == null) { 
      this.cashBalance = cash;
      this.currentDate = initialDate;
      fireInitialized(initialDate);
    } else { 
      throw new IllegalStateException("Already initialized");
    }
    
  }

  public void buyStock(String symbol, int shares, 
		       OrderTiming orderTiming, double limit) {
    double projectedPrice = PriceUtils.getProjectedPrice(this.histories, symbol,
							 this.currentDate);
    pendingBuys.add(new TradeOrder(TradeType.BUY, symbol, shares,
				   projectedPrice, orderTiming, limit));
  }

  public void sellStock(String symbol, int shares, 
			OrderTiming orderTiming, double limit) {
    double projectedPrice = PriceUtils.getProjectedPrice(this.histories, symbol,
							 this.currentDate);
    pendingSells.add(new TradeOrder(TradeType.SELL, symbol, shares,
				    projectedPrice, orderTiming, limit));
  }

  // execute orders with tradeFees and update balances.
  public void executeOrders(Date tradingDate) {
    this.currentDate = tradingDate;
    executeSellOrders(tradingDate);
    removeEmptyPositions();

    if(this.pendingBuys.size() != 0 && this.pendingSells.size() != 0) {
	// ATR changed to call this twice.
	fireOrdersCompleted(tradingDate);
    }

    executeBuyOrders(tradingDate);
    fireOrdersCompleted(tradingDate);
    removeEmptyPositions(); // in case of covered shorts
  }

  protected void executeSellOrders(Date tradingDate) { 
    for (TradeOrder order : this.pendingSells) {
      double tradingPrice =
	PriceUtils.getPrice(this.histories, order.symbol, tradingDate,
			    order.getPriceTiming(), order.limit);
      StockPosition position = positionIndex.get(order.symbol);
      if (tradingPrice > 0.00) {
	order.executed(tradingDate, tradingPrice);
	double totalCashIn =
	    order.getExecutedValue() - getTradeFees(order.shares);

	if (position == null) { // ATR- create Short Sale position.
	  position = createStockPosition(order.symbol);
	  positions.add(position);
	  positionIndex.put(order.symbol, position);
	}
	position.removeShares(tradingDate, order.shares, tradingPrice);

	this.cashBalance += totalCashIn;
	fireOrderSold(order, tradingDate, position);
      } else { // limit price not reached or no price available, so trade cancelled
	fireOrderCancelled(order, tradingDate, position);
      }
    }
    this.pendingSells.clear();
  }

  protected void executeBuyOrders(Date tradingDate) {
    for (TradeOrder order : this.pendingBuys) {
      double tradingPrice =
	PriceUtils.getPrice(this.histories, order.symbol, tradingDate,
			    order.getPriceTiming(), order.limit);
      StockPosition position = positionIndex.get(order.symbol);
      if (tradingPrice > 0.00) {
	order.executed(tradingDate, tradingPrice);
	double totalCost =
	  order.getExecutedValue() + getTradeFees(order.shares);
	double costBasisPerShare = totalCost/order.shares;
	if (position == null) {
	  position = createStockPosition(order.symbol);
	  positions.add(position);
	  positionIndex.put(order.symbol, position);
	}
	position.addShares(tradingDate, order.shares, costBasisPerShare);
	this.cashBalance -= totalCost;
	fireOrderBought(order, tradingDate, position);
      } else { // limit price not reached or no price available, so trade cancelled.
	fireOrderCancelled(order, tradingDate, position);
      }
    }
    this.pendingBuys.clear();
  }

  /** May be overridden by a derived class. **/
  protected DefaultStockPosition createStockPosition(String symbol) {
    return new DefaultStockPosition(symbol);
  }

  /** Remove any stock positions which have been completely sold off. **/
  protected void removeEmptyPositions() {
    Collection<StockPosition> emptyPositions = new ArrayList<StockPosition>();
    for (StockPosition position : this.positions)
      if (position.getShares() == 0) 
	emptyPositions.add(position);
    for (StockPosition emptyPosition : emptyPositions) { 
      this.positions.remove(emptyPosition);
      this.positionIndex.remove(emptyPosition.getSymbol());
    }
  }

  public double getMarginBuyingPower() {
      double shortBalance = 0;
      for (StockPosition position : this.positions) {
          if(position.getShares() < 0) {
              shortBalance += getCurrentStockValue(position);
          }
      }
      return 2 * (getCurrentCashBalance() - 2 * Math.abs(shortBalance));
  }

  // TRADE_OBSERVERS
  public void addTradeObserver(TradeObserver observer) {
    this.observers.add(observer);
  }
  public void removeTradeObserver(TradeObserver observer) {
    this.observers.remove(observer);
  }
  /** Inform trade observers that account is initialized. **/
  protected void fireInitialized(Date initialDate) {
    for (TradeObserver observer : this.observers) {
      observer.initialized(initialDate, this);
    }
  }
  /** Inform trade observers that a buy order has been executed. **/
  protected void fireOrderBought(TradeOrder order, Date tradingDate,
				 StockPosition position) {
    for (TradeObserver observer : this.observers) {
      observer.orderBought(order, tradingDate, position, this);
    }
  }
  /** Inform trade observers that a sell order has been executed. **/
  protected void fireOrderSold(TradeOrder order, Date tradingDate,
			       StockPosition position) {
    for (TradeObserver observer : this.observers) {
      observer.orderSold(order, tradingDate, position, this);
    }
  }
  /** Inform trade observers that a sell order has been executed. **/
  protected void fireOrderCancelled(TradeOrder order, Date tradingDate,
				    StockPosition position) {
    for (TradeObserver observer : this.observers) {
      observer.orderCancelled(order, tradingDate, position, this);
    }
  }
  /** Inform trade observers all this orders on this account for
      tradingDate have been completed. **/
  protected void fireOrdersCompleted(Date tradingDate) {
    for (TradeObserver observer : this.observers) {
      observer.ordersCompleted(tradingDate, this);
    }
  }
		    
    /** Added by ATR: are any orders pending? Use in the strategy to avoid
        order duplication around weekends/holidays.
     **/
    public boolean hasPendingOrders() {
        return this.pendingBuys.size() != 0 || this.pendingSells.size() != 0;
    }
    
}
