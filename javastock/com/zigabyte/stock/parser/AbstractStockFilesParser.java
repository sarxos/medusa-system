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
package com.zigabyte.stock.parser;

import com.zigabyte.stock.data.*;
import java.io.*;
import java.util.Date;

public abstract class AbstractStockFilesParser
implements StockMarketHistoryFactory {
  // FIELD
  /** whether to try to use {@link ShortStockDataPoint}. **/
  private final boolean useShortRep;

  /** Construct a factory that creates {@link DefaultStockDataPoint}s. **/
  protected AbstractStockFilesParser() {
    this(false);
  }

  /** Construct a factory that may create 
      {@link ShortStockDataPoint} or {@link DefaultStockDataPoint}s.
      @param useShortRep if true, tries to save space by creating data points 
      using {@link ShortStockDataPoint}, which <em>assumes prices are to
      the nearest cent</em> (so may not be suitable for fractions that
      cannot be expressed in cents).
      If a price is over 32767 cents, backs out for that data point and uses
      {@link DefaultStockDataPoint}.  If useShortRep is false, always
      creates {@link DefaultStockDataPoint}.
      @see #createStockDataPoint
   **/
  protected AbstractStockFilesParser(boolean useShortRep) {
    this.useShortRep = useShortRep;
  }

  // IMPLEMENT StockMarketHistoryFactory
  /** Create a new StockMarketHistory and load histories from the directory,
      recursing into subdirectories.
      @param dir file or root directory containing stock data
  **/
  public StockMarketHistory loadHistory(File dir) throws IOException {
    DefaultStockMarketHistory stockHistories = createStockMarketHistory(dir);
    loadHistory(dir, stockHistories);
    return stockHistories;
  }

  /** Load histories in the directory, recursing into subdirectories.
      @param file file or root directory containing stock data
      @param stockHistories add parsed histories to stockHistories
   **/
  public abstract void loadHistory(File file, 
				   DefaultStockMarketHistory stockHistories)
    throws IOException;

  /** Creates an empty {@link DefaultStockMarketHistory}. **/
  protected DefaultStockMarketHistory createStockMarketHistory(File dir) {
    return new DefaultStockMarketHistory(dir.getPath());
  }
  /** Creates an empty {@link DefaultStockHistory} with symbol and name.**/ 
  protected DefaultStockHistory createStockHistory(String symbol,
						   String name) {
    return new DefaultStockHistory(symbol, name);
  }

  /** If true useShortRep was passed to constructor, tries creating
      data points using {@link ShortStockDataPoint}, which
      <em>assumes prices are to the nearest cent</em> (so may not be
      suitable for other currencies).  If a price is over 32767 cents,
      backs out and uses {@link DefaultStockDataPoint}.
      If useShortRep is false, always creates {@link DefaultStockDataPoint}.

      <p>May be overridden in derived classes to build another
      class to store data points.

      <p><em>Assume data is preadjusted, contains no split
      or dividend dates</em>.
  **/
  protected StockDataPoint createStockDataPoint(Date date,
						float open, float high,
						float low, float close,
						float volume) {
    try {
      if (this.useShortRep)
	return new ShortStockDataPoint(date, open, high, low, close, volume);
    } catch (IllegalArgumentException e) {}
    // values out of range for short, so use default (floats).
    return new DefaultStockDataPoint(date, open, high, low, close, volume);
  }

}
