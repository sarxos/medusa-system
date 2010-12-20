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

/** Base abstract class for a contiguous part of the domain (x values) of
    another dataset.  Maps calls to the source dataset.  **/
public class OHLCDatasetSubdomainAdapter extends XYDatasetSubdomainAdapter
implements OHLCDataset {
  private final OHLCDataset dataset;
  public OHLCDatasetSubdomainAdapter(OHLCDataset dataset,
				     Date loDate, Date hiDate) {
    super(dataset, loDate.getTime(), hiDate.getTime());
    this.dataset = dataset;
  }
  // Interface OHLCDataset
  public double getOpenValue(int series, int item) {
    return this.dataset.getOpenValue(series, mapItem(series, item));
  }
  public Number getOpen(int series, int item) {
    return this.dataset.getOpen(series, mapItem(series, item));
  }
  public double getHighValue(int series, int item) {
    return this.dataset.getHighValue(series, mapItem(series, item));
  }
  public Number getHigh(int series, int item) {
    return this.dataset.getHigh(series, mapItem(series, item));
  }
  public double getLowValue(int series, int item) {
    return this.dataset.getLowValue(series, mapItem(series, item));
  }
  public Number getLow(int series, int item) {
    return this.dataset.getLow(series, mapItem(series, item));
  }
  public double getCloseValue(int series, int item) {
    return this.dataset.getCloseValue(series, mapItem(series, item));
  }
  public Number getClose(int series, int item) {
    return this.dataset.getClose(series, mapItem(series, item));
  }
  public double getVolumeValue(int series, int item) {
    return this.dataset.getVolumeValue(series, mapItem(series, item));
  }
  public Number getVolume(int series, int item) {
    return this.dataset.getVolume(series, mapItem(series, item));
  }
}
