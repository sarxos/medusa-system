package com.sarxos.medusa.math;

import com.sarxos.medusa.market.Quote;

/**
 * <b>C</b>hange <b>M</b>omentum <b>O</b>scillator
 * 
 * @author Bartosz Firyn (SarXos)
 * @see http://www.positiveterritory.com/do/tw/chanmo11.htm
 */
public class CMO {

	/**
	 * Calculate <b>C</b>hange <b>M</b>omentum <b>O</b>scillator.
	 * 
	 * @param q - quote to calculate CMO for
	 * @param P - time period
	 * @return Will return double value
	 */
	public static double cmo(Quote q, int P) {
		
		double[] p = SX.detach(q, P + 1)[3];
		double d = 0;
		double[] cmo1 = new double[P];
		double[] cmo2 = new double[P];
		
		for (int i = 1; i < p.length; i++) {
			d = p[i] - p[i - 1];
			cmo1[i - 1] = d >= 0 ? d : 0;
			cmo2[i - 1] = d < 0 ? -d : 0; 
		}
		
		double s1 = M.sum(cmo1);
		double s2 = M.sum(cmo2);
		
		return 100 * (s1 - s2) / (s1 + s2);
	}
	
	/**
	 * Calculate <b>C</b>hange <b>M</b>omentum <b>O</b>scillator.
	 * 
	 * @param data - quotes vector to calculate CMO for
	 * @param P - time period
	 * @return Will return array of double values
	 */	
	public static double[] cmo(Quote[] data, int P) {
		double[] cmo = new double[P];
		for (int i = 0; i < data.length; i++) {
			cmo[i] = cmo(data[i], P);
		}
		return cmo;
	}
}
