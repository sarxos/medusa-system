package com.sarxos.medusa.gui;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
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
		
		OHLCSeries series = new OHLCSeries(Symbol.KGH);
		for (int i = quotes.size() - 300; i < quotes.size(); i++) {
			Quote q = quotes.get(i);
			series.add(new Day(q.getDate()), q.getOpen(), q.getHigh(), q.getLow(), q.getClose());
		}
		
		Quote q = quotes.get(quotes.size() - 1);
		
		MA.ema(q, 3);
		
		
		OHLCSeriesCollection seriescol = new OHLCSeriesCollection();
		seriescol.addSeries(series);

		DateAxis time = new DateAxis("Time");
        NumberAxis values = new NumberAxis("OHLC");
        
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(time);
		
        XYPlot ohlc = new XYPlot(seriescol, time, values, null);
        HighLowRenderer crend = new HighLowRenderer();
        ohlc.setRenderer(crend);
        plot.add(ohlc);
        
        
        
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
