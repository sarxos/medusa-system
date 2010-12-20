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

import java.util.*;

/*******************************************************************************
 * PriceUtils get prices from {@link StockMarketHistory} based on date and
 * {@link PriceTiming} (OPEN, CLOSE, LIMIT_OR_ABOVE, LIMIT_OR_BELOW).
 ******************************************************************************/
public class PriceUtils {

	final static private boolean REPORT_MISSING_DATA = false;

	/***************************************************************************
	 * Get most recent closing price for stock symbol on or before date, or 0 if
	 * no prior data for symbol.
	 **************************************************************************/
	public static double getProjectedPrice(StockMarketHistory histories,
			String symbol, Date date) {
		StockHistory history = histories.get(symbol);
		if (history != null) {
			StockDataPoint datum = history.getAtOrBefore(date);
			if (datum != null) {
				return datum.getAdjustedClose();
			} else {
				report(histories, date, "No data for " + symbol + " on " + date);
				return 0;
			}
		} else {
			report(histories, date, "No data for " + symbol);
			return 0;
		}
	}

	/** Get closing price for stock symbol on date, or 0 if no data for date. * */
	public static double getPrice(StockMarketHistory histories, String symbol,
			Date date) {
		return getPrice(histories, symbol, date, PriceTiming.CLOSE);
	}

	/***************************************************************************
	 * Get {@link PriceTiming#OPEN} or {@link PriceTiming#CLOSE} close price for
	 * stock symbol on date, or 0 if no data for date.
	 **************************************************************************/
	public static double getPrice(StockMarketHistory histories, String symbol,
			Date date, PriceTiming timing) {
		switch (timing) {
		case OPEN:
		case CLOSE:
			return getPrice(histories, symbol, date, timing, Double.NaN);
		case LIMIT_OR_ABOVE:
		case LIMIT_OR_BELOW:
		default: // null
			throw new IllegalArgumentException(String.valueOf(timing));
		}
	}

	/***************************************************************************
	 * Get price for stock named symbol on date.
	 * <ul>
	 * <li>If no data for stock symbol on date in histories, returns 0.
	 * <li>If timing is OPEN, returns adjusted opening price.
	 * <li>If timing is CLOSE, returns adjusted closing price.
	 * <li>If timing is LIMIT_OR_ABOVE: if adjusted open >= limit, returns
	 * adjusted open, else if adjusted high >= limit, returns limit, else
	 * returns 0 (never reached limit).
	 * <li>If timing is LIMIT_OR_BELOW: if adjusted open <= limit, returns
	 * adjusted open, else if adjusted low <= limit, returns limit, else returns
	 * 0 (never reached limit).
	 * </ul>
	 **************************************************************************/
	protected static double getPrice(StockMarketHistory histories,
			String symbol, Date date, PriceTiming timing, double limit) {
		StockHistory history = histories.get(symbol);
		if (history != null) {
			StockDataPoint datum = history.get(date);
			if (datum != null) {
				switch (timing) {
				case OPEN:
					return datum.getAdjustedOpen();
				case CLOSE:
					return datum.getAdjustedClose();
				case LIMIT_OR_ABOVE: {
					double open = datum.getAdjustedOpen();
					if (open >= limit)
						return open;
					else if (datum.getAdjustedHigh() >= limit)
						return limit;
					else
						return 0; // no trade, did not reach limit
				}
				case LIMIT_OR_BELOW: {
					double open = datum.getAdjustedOpen();
					if (open <= limit)
						return open;
					else if (datum.getAdjustedLow() <= limit)
						return limit;
					else
						return 0; // no trade, did not reach limit
				}
				default:
					throw new IllegalArgumentException(String.valueOf(timing));
				}
			} else {
				report(histories, date, "No data for " + symbol + " on " + date);
				return 0;
			}
		} else {
			report(histories, date, "No data for " + symbol);
			return 0;
		}
	}

	/***************************************************************************
	 * Report a message to System.err if it hasn't already been reported. Clears
	 * message memory when date goes backward in time.
	 **************************************************************************/
	private static void report(StockMarketHistory histories, Date date, String msg) {
		if (!REPORT_MISSING_DATA)
			return;
		
		if (cachedForHistories != histories || lastCachedDate == null
				|| date.getTime() < lastCachedDate.getTime()) {
			// reset error message cache.
			cachedForHistories = histories;
			lastCachedDate = date;
			errorCache.clear();
		}
		if (errorCache.add(msg))
			System.err.println(msg);
	}

	private static StockMarketHistory cachedForHistories = null;

	private static Date lastCachedDate = null;

	private static Set<String> errorCache = new HashSet<String>();
}
