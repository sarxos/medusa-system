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

/** Interface to access data at a particular date. **/
public interface StockDataPoint {
  /** Date on which these trades were made.**/
  public java.util.Date getDate();
  /** First traded price per share on this date, adjusted for share splits.**/
  public float getAdjustedOpen();
  /** High traded price per share on this date, adjusted for share splits.**/
  public float getAdjustedHigh();
  /** Low traded price per share on this date, adjusted for share splits.**/
  public float getAdjustedLow();
  /** Last traded price per share on this date, adjusted for share splits.**/
  public float getAdjustedClose();
  /** Number of traded lots (100 shares) on this date, adjusted for share splits.**/
  public float getVolumeLots();
  /** Dividend paid on this date, per share, adjusted for share splits.**/
  public float getDividendAmount();
  /** True if a dividend was scheduled for this date. **/
  public boolean isDividendDate();
  /** True if shares were split on this date. **/
  public boolean isSplitDate();

  public enum FieldID {
    OPEN, HIGH, LOW, CLOSE, VOLUME;
  }
}
