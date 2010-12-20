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

/** Base abstract class that forwards {@link XYDataset} methods to another
    dataset, for creating an adapter that overrides a subset of the methods. **/
public class XYDatasetAdapter extends SeriesDatasetAdapter implements XYDataset{
  private final XYDataset dataset;
  public XYDatasetAdapter(XYDataset dataset) {
    super(dataset);
    this.dataset = dataset;
  }
  // Interface XYDataset
  public DomainOrder getDomainOrder() {
    return this.dataset.getDomainOrder();
  }
  public int getItemCount(int series) {
    return this.dataset.getItemCount(series);
  }
  public double getXValue(int series, int item) {
    return this.dataset.getXValue(series, item);
  }
  public Number getX(int series, int item) {
    return this.dataset.getX(series, item);
  }
  public double getYValue(int series, int item) {
    return this.dataset.getYValue(series, item);
  }
  public Number getY(int series, int item) {
    return this.dataset.getY(series, item);
  }
}
