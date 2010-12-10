package com.sarxos.gpwnotifier.examples;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.ApplicationFrame;

import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.entities.Index;
import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;
import com.sarxos.gpwnotifier.entities.SignalType;
import com.sarxos.gpwnotifier.generator.Rejczak;


public class RejczakExample extends ApplicationFrame {

	private static final long serialVersionUID = 1623709434533235381L;

	public RejczakExample() {
		super("Rejczak Example");
	}

	public static void main(String[] args) throws QuotesReaderException {
		
		int N = 300;
		
		double charge = 3;
		
		double max_d = 0;
		double max_income = 0;

		QuotesReader<Index> reader = new StoqReader<Index>(Index.class);
		List<Index> data = reader.read(new File("wig20_d.csv").toURI());
		
//		/* NOTE!
//		 * Create dataset for the candlestick chart.   
//		 */
//		
//        Date[] dates = new Date[N];
//        double[] high = new double[N];
//        double[] low = new double[N];
//        double[] open = new double[N];
//        double[] close = new double[N];
//        double[] volume = new double[N];		
//		
//        Index idx = data.get(data.size() - N);
//        int i = 0;
//
//        do {
//        	
//			dates[i] = idx.getDate();
//			high[i] = idx.getHigh();
//			low[i] = idx.getLow();
//			open[i] = idx.getOpen();
//			close[i] = idx.getClose();
//			volume[i] = idx.getVolume();
//			i++;
//        } while((idx = (Index) idx.next()) != null);
//        
//        DefaultHighLowDataset dataset = new DefaultHighLowDataset("", dates, high, low, open, close, volume);
//	
//        JFreeChart chart = ChartFactory.createCandlestickChart(
//                "WIG20", "Date", "Price", dataset, false);
//        chart.getXYPlot().getRangeAxis().setRange(2000, 2800);
//        chart.setAntiAlias(false);
//        
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(600, 350));
//        
//        ApplicationFrame frame = new RejczakExample();
//        
//        frame.setContentPane(chartPanel);
//        frame.pack();
//        frame.setVisible(true);
        
		/* NOTE!
		 * Generate signals.
		 */
		
//		double d = 0.4;
		
		for (double d = -1; d < 1; d += 0.01) {
		
			//System.out.println("--- coef " + d);
			
			SignalGenerator<Quote> rejczak = new Rejczak(d);
			
			List<Signal> signals = rejczak.generate(data.toArray(new Quote[data.size()]), N);
			
			Signal signal = null;
			Date date = null;
			SignalType type = null;
			
			Iterator<Signal> iterator = signals.iterator();
			
			Signal tmp = null;
			int points = 0;
			int wallet = 0;
			
			while (iterator.hasNext()) {
				signal = iterator.next();
				date = signal.getDate();
				type = signal.getType();
				
				switch (signal.getType()) {
					case SELL:
						if (wallet == 0) {
							wallet -= 1;
							points -= charge * 2 / 3;
						} else if (wallet > 0) {
							wallet -= 2;
							points -= charge;
							Quote q1 = tmp.getQuote().next();
							Quote q2 = signal.getQuote().next();
							if (q1 != null && q2 != null) {
								points += Math.abs(q2.getOpen() - q1.getOpen());
							}
						} else {
							throw new ArithmeticException("Imposiible situation. Cannot sell negative wallet.");
						}
						break;
					case BUY:
						if (wallet == 0) {
							wallet += 1;
							points -= charge * 2 / 3;
						} else if (wallet < 0) {
							wallet += 2;
							points -= charge;
							Quote q1 = tmp.getQuote().next();
							Quote q2 = signal.getQuote().next();
							if (q1 != null && q2 != null) {
								points += Math.abs(q2.getOpen() - q1.getOpen());
							}
	 					} else {
	 						throw new ArithmeticException("Imposiible situation. Cannot buy positive wallet.");
	 					}
						break;
				}
				
//				System.out.println(
//						Quote.DATE_FORMAT.format(date) + " " + type + " " +
//						(tmp != null ? tmp.getQuote().next() : "x") + " : " + signal.getQuote().next()
//				);

				tmp = signal;
			}
			
			double income = points * 10;
			
			if (income > max_income) {
				max_income = income;
				max_d = d;
			}
			
//			System.out.println("\nINCOME: " + (points * 10) + " PLN");
		}
		
		System.out.println("max income: " + max_income + " PLN");
		System.out.println("max coef:   " + max_d);
	}
}
