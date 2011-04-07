package com.sarxos.medusa.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.math.MA;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.history.BossaProvider;

public class Test extends JFrame {

	public static void main(String[] args) throws ProviderException {
		
		BossaProvider bp = new BossaProvider();
		List<Quote> quotes = bp.getAllQuotes(Symbol.KGH);
		
		System.out.println(quotes.size());

		Symbol s = Symbol.KGH;
		
		OHLCSeries series = new OHLCSeries(s);
		for (int i = quotes.size() - 300; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			series.add(new Day(q.getDate()), q.getOpen(), q.getHigh(), q.getLow(), q.getClose());
		}
		
//		Quote q = quotes.get(quotes.size() - 1);
//		
//		MA.ema(q, 3);
		
		
		OHLCSeriesCollection seriescol = new OHLCSeriesCollection();
		seriescol.addSeries(series);

		DateAxis time = new DateAxis("Time");
        NumberAxis values = new NumberAxis("OHLC");
        
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(time);
		
        XYPlot ohlc = new XYPlot(seriescol, time, values, null);
        HighLowRenderer crend = new HighLowRenderer();
        ohlc.setRenderer(crend);
        plot.add(ohlc);
        
        /// 
        double[] ema = new double[quotes.size()];
        double[] sma = new double[quotes.size()];
        double[] jma = new double[quotes.size()];
        
        TimeSeries s1 = new TimeSeries("EMA for " + s);
		for (int i = quotes.size() - 300; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			ema[i] = MA.jma(q, 10, 0);
			s1.add(new Day(q.getDate()), ema[i]);
		}

        TimeSeries s2 = new TimeSeries("SMA for " + s);
		for (int i = quotes.size() - 300; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			sma[i] = MA.jma(q, 20, 0);
			s2.add(new Day(q.getDate()), sma[i]);
		}
		
		for (int i = 1; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			if (ema[i] < sma[i] && ema[i - 1] > sma[i - 1]) {
				CircleDrawer cd = new CircleDrawer(Color.red, new BasicStroke(1.0f), null);
				double x = new Day(q.getDate()).getFirstMillisecond();
				XYDrawableAnnotation annotation = new XYDrawableAnnotation(x, q.getOpen(), 10, 10, cd);
				ohlc.addAnnotation(annotation);
			} else if (ema[i] > sma[i] && ema[i - 1] < sma[i - 1]) {
				CircleDrawer cd = new CircleDrawer(Color.green, new BasicStroke(1.0f), null);
				double x = new Day(q.getDate()).getFirstMillisecond();
				XYDrawableAnnotation annotation = new XYDrawableAnnotation(x, q.getOpen(), 10, 10, cd);
				ohlc.addAnnotation(annotation);
			}
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();	
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		
        XYSplineRenderer lrend = new XYSplineRenderer();
        lrend.setSeriesShapesVisible(0, false);
        lrend.setSeriesShapesVisible(1, false);
        XYPlot jmaplot = new XYPlot(dataset, time, values, lrend);
        jmaplot.setRenderer(lrend);
        plot.add(jmaplot);
        
		JFreeChart chart = new JFreeChart("KGHM", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        ChartFactory.getChartTheme().apply(chart);
		
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 350));

        Test t = new Test();
        t.setContentPane(chartPanel);
        t.pack();
        t.setVisible(true);
        t.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
