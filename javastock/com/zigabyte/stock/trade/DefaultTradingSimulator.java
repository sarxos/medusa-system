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

import com.zigabyte.stock.data.StockMarketHistory;
import com.zigabyte.stock.data.StockHistory;
import com.zigabyte.stock.strategy.TradingStrategy;

import java.util.*;
import java.text.SimpleDateFormat;

/** Simulate trading over a {@link StockMarketHistory}.
 A run starts with a {@link TradingAccount},
 and uses a {@link TradingStrategy} from start date to end date. **/
public class DefaultTradingSimulator {
	protected StockMarketHistory histories;

	public DefaultTradingSimulator(StockMarketHistory histories) {
		this.histories = histories;
	}

	/** Runs strategy for every day from startDate before endDate,
	 then sells off all remaining account positions on endDate. **/
	public void runStrategy(TradingStrategy strategy, TradingAccount account,
			Date startDate, Date endDate) {
		if (startDate.getTime() > endDate.getTime())
			throw new IllegalArgumentException("startDate after endDate");

		// Loop from start date to end date by day.
		// Include non-trading days, so strategies can run on weekends or holidays.
		Date nextTradingDate = nextTradingDate(startDate);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		final long msecPerDay = 24 * 60 * 60 * 1000;
		for (; calendar.getTimeInMillis() <= endDate.getTime(); calendar.add(
				Calendar.DAY_OF_MONTH, 1)) {
			Date date = calendar.getTime();
			// Wait until a trading day to execute orders.
			if (nextTradingDate != null
					&& nextTradingDate.getTime() == date.getTime()) {
				account.executeOrders(date);
			}
			nextTradingDate = nextTradingDate(date);
			// place orders only if they'll be executed before end date
			if (nextTradingDate != null
					&& nextTradingDate.getTime() <= endDate.getTime()) {
				int daysUntilMarketOpen = (int) ((nextTradingDate.getTime() - date
						.getTime()) / msecPerDay) - 1;
				strategy.placeTradeOrders(this.histories, account, date,
						daysUntilMarketOpen);
			}
		}
	}

	/** Return the next trading date after date in this
	 simulator's stock market history. **/
	public Date nextTradingDate(Date date) {
		return this.histories.nextTradingDate(date, true);
	}
}
