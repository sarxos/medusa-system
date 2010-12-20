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
package com.zigabyte.stock.trade;

/** Identifies timing of buy or sell order.
    @see PriceTiming **/
public enum OrderTiming {
  /** A market order to be executed on the next trading day
      at the opening price. **/
  NEXT_DAY_OPEN,
  /** A market order to be executed on the next trading day
      at the closing price. **/
  NEXT_DAY_CLOSE,
  /** A limit order to be executed on the next trading day,
      at the limit price or lower for buy orders,
      at the limit price or higher for sell orders. **/
  NEXT_DAY_LIMIT,

  /** Good-Til-Cancelled stop-loss order. **/
  NEXT_DAY_STOP
}
