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

import org.jfree.data.general.*;

/** Base abstract class that forwards {@link Dataset} methods to
    another dataset, for creating an adapter that overrides
    a subset of the methods. **/
public abstract class DatasetAdapter implements Dataset {
  private final Dataset dataset;
  public DatasetAdapter(Dataset dataset) {
    this.dataset = dataset;
  }
  // Interface Dataset
  public void addChangeListener(DatasetChangeListener listener) {
    this.dataset.addChangeListener(listener);
  }
  public void removeChangeListener(DatasetChangeListener listener) {
    this.dataset.removeChangeListener(listener);
  }
  public DatasetGroup getGroup() {
    return this.dataset.getGroup();
  }
  public void setGroup(DatasetGroup group) {
    this.dataset.setGroup(group);
  }
}
