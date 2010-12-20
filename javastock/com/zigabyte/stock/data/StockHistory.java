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
package com.zigabyte.stock.data;

/** Interface to access name and historical data points of a single stock. **/
public interface StockHistory extends Iterable<StockDataPoint> {
  /** Stock symbol for this stock. **/
  public String getSymbol();
  /** Name of this stock. **/
  public String getName();
  /** Number of data points in this history. **/
  public int size();
  /** Get a datapoint at index.**/
  public StockDataPoint get(int index);
  /** Get a datapoint at dateTime, or null if no data point at that dateTime.**/
  public StockDataPoint get(java.util.Date dateTime);
  /** Get datapoint at dateTime,
      else if none at dateTime, get latest datapoint before dateTime,
      else if before all datapoints, return null. **/
  public StockDataPoint getAtOrBefore(java.util.Date dateTime);
  /** Get datapoint at dateTime,
      else if none at dateTime, get earliest datapoint after dateTime,
      else if after all datapoints, return null. **/
  public StockDataPoint getAtOrAfter(java.util.Date dateTime);
  /** Get datapoint at dateTime,
      else if none at dateTime, get latest datapoint before dateTime,
      else if before all datapoints, return -1. **/
  public int getIndexAtOrBefore(java.util.Date dateTime);
  /** Get datapoint at dateTime,
      else if none at dateTime, get earliest datapoint after dateTime,
      else if after all datapoints, return -1. **/
  public int getIndexAtOrAfter(java.util.Date dateTime);
  /** Get the index corresonding to date, or
      ~index [i.e,. -(index+1)] where date would be inserted.
      @see java.util.Collections#binarySearch **/
  public int binarySearch(java.util.Date dateTime);
}

