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
package com.zigabyte.stock.sample;

import com.zigabyte.stock.data.*;
import com.zigabyte.stock.trade.*;
import com.zigabyte.stock.tradeobserver.*;
import com.zigabyte.stock.strategy.*;
import com.zigabyte.stock.parser.*;
import com.zigabyte.stock.parser.metastock.*;
import java.io.*;
import java.text.*;
import java.util.*;

/** Run a sample simulation using {@link SundayPeaks} strategy on
    metastock data.<p>

    Uses {@link DefaultTradingAccount} with the following observers:
    {@link TradeTraceObserver}, {@link PeriodTraceObserver}, 
    {@link TradeWinLossObserver}, {@link PeriodWinLossObserver}.
    If the metastock data includes the S&P500 index (symbol "$SP"),
    then also includes {@link BetaObserver}.<p>
    
    Uses {@link TradeTraceObserver} to output a trace of trades, and the
    resulting balances after the trades on a day, to file TRADE.LOG.
    Uses {@link PeriodTraceObserver} to output monthly account balances
    to MONTHLY.LOG.<p>

    (There may also be warning messages that data for a stock was
    missing on some days.)<p>

    Produces a report like the following:
    <pre>
      Dates:    01Jun2004 - 24Sep2004
      Strategy: SundayPeaks(0.2)

      Initial value:                          $10,000.00
	Cash Remaining:          $136.82
	Stock remaining:       $8,963.46
      Final value:                             $9,100.28
      Total Profit/Loss:                        -$899.72

      Winning positions remaining:       3
	Average winning position profit:          $64.01
      Losing positions remaining:        1
	Average losing position loss:         -$1,033.55

      Winning trade count:               1
	Average winning trade profit:            $224.63
      Losing trade count:                3
	Average losing trade loss:               -$94.28

      Winning Month Count:               2
	Best Month    (01Jun2004):               $349.23
      Losing Month Count:                2
	Worst Month   (01Sep2004):              -$824.66

      Beta vs. $SP:                      0.902
    </pre>
 **/
public class StrategySimulatorSample { 
  static double initialCash = 10000.00;
  static double perTradeFee = 1.00;
  static double perShareTradeCommission = 0.02;
  static String betaCompareIndexSymbol = "$SP"; // S&P 500 Index
  static double minCashMaxBuyFraction = 1.0/5.0;

  static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMMyyyy");
  static final NumberFormat DOLLAR_FORMAT = new DecimalFormat("$#,##0.00");
  static final NumberFormat BETA_FORMAT = new DecimalFormat("#0.000");
  /** @param parameters
   *  <pre>
   *  [-start ddMMMyyyy] [-end ddMMMyyyy] -metastock dir
   *  </pre>
   *  If start date is omitted, a date 3 months ago is used.<br>
   *  If end date is omitted, today is used.<br>
   *  Dir is directory of Metastock format data.
   **/
  public static void main(String... parameters) {
    TradingStrategy strategy = new SundayPeaks(minCashMaxBuyFraction);
    try {
      Calendar calendar = new GregorianCalendar();
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      Date end = calendar.getTime();    // default end:   today
      calendar.add(Calendar.MONTH, -3); // default start: three months ago
      Date start = calendar.getTime();
      int i;
      for (i = 0; i < parameters.length; i++) { 
	String parameter = parameters[i].toLowerCase();
	if ("-start".equals(parameter))
	  start = DATE_FORMAT.parse(parameters[++i]);
	else if ("-end".equals(parameter))
	  end = DATE_FORMAT.parse(parameters[++i]);
	else 
	  break;
      }
      if (i == parameters.length)
	parameterExit();
      String type = parameters[i++].toLowerCase();
      StringBuffer buf = new StringBuffer();
      for (int i0 = i; i < parameters.length; i++) {
	if (i > i0) buf.append(' ');
	buf.append(parameters[i]);
      }
      File dir = new File(buf.toString());
      StockMarketHistory histories;
      if ("-metastock".equals(type))
	histories = new MetastockParser(true).loadHistory(dir);
      else if ("-serialized".equals(type))
	histories = new SerializedStockFilesParser(false).loadHistory(dir);
      else if ("-serializedgz".equals(type))
	histories = new SerializedStockFilesParser(true).loadHistory(dir);
      else throw new IllegalArgumentException(type);

      // adjust dates
      if (!histories.hasTradingData(start))
	start = findLaterTradingDate(histories, start);
      if (!histories.hasTradingData(end))
	end = findEarlierTradingDate(histories, end);

      // write to System.out and to files TRADE.LOG, MONTHLY.LOG
      PrintWriter reportOut =
	new PrintWriter(new OutputStreamWriter(System.out), true);
      PrintWriter tradeOut =
	new PrintWriter(new FileWriter("TRADE.LOG"), true);
      PrintWriter monthlyOut =
	new PrintWriter(new FileWriter("MONTHLY.LOG"), true);
      runSimulation(strategy, histories, dir, start, end,
		    reportOut, tradeOut, monthlyOut);
      return;
    } catch (Exception e) {
      e.printStackTrace();
      parameterExit();
    }
  }
  private static void parameterExit() {
    System.err.println
      ("\nParameters: "+
       "[-start ddMMMyyyy] [-end ddMMMyyyy] -TYPE path"+
       "\nwhere -TYPE is one of\n"+
       "\n -metastock metastockDirectory"+
       "\n -serialized serializedFile.ser"+
       "\n -serializedgz serializedFile.ser.gz");
    System.exit(-1);
  }

  /** Simulate using strategy over histories from start date to end date,
      then write report as described {@link StrategySimulatorSample above}.
  **/
  public static void runSimulation(TradingStrategy strategy,
				   StockMarketHistory histories, File dir,
				   Date start, Date end,
				   PrintWriter out,
				   PrintWriter traceOut,
				   PrintWriter monthlyOut) {
    DefaultTradingAccount account =
      new DefaultTradingAccount(histories,
				perTradeFee, perShareTradeCommission);

    // Observers
    account.addTradeObserver(new TradeTraceObserver(true, traceOut));
    account.addTradeObserver(new PeriodTraceObserver(1, Calendar.MONTH, true,
						     monthlyOut));

    TradeWinLossObserver winLossObserver = new TradeWinLossObserver();
    account.addTradeObserver(winLossObserver);

    PeriodWinLossObserver monthObserver =
      new PeriodWinLossObserver(1, Calendar.MONTH, true);
    account.addTradeObserver(monthObserver);

    boolean hasBetaIndex = (histories.get(betaCompareIndexSymbol) != null);
    BetaObserver betaObserver = null;
    if (hasBetaIndex) {
      betaObserver = new BetaObserver(betaCompareIndexSymbol);
      account.addTradeObserver(betaObserver);
    } 


    // run simulation
    account.initialize(start, initialCash);
    DefaultTradingSimulator simulator = new DefaultTradingSimulator(histories);
    simulator.runStrategy(strategy, account, start, end);

    // report
    out.println("--------------------------------------------------");
    out.println("Data:     "+dir);
    out.println("Dates:    "+
		DATE_FORMAT.format(start)+" - "+DATE_FORMAT.format(end));
    out.println("Strategy: "+ strategy);
    // report value
    out.println();
    out.println("Initial value:                     "+
		formatDollars(initialCash));
    out.println("  Cash Remaining:  "+
		formatDollars(account.getCurrentCashBalance()));
    out.println("  Stock remaining: "+
		formatDollars(account.getCurrentStockValue()));
    out.println("Final value:                       "+
		formatDollars(account.getCurrentAccountValue()));
    out.println("Total Profit/Loss:                 "+
		formatDollars(account.getCurrentAccountValue()
			      - initialCash));

    // report remaining positions
    if (account.getStockPositionCount() > 0) { 
      // collect winning and losing positions
      List<StockPosition> winningPositions = new ArrayList<StockPosition>();
      List<StockPosition> losingPositions = new ArrayList<StockPosition>();
      List<StockPosition> evenPositions = new ArrayList<StockPosition>();
      double winningPositionsProfit = 0, losingPositionsLoss = 0;
      for (StockPosition position : account) {
	double totalCurrentValue = account.getCurrentStockValue(position);
	double totalCostBasis = position.getCostBasis() * position.getShares();
	double projectedProfitOrLoss = totalCurrentValue - totalCostBasis;
	if (projectedProfitOrLoss > 0) {
	  winningPositions.add(position);
	  winningPositionsProfit += projectedProfitOrLoss;
	} else if (projectedProfitOrLoss < 0) { 
	  losingPositions.add(position);
	  losingPositionsLoss += projectedProfitOrLoss;
	} else if (projectedProfitOrLoss == 0) {
	  evenPositions.add(position);
	} else {// in case NaN data somewhere
	  System.err.println(position.getSymbol() + ": "+
			     "value="+totalCurrentValue+", "+
			     "cost="+totalCostBasis);
	}
      }
      out.println();
      if (winningPositions.size() > 0) {
	out.println("Winning positions remaining:       "+
		    winningPositions.size());
	out.println("  Average winning position profit: "+
		    formatDollars(winningPositionsProfit
				  /winningPositions.size()));
	
      }
      if (losingPositions.size() > 0) {
	out.println("Losing positions remaining:        "+
		    losingPositions.size());
	out.println("  Average losing position loss:    "+
		    formatDollars(losingPositionsLoss
				  /losingPositions.size()));
	
      }
      if (evenPositions.size() > 0) {
	out.println("Even position remaining:           "+
		    evenPositions.size());
      }
    }

    // report trades
    out.println();
    out.println("Winning trade count:               "+
		winLossObserver.getWinningTradeCount());
    out.println("  Average winning trade profit:    "+
		formatDollars(winLossObserver.getAverageWinningTradeProfit()));
    out.println("Losing trade count:                "+
		winLossObserver.getLosingTradeCount());
    out.println("  Average losing trade loss:       "+
		formatDollars(winLossObserver.getAverageLosingTradeLoss()));
    if (winLossObserver.getEvenTradeCount() > 0)
      out.println("Even trade count:                  "+
		  winLossObserver.getEvenTradeCount());

    // report months
    out.println();
    if (monthObserver.getBestProfitPeriodStartDate() != null) { 
      out.println("Winning Month Count:               "+
		  monthObserver.getWinningPeriodCount());
      out.println("  Best Month    ("+
		  DATE_FORMAT.format
		  (monthObserver.getBestProfitPeriodStartDate())+"):       "+
		  formatDollars(monthObserver.getBestPeriodProfit()));
    } else {
      out.println("(No Profitable months)");
    }
    if (monthObserver.getWorstLossPeriodStartDate() != null) { 
      out.println("Losing Month Count:                "+
		  monthObserver.getLosingPeriodCount());
      out.println("  Worst Month   ("+
		  DATE_FORMAT.format
		  (monthObserver.getWorstLossPeriodStartDate())+"):       "+
		  formatDollars(monthObserver.getWorstPeriodLoss()));
    } else {
      out.println("(No Losing months)");
    }

    // report beta
    out.println();
    if (betaObserver != null) 
      out.println("Beta vs. "+betaCompareIndexSymbol+":                      "+
		  BETA_FORMAT.format(betaObserver.computeBeta()));
    else
      out.println("(Beta: No "+betaCompareIndexSymbol+" in data.)");
  }
  private static StringBuffer formatDollars(double d) {
    return fillWidth(-15, DOLLAR_FORMAT.format(d));
  }
  private static StringBuffer fillWidth(int width, String s) {
    StringBuffer buf = new StringBuffer();
    if (width < 0) { 
      width += s.length();
      while (width++ < 0)
	buf.append(' ');
      buf.append(s);
    } else {
      buf.append(s);
      width -= s.length();
      while (width-- > 0)
	buf.append(' ');
    }
    return buf;
  }
  /** Tries up to 10 days after start to skip holidays.
      If still none found, throws IllegalArgumentException. **/
  private static Date findLaterTradingDate(StockMarketHistory histories,
					   Date start) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(start);
    for (int i=0; i <= 10 && !histories.hasTradingData(calendar.getTime()); i++)
      calendar.add(Calendar.DAY_OF_MONTH, 1);
    if (histories.hasTradingData(calendar.getTime()))
      return calendar.getTime();
    else {
      for (StockHistory history : histories) { 
	if (history.size() > 0) {
	  Date firstStockFirstDate = history.get(0).getDate();
	  throw new IllegalArgumentException
	    ("No data near "+DATE_FORMAT.format(start)+
	     ", consider "+DATE_FORMAT.format(firstStockFirstDate));
	}
      }
      // no non-empty stocks found
      throw new IllegalArgumentException("No data");
    }
  }
  /** Tries up to 10 days before end to skip holidays.
      If still none found, throws IllegalArgumentException. **/
  private static Date findEarlierTradingDate(StockMarketHistory histories,
					     Date end) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(end);
    for (int i=0; i <= 10 && !histories.hasTradingData(calendar.getTime()); i++)
      calendar.add(Calendar.DAY_OF_MONTH, -1);
    if (histories.hasTradingData(calendar.getTime()))
      return calendar.getTime();
    else {
      for (StockHistory history : histories) { 
	if (history.size() > 0) {
	  Date firstStockLastDate = history.get(history.size()-1).getDate();
	  throw new IllegalArgumentException
	    ("No data near "+DATE_FORMAT.format(end)+
	     ", consider "+DATE_FORMAT.format(firstStockLastDate));
	}
      }
      // no non-empty stocks found
      throw new IllegalArgumentException("No data");
    }
  }

}

