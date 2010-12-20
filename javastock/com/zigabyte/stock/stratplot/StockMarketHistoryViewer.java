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
package com.zigabyte.stock.stratplot;

import com.zigabyte.stock.data.*;

import org.jfree.chart.*;
import org.jfree.data.*;
import org.jfree.data.xy.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.text.*;
import java.util.*;

/** Displays a window listing the stocks and graphing one chosen history. **/
public class StockMarketHistoryViewer extends JFrame {
  // FIELDS:
  // Data
  private StockMarketHistory stockMarketHistory;

  // UI
  private final JList stockList = new JList();
  {
    stockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    stockList.addListSelectionListener(new ListSelectionListener() {
	public void valueChanged(ListSelectionEvent e) {
	  refreshChart(); // update chart when selected stock changes
	}});
  }
  private final ChartPanel chartPanel = new ChartPanel(null);

  // CONSTRUCTOR:
  /** Create a window to view stockMarketHistory. **/
  public StockMarketHistoryViewer(String name,
				  StockMarketHistory stockMarketHistory) {
    super(name);
    initLayout();
    setStockMarketHistory(stockMarketHistory);
  }
  private void initLayout() {
    Container content = this.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(new JLabel("Select stock to view chart"), BorderLayout.NORTH);
    content.add(new JScrollPane(stockList), BorderLayout.LINE_START);
    content.add(chartPanel, BorderLayout.CENTER);
    this.setSize(1024,768);
  }

  // METHODS:
  /** Changes stock market history, updates stock list, and selects first
      item in list (displays its chart). **/
  protected void setStockMarketHistory(StockMarketHistory stockMarketHistory) {
    if (stockMarketHistory != this.stockMarketHistory) { 
      this.stockMarketHistory = stockMarketHistory;
      refreshList();
      if (stockMarketHistory.size() > 0) { 
	stockList.setSelectedIndex(0);
      }
      refreshChart();
    }
  }
  /** Refresh the stock list after the stock market history is changed. **/
  private void refreshList() {
    if (this.stockMarketHistory != null && this.stockMarketHistory.size() > 0){
      SortedSet<StockHistory> sortedStockHistories =
	new TreeSet<StockHistory>(TO_STRING_COMPARATOR);
      int index = 0;
      Calendar calendar = new GregorianCalendar();
      for (StockHistory stockHistory : stockMarketHistory) {
	if (stockHistory.size() > 0) {
	  calendar.clear();
	  calendar.setTime(stockHistory.get(0).getDate());
	  sortedStockHistories.add(stockHistory);
	}
      }
      stockList.setListData(sortedStockHistories.toArray());
    } else {
      stockList.setListData(new Object[]{});
    }
  }
  /** Used to sort the stock histories by their toString results. **/
  private static Comparator<StockHistory> TO_STRING_COMPARATOR =
    new Comparator<StockHistory>() {
      public int compare(StockHistory o1, StockHistory o2) {
	return o1.toString().compareTo(o2.toString());
      }
    };
  
  /** Update the chart to display the history of the selected StockHistory **/
  private void refreshChart() {
    StockHistory stockHistory =
      (StockHistory) this.stockList.getSelectedValue();
    if (stockHistory != null) {
      // a quick chart to show open/high/low/close/volume data by date
      OHLCDataset chartData = new StockHistoryOHLCDataset(stockHistory);
      chartPanel.setChart(ChartFactory.createCandlestickChart
			  (stockHistory.toString(), null, null,
			   chartData, false));
    } else {
      chartPanel.setChart(null);
    }
  }

  /** Adapter for JFreeChart to access data in a stock history. **/
  private static class StockHistoryOHLCDataset extends AbstractXYDataset
  implements OHLCDataset {
    StockHistory stockHistory;
    StockHistoryOHLCDataset(StockHistory stockHistory) {
      this.stockHistory = stockHistory;
    }
    public DomainOrder getDomainOrder() {
      return DomainOrder.ASCENDING;
    }
    public int getSeriesCount() {
      return 1; // just one history
    } 
    public String getSeriesName(int series) {
      return this.stockHistory.getSymbol();
    }
    public int getItemCount(int series) {
      return this.stockHistory.size();
    }
    public double getXValue(int series, int item) {
      return stockHistory.get(item).getDate().getTime();
    }
    public Number getX(int series, int item) {
      return new Double(getXValue(series, item));
    }
    public double getYValue(int series, int item) {
      return getCloseValue(series, item);
    }
    public Number getY(int series, int item) {
      return new Double(getYValue(series, item));
    }
    public double getOpenValue(int series, int item) {
      return stockHistory.get(item).getAdjustedOpen();
    }
    public Number getOpen(int series, int item) {
      return new Double(getOpenValue(series, item));
    }
    public double getHighValue(int series, int item) {
      return stockHistory.get(item).getAdjustedHigh();
    }
    public Number getHigh(int series, int item) {
      return new Double(getHighValue(series, item));
    }
    public double getLowValue(int series, int item) {
      return stockHistory.get(item).getAdjustedLow();
    }
    public Number getLow(int series, int item) {
      return new Double(getLowValue(series, item));
    }
    public double getCloseValue(int series, int item) {
      return stockHistory.get(item).getAdjustedClose();
    }
    public Number getClose(int series, int item) {
      return new Double(getCloseValue(series, item));
    }
    public double getVolumeValue(int series, int item) {
      return stockHistory.get(item).getVolumeLots();
    }
    public Number getVolume(int series, int item) {
      return new Double(getVolumeValue(series, item));
    }
  }
}
