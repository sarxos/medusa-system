package com.sarxos.medusa.gui.drawer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.net.URI;

import org.jfree.ui.Drawable;

import com.sarxos.swing.SVGRenderedIcon;


/**
 * Price pointer.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class PriceDrawer implements Drawable {

	private SVGRenderedIcon icon = null;

	/**
	 * Creates a new instance.
	 * 
	 * @param outlinePaint the outline paint.
	 * @param outlineStroke the outline stroke.
	 * @param fillPaint the fill paint.
	 */
	public PriceDrawer(URI uri) {
		this.icon = new SVGRenderedIcon(uri);
	}

	/**
	 * Draws the circle.
	 * 
	 * @param g2 the graphics device.
	 * @param area the area in which to draw.
	 */
	@Override
	public void draw(Graphics2D g2, Rectangle2D area) {

		double x = area.getX();
		double y = area.getY();
		double w = area.getWidth();
		double h = area.getHeight();

		Dimension dim = new Dimension((int) w, (int) h);

		icon.setPreferredSize(dim);
		icon.paintIcon(null, g2, (int) x, (int) y);
	}
}