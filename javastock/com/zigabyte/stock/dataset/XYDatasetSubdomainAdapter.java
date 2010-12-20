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

/** Base abstract class for a contiguous part of the domain (x values) of
    another dataset.  Maps calls to the source dataset.  **/
public class XYDatasetSubdomainAdapter extends XYDatasetAdapter {
  private final XYDataset dataset;
  private final int[] itemOffsets, itemCounts;
  public XYDatasetSubdomainAdapter(XYDataset dataset, double loX, double hiX) {
    super(dataset);
    this.dataset = dataset;
    int seriesCount = dataset.getSeriesCount();
    this.itemOffsets = new int[seriesCount];
    this.itemCounts = new int[seriesCount];
    for (int series = 0; series < seriesCount; series++) {
      int loItem = binarySearchForX(dataset, series, loX);
      int hiItem = binarySearchForX(dataset, series, hiX);
      if (hiItem < dataset.getItemCount(series) && 
	  hiX == dataset.getXValue(series, hiItem))
	hiItem++; // exclusive, so count will be accurate

      this.itemOffsets[series] = loItem;
      this.itemCounts[series] = hiItem - loItem;
    }
  }

  private static int binarySearchForX(XYDataset data, int series, double x) {
    // inclusive lo bound, exclusive hi bound
    int lo = 0, hi = data.getItemCount(series); 
    while (lo < hi) {
      int mid = (lo + hi) / 2;
      double midX = data.getXValue(series, mid);
      if (x < midX)
	hi = mid;
      else if (x > midX)
	lo = mid + 1;
      else
	return mid;
    }
    return lo; // not found, return index of next date
  }


  protected int mapItem(int series, int item) {
    // allow access to items outside domain, for indicators (e.g., 10day avg).
    // Use item 0 value if item < 0.
    return Math.max(0, this.itemOffsets[series] + item);
  }
  // Interface XYDataset
  public int getItemCount(int series) {
    return this.itemCounts[series];
  }
  public double getXValue(int series, int item) {
    return this.dataset.getXValue(series, mapItem(series, item));
  }
  public Number getX(int series, int item) {
    return this.dataset.getX(series, mapItem(series, item));
  }
  public double getYValue(int series, int item) {
    return this.dataset.getYValue(series, mapItem(series, item));
  }
  public Number getY(int series, int item) {
    return this.dataset.getY(series, mapItem(series, item));
  }
}
