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
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.history.BossaProvider;


public class Test extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws ProviderException {

		BossaProvider bp = new BossaProvider();
		List<Quote> quotes = bp.getAllQuotes(Symbol.KGH);

		System.out.println(quotes.size());

		Symbol s = Symbol.BRE;

		OHLCSeries series = new OHLCSeries(s);
		for (int i = quotes.size() - 300; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			series.add(new Day(q.getDate()), q.getOpen(), q.getHigh(), q.getLow(), q.getClose());
		}

		// Quote q = quotes.get(quotes.size() - 1);
		//
		// MA.ema(q, 3);

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

		TimeSeries s1 = new TimeSeries("EMA");
		TimeSeries s2 = new TimeSeries("SMA");

		TimeSeriesCollection dataset2 = new TimeSeriesCollection();

		dataset2.addSeries(s2);
		dataset2.addSeries(s1);

		ohlc.setDataset(1, dataset1);
		ohlc.setDataset(0, dataset2);

		AbstractGenerator<Quote> siggen = new MAVD2(10, 20, 20);
		siggen.setOutputting(true);

		for (int i = quotes.size() - 300; i < quotes.size(); i++) {

			Quote q = quotes.get(i);
			Signal signal = siggen.generate(q);

			double x = 0;

			switch (signal.getType()) {
				case BUY:
					x = new Day(q.getDate()).getFirstMillisecond();
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getOpen(), 10, 10, new BDrawer()));
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getLow() - 2, 8, 15, new UDrawer()));
					break;
				case SELL:
					x = new Day(q.getDate()).getFirstMillisecond();
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getOpen(), 10, 10, new SDrawer()));
					ohlc.addAnnotation(new XYDrawableAnnotation(x, q.getHigh() + 2, 8, 15, new DDrawer()));
					break;
			}

			List<Value> vals = signal.getValues();
			for (Value v : vals) {
				TimeSeries ts = dataset2.getSeries(v.getName());
				ts.add(new Day(q.getDate()), v.getValue());
			}
		}

		XYSplineRenderer lrend = new XYSplineRenderer();
		lrend.setSeriesShapesVisible(0, false);
		lrend.setSeriesShapesVisible(1, false);

		// XYPlot jmaplot = new XYPlot(dataset, time, values, lrend);
		// jmaplot.setRenderer(lrend);

		ohlc.setRenderer(0, lrend);

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
