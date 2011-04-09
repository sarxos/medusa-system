package com.sarxos.medusa.gui.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;


/**
 * Enhanced high-low renderer.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class OHLCRenderer extends HighLowRenderer {

	private static final long serialVersionUID = 1L;

	private void setOCPaint(OHLCDataset hld, Graphics2D g2, int series, int item) {

		double o = hld.getOpenValue(series, item);
		double c = hld.getCloseValue(series, item);

		if (c >= o) {
			g2.setColor(Color.GREEN);
		} else {
			g2.setColor(Color.RED);
		}
	}

	@Override
	public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {

		double x = dataset.getXValue(series, item);
		if (!domainAxis.getRange().contains(x)) {
			return; // the x value is not within the axis range
		}
		double xx = domainAxis.valueToJava2D(x, dataArea,
					plot.getDomainAxisEdge());

		// setup for collecting optional entity info...
		Shape entityArea = null;
		EntityCollection entities = null;
		if (info != null) {
			entities = info.getOwner().getEntityCollection();
		}

		PlotOrientation orientation = plot.getOrientation();
		RectangleEdge location = plot.getRangeAxisEdge();

		Paint itemPaint = getItemPaint(series, item);
		Stroke itemStroke = getItemStroke(series, item);
		g2.setPaint(itemPaint);
		g2.setStroke(itemStroke);

		if (dataset instanceof OHLCDataset) {

			OHLCDataset hld = (OHLCDataset) dataset;

			setOCPaint(hld, g2, series, item);

			double yHigh = hld.getHighValue(series, item);
			double yLow = hld.getLowValue(series, item);
			if (!Double.isNaN(yHigh) && !Double.isNaN(yLow)) {
				double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, location);
				double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, location);
				if (orientation == PlotOrientation.HORIZONTAL) {
					g2.draw(new Line2D.Double(yyLow, xx, yyHigh, xx));
					entityArea = new Rectangle2D.Double(
						Math.min(yyLow, yyHigh),
						xx - 1.0,
						Math.abs(yyHigh - yyLow),
						2.0);
				} else if (orientation == PlotOrientation.VERTICAL) {
					g2.draw(new Line2D.Double(xx, yyLow, xx, yyHigh));
					entityArea = new Rectangle2D.Double(
						xx - 1.0,
						Math.min(yyLow, yyHigh),
						2.0,
						Math.abs(yyHigh - yyLow));
				}
			}

			double delta = getTickLength();
			if (domainAxis.isInverted()) {
				delta = -delta;
			}

			if (getDrawOpenTicks()) {
				double yOpen = hld.getOpenValue(series, item);
				if (!Double.isNaN(yOpen)) {
					double yyOpen = rangeAxis.valueToJava2D(yOpen, dataArea, location);
					if (this.getOpenTickPaint() != null) {
						g2.setPaint(this.getOpenTickPaint());
					} else {
						g2.setPaint(itemPaint);
					}
					setOCPaint(hld, g2, series, item);
					if (orientation == PlotOrientation.HORIZONTAL) {
						g2.draw(new Line2D.Double(yyOpen, xx + delta, yyOpen, xx));
					} else if (orientation == PlotOrientation.VERTICAL) {
						g2.draw(new Line2D.Double(xx - delta, yyOpen, xx, yyOpen));
					}
				}
			}

			if (getDrawCloseTicks()) {
				double yClose = hld.getCloseValue(series, item);
				if (!Double.isNaN(yClose)) {
					double yyClose = rangeAxis.valueToJava2D(yClose, dataArea, location);
					if (this.getCloseTickPaint() != null) {
						g2.setPaint(this.getCloseTickPaint());
					} else {
						g2.setPaint(itemPaint);
					}
					setOCPaint(hld, g2, series, item);
					if (orientation == PlotOrientation.HORIZONTAL) {
						g2.draw(new Line2D.Double(yyClose, xx, yyClose,
									xx - delta));
					} else if (orientation == PlotOrientation.VERTICAL) {
						g2.draw(new Line2D.Double(xx, yyClose, xx + delta,
									yyClose));
					}
				}
			}

		} else {
			// not a HighLowDataset, so just draw a line connecting this point
			// with the previous point...
			if (item > 0) {
				double x0 = dataset.getXValue(series, item - 1);
				double y0 = dataset.getYValue(series, item - 1);
				double y = dataset.getYValue(series, item);
				if (Double.isNaN(x0) || Double.isNaN(y0) || Double.isNaN(y)) {
					return;
				}
				double xx0 = domainAxis.valueToJava2D(x0, dataArea,
							plot.getDomainAxisEdge());
				double yy0 = rangeAxis.valueToJava2D(y0, dataArea, location);
				double yy = rangeAxis.valueToJava2D(y, dataArea, location);
				if (orientation == PlotOrientation.HORIZONTAL) {
					g2.draw(new Line2D.Double(yy0, xx0, yy, xx));
				} else if (orientation == PlotOrientation.VERTICAL) {
					g2.draw(new Line2D.Double(xx0, yy0, xx, yy));
				}
			}
		}

		if (entities != null) {
			addEntity(entities, entityArea, dataset, series, item, 0.0, 0.0);
		}
	}

}
