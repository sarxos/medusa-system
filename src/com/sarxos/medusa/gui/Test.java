package com.sarxos.medusa.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

import com.sarxos.medusa.generator.HMAC;
import com.sarxos.medusa.generator.MAVD2;
import com.sarxos.medusa.gui.drawer.BDrawer;
import com.sarxos.medusa.gui.drawer.DDrawer;
import com.sarxos.medusa.gui.drawer.SDrawer;
import com.sarxos.medusa.gui.drawer.UDrawer;
import com.sarxos.medusa.gui.renderer.OHLCRenderer;
import com.sarxos.medusa.market.AbstractGenerator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Signal;
import com.sarxos.medusa.market.Signal.Value;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.math.ADX;
import com.sarxos.medusa.math.ATR;
import com.sarxos.medusa.math.MA;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.history.BossaProvider;


public class Test extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws ProviderException {

		Symbol s = Symbol.BRE;
		AbstractGenerator<Quote> siggen = new HMAC(20, 40, 20);
		siggen.setOutputting(true);
		
		//
		
		
		BossaProvider bp = new BossaProvider();
		List<Quote> quotes = bp.getAllQuotes(s);

		OHLCSeries series = new OHLCSeries(s);
		for (int i = quotes.size() - 300; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			series.add(new Day(q.getDate()), q.getOpen(), q.getHigh(), q.getLow(), q.getClose());
		}

		OHLCSeriesCollection dataset1 = new OHLCSeriesCollection();
		dataset1.addSeries(series);

		DateAxis time = new DateAxis("Time");
		NumberAxis values = new NumberAxis("OHLC");

		CombinedDomainXYPlot plot = new CombinedDomainXYPlot(time);

		XYPlot ohlc = new XYPlot(null, time, values, null);
		HighLowRenderer crend = new OHLCRenderer();
		ohlc.setRenderer(1, crend);

		plot.add(ohlc);

		// /

		TimeSeriesCollection dataset2 = new TimeSeriesCollection();

		ohlc.setDataset(1, dataset1);
		ohlc.setDataset(0, dataset2);

		for (int i = quotes.size() - 300; i < quotes.size(); i++) {

			Quote q = quotes.get(i);
			Signal signal = siggen.generate(q);

			double x = 0;

			switch (signal.getType()) {
				case BUY:
					x = new Day(q.getDate()).getFirstMillisecond();
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getOpen(), 10, 10, new BDrawer()));
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getLow() * 0.96, 8, 15, new UDrawer()));
					break;
				case SELL:
					x = new Day(q.getDate()).getFirstMillisecond();
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getOpen(), 10, 10, new SDrawer()));
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getHigh() * 1.04, 8, 15, new DDrawer()));
					break;
			}

			List<Value> vals = signal.getValues();
			for (Value v : vals) {
				String name = v.getName();
				TimeSeries ts = dataset2.getSeries(name);
				if (ts == null) {
					ts = new TimeSeries(name);
					dataset2.addSeries(ts);
				}
				ts.add(new Day(q.getDate()), v.getValue());
			}
		}

		XYSplineRenderer lrend = new XYSplineRenderer();
		int snum = dataset2.getSeriesCount();
		for (int i = 0; i < snum; i++) {
			lrend.setSeriesShapesVisible(i, false);
		}
		ohlc.setRenderer(0, lrend);

		// atr
		
		TimeSeriesCollection dataset3 = new TimeSeriesCollection();
		XYSplineRenderer lrend2 = new XYSplineRenderer();
		lrend2.setSeriesShapesVisible(0, false);
		DateAxis time2 = new DateAxis("Time");
		NumberAxis values2 = new NumberAxis("HMA");
		XYPlot jmaplot = new XYPlot(dataset3, time2, values2, lrend2);
		for (int i = quotes.size() - 300; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			//double atr = ATR.atr(q, 4);
			//double atr = MA.cwma(new Quote[] {q}, 14);
			double atr = MA.hma(q, 20);
			TimeSeries ts = dataset3.getSeries("HMA");
			if (ts == null) {
				ts = new TimeSeries("HMA");
				dataset3.addSeries(ts);
			}
			ts.add(new Day(q.getDate()), atr);
		}
		plot.add(jmaplot);
		
		//
		


		JFreeChart chart = new JFreeChart(s.getName(), JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		// ChartFactory.getChartTheme().apply(chart);

		ohlc.getRenderer(0).setSeriesPaint(0, Color.YELLOW);
		ohlc.getRenderer(0).setSeriesPaint(1, Color.CYAN);

		ohlc.setBackgroundPaint(Color.BLACK);
		ohlc.setRangeGridlinesVisible(true);
		ohlc.setRangeGridlinePaint(Color.DARK_GRAY);
		ohlc.setDomainGridlinesVisible(true);
		ohlc.setDomainGridlinePaint(Color.DARK_GRAY);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 350));

		Test t = new Test();
		t.setContentPane(chartPanel);
		t.pack();
		t.setVisible(true);
		t.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
