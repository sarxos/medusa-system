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

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.io.Serializable;

/** Default implementation of {@link StockDataPoint}.
    Stores each price as a float.
    @see ShortStockDataPoint **/
public class DefaultStockDataPoint implements StockDataPoint, Serializable {
  private Date date;
  private float adjustedOpen, adjustedHigh, adjustedLow, adjustedClose;
  private float volume;
  private float dividend = 0;
  private boolean isDividendDate = false, isSplitDate = false;
  public DefaultStockDataPoint(Date date,
			       float adjustedOpen,
			       float adjustedHigh,
			       float adjustedLow,
			       float adjustedClose,
			       float volume) {
    this.date = date;
    this.adjustedOpen = adjustedOpen;
    this.adjustedHigh = adjustedHigh;
    this.adjustedLow  = adjustedLow;
    this.adjustedClose= adjustedClose;
    this.volume = volume;
  }
  public DefaultStockDataPoint(Date date,
			       float adjustedOpen,
			       float adjustedHigh,
			       float adjustedLow,
			       float adjustedClose,
			       float volume,
			       float dividend,
			       boolean isDividendDate,
			       boolean isSplitDate) {
    this.date = date;
    this.adjustedOpen = adjustedOpen;
    this.adjustedHigh = adjustedHigh;
    this.adjustedLow  = adjustedLow;
    this.adjustedClose= adjustedClose;
    this.volume = volume;
    this.dividend = dividend;
    this.isDividendDate = isDividendDate;
    this.isSplitDate = isSplitDate;
  }
  public java.util.Date getDate() {
    return this.date;
  }
  public float getAdjustedOpen() {
    return this.adjustedOpen;
  }
  public float getAdjustedHigh() {
    return this.adjustedHigh;
  }
  public float getAdjustedLow() {
    return this.adjustedLow;
  }
  public float getAdjustedClose() {
    return this.adjustedClose;
  }
  public float getVolumeLots() {
    return this.volume;
  }
  public float getDividendAmount() {
    return this.dividend;
  }
  public boolean isDividendDate() {
    return this.isDividendDate;
  }
  public boolean isSplitDate() {
    return this.isSplitDate;
  }
  public String toString() {
    return (DATE_FORMAT.format(this.date)+
	    "["+DOLLAR_FORMAT.format(this.adjustedOpen)+
	    "("+DOLLAR_FORMAT.format(this.adjustedHigh)+
	    ","+DOLLAR_FORMAT.format(this.adjustedLow)+
	    ")"+DOLLAR_FORMAT.format(this.adjustedClose)+"]"+
	    "#"+this.volume);
  }
  private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMMyyyy");
  private static DecimalFormat DOLLAR_FORMAT = new DecimalFormat("$#,##0.00");
}
