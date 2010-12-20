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

/** A space-saving implementation of StockDataPoint.
    Assumes prices are in dollars to the nearest cent,
    and stores each price as cents in a short integer (16 bits)
    (rather than a 32-bit float or 64-bit double).
    The prices are converted to floats when retreived.
    The largest price this representation can store is
    Short.MAX_VALUE cents: $327.67.  Use another representation
    such as {@link DefaultStockDataPoint} for points with larger
    prices or with prices with smaller digits than cents.
    Also stores only absolute time, dropping timezone offset in Dates.
    @see DefaultStockDataPoint **/
public class ShortStockDataPoint implements StockDataPoint, Serializable {
  private long date;
  private short adjustedOpen, adjustedHigh, adjustedLow, adjustedClose;
  private float volume;
  private short dividend = 0;
  private byte flags = 0;
  private static final byte IS_DIVIDEND_FLAG = (byte) 1<<0;
  private static final byte IS_SPLIT_FLAG = (byte) 1<<1;
  /** Create StockDataPoint with given data, defaulting dividend to 0,
      isDividendDate to false, and isSplitDate to false.
      @throws IllegalArgumentException if any price &gt; 327.67,
      or if round(price*100f)/100f != price. **/
  public ShortStockDataPoint(Date date,
			     float adjustedOpen,
			     float adjustedHigh,
			     float adjustedLow,
			     float adjustedClose,
			     float volume) throws IllegalArgumentException {
    this.date = date.getTime();
    this.adjustedOpen = toCentsShort(adjustedOpen);
    this.adjustedHigh = toCentsShort(adjustedHigh);
    this.adjustedLow  = toCentsShort(adjustedLow);
    this.adjustedClose= toCentsShort(adjustedClose);
    this.volume = volume;
  }
  /** Create StockDataPoint with given data.
      @throws IllegalArgumentException if any price &gt; 327.67,
      or if round(price*100f)/100f != price. **/
  public ShortStockDataPoint(Date date,
			     float adjustedOpen,
			     float adjustedHigh,
			     float adjustedLow,
			     float adjustedClose,
			     float volume,
			     float dividend,
			     boolean isDividendDate,
			     boolean isSplitDate)
  throws IllegalArgumentException {
    this(date, adjustedOpen, adjustedHigh, adjustedLow, adjustedClose, volume);
    this.dividend = toCentsShort(dividend);
    if (isDividendDate) this.flags |= IS_DIVIDEND_FLAG;
    if (isSplitDate)    this.flags |= IS_SPLIT_FLAG;
  }
  /** Convert float price to cents price
      @throws IllegalArgumentException if price &gt; 327.67,
      or if round(price*100f)/100f != price. **/
  private short toCentsShort(float price) {
    // assume price is in dollars and cents, convert to cents
    int centsPrice = Math.round(price * 100f);
    if (centsPrice > Short.MAX_VALUE)
      throw new IllegalArgumentException
	(price+" > "+Short.MAX_VALUE+"cents");
    else if (centsPrice < Short.MIN_VALUE)
      throw new IllegalArgumentException
	(price+" < "+Short.MIN_VALUE+"cents");
    else if (centsPrice / 100f != price)
      throw new IllegalArgumentException
	(price+" != "+centsPrice+"cents");
    else
      return (short) centsPrice;
  }
  /** Convert cents price to float (cents/100f) **/
  private float fromCentsShort(short centsPrice) {
    return ((float) centsPrice)/100f;
  }

  public java.util.Date getDate() {
    return new Date(this.date);
  }
  public float getAdjustedOpen() {
    return fromCentsShort(this.adjustedOpen);
  }
  public float getAdjustedHigh() {
    return fromCentsShort(this.adjustedHigh);
  }
  public float getAdjustedLow() {
    return fromCentsShort(this.adjustedLow);
  }
  public float getAdjustedClose() {
    return fromCentsShort(this.adjustedClose);
  }
  public float getVolumeLots() {
    return this.volume;
  }
  public float getDividendAmount() {
    return fromCentsShort(this.dividend);
  }
  public boolean isDividendDate() {
    return (this.flags & IS_DIVIDEND_FLAG) != 0;
  }
  public boolean isSplitDate() {
    return (this.flags & IS_SPLIT_FLAG) != 0;
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
