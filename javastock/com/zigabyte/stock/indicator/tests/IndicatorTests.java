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
package com.zigabyte.stock.indicator.tests;

import org.suiterunner.*;

public class IndicatorTests extends Suite {
  public static void main(String[] ignore) {
    Runner.main(new String[]{
      "-oFBAR", "-s", "com.zigabyte.stock.indicator.tests.IndicatorTests"});
  }
  public IndicatorTests() {
    addSubSuite(new MovingAverageTests());
    addSubSuite(new MovingMaximumTests());
    addSubSuite(new MovingDollarVolumeTests());
  }
}
