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
package com.zigabyte.stock.dataset;

import org.jfree.data.*;
import org.jfree.data.xy.*;

import java.util.Date;

/** Converts each Y value to change from starting (item 0) Y value:
    (value/startValue - 1.0) * 100.0 **/
public class XYDatasetPercentChangeAdapter extends XYDatasetAdapter {
  private final XYDataset dataset;
  public XYDatasetPercentChangeAdapter(XYDataset dataset) {
    super(dataset);
    this.dataset = dataset;
  }
  /** Return (value/start - 1.0) * 100.0 **/
  protected double percentChange(double value, double start) {
    return (value/start - 1.0) * 100.0;
  }
  /** Return y value of item 0 **/
  protected double getStartYValue(int series) {
    return this.dataset.getYValue(series, 0);
  }
  protected double percentYChange(int series, double value) {
    return percentChange(value, getStartYValue(series));
  }
  protected Double percentYChange(int series, Number value) {
    return new Double(percentYChange(series, value.doubleValue()));
  }
  // Interface XYDataset
  public double getYValue(int series, int item) {
    return percentYChange(series, this.dataset.getYValue(series, item));
  }
  public Number getY(int series, int item) {
    return percentYChange(series, this.dataset.getY(series, item));
  }
}
