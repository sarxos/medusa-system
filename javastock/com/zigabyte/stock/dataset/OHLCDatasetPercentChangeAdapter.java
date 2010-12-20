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

/** Converts each price value to change from starting (item 0) open value:
    (value/startValue - 1.0) * 100.0.  Converts volume value to change from
    starting (item 0) volume. **/
public class OHLCDatasetPercentChangeAdapter 
extends XYDatasetPercentChangeAdapter implements OHLCDataset {
  private final OHLCDataset dataset;
  public OHLCDatasetPercentChangeAdapter(OHLCDataset dataset) {
    super(dataset);
    this.dataset = dataset;
  }
  /** Return open value of item 0 **/
  public double getStartYValue(int series) {
    return this.dataset.getOpenValue(series, 0);
  }
  /** Return volume value of item 0 **/
  public double getStartVolumeValue(int series) {
    return this.dataset.getVolumeValue(series, 0);
  }
  protected double percentVolumeChange(int series, double value) {
    return percentChange(value, getStartVolumeValue(series));
  }
  protected Double percentVolumeChange(int series, Number value) {
    return new Double(percentVolumeChange(series, value.doubleValue()));
  }
  // Interface OHLCDataset
  public double getOpenValue(int series, int item) {
    return percentYChange(series, this.dataset.getOpenValue(series, item));
  }
  public Number getOpen(int series, int item) {
    return percentYChange(series, this.dataset.getOpen(series, item));
  }
  public double getHighValue(int series, int item) {
    return percentYChange(series, this.dataset.getHighValue(series, item));
  }
  public Number getHigh(int series, int item) {
    return percentYChange(series, this.dataset.getHigh(series, item));
  }
  public double getLowValue(int series, int item) {
    return percentYChange(series, this.dataset.getLowValue(series, item));
  }
  public Number getLow(int series, int item) {
    return percentYChange(series, this.dataset.getLow(series, item));
  }
  public double getCloseValue(int series, int item) {
    return percentYChange(series, this.dataset.getCloseValue(series, item));
  }
  public Number getClose(int series, int item) {
    return percentYChange(series, this.dataset.getClose(series, item));
  }
  public double getVolumeValue(int series, int item) {
    return percentVolumeChange(series,
			       this.dataset.getVolumeValue(series, item));
  }
  public Number getVolume(int series, int item) {
    return percentVolumeChange(series, this.dataset.getVolume(series, item));
  }
}
