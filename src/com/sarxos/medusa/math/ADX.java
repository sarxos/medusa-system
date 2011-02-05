package com.sarxos.medusa.math;

import com.sarxos.medusa.market.Quote;


public class ADX {

	/**
	 * <p>
	 * Positive directional movement (+DM).
	 * </p>
	 *
	 * <p>
	 * UM(Q(n)) = high(Q(n)) - high(Q(n - 1))<br>
	 * DM(Q(n)) = low(Q(n - 1)) - low(Q(n)) <br>
	 * +DM(Q(n)) = UM(Q(n)) &gt; DM(Q(n)) && UM(Q(n)) &gt; 0 ? UM(Q(n)) : 0<br>
	 * </p> 
	 * 
	 * @return
	 */
	public static double dmp(Quote q) {
		Quote p = q.prev();
		
		double um = q.getHigh() - p.getHigh();
		double dm = p.getLow() - q.getLow();
		
		return um > dm && um > 0 ? um : 0;
	}
	
	/**
	 * @param q - Q(n)
	 * @param P - period
	 * @return Return P-days vector of +DM before Q(n) date
	 */
	public static double[] dmp(Quote q, int P) {

		double[] dmp = new double[P];
		
		Quote p = q;
		
		int i = 0;
		do {
			dmp[i] = dmp(p);
			p = p.prev();
		} while (i++ < P - 1);
		
		return dmp;
	}
	
	/**
	 * <p>
	 * Negative directional movement (-DM).
	 * </p>
	 * 
	 * <p>
	 * UM(Q(n)) = high(Q(n)) - high(Q(n - 1))<br>
	 * DM(Q(n)) = low(Q(n - 1)) - low(Q(n)) <br>
	 * -DM(Q(n)) = DM(Q(n)) &gt; UM(Q(n)) && DM(Q(n)) &gt; 0 ? DM(Q(n)) : 0<br>
	 * </p> 
	 * 
	 * @param q
	 * @return
	 */
	public static double dmn(Quote q) {
		Quote p = q.prev();
		
		double um = q.getHigh() - p.getHigh();
		double dm = p.getLow() - q.getLow();
		
		return dm > um && dm > 0 ? dm : 0;
	}

	/**
	 * @param q - Q(n)
	 * @param P - period
	 * @return Return P-days vector of -DM before Q(n) date
	 */
	public static double[] dmn(Quote q, int P) {

		double[] dmn = new double[P];
		
		Quote p = q;
		
		int i = 0;
		do {
			dmn[i] = dmn(p);
			p = p.prev();
		} while (i++ < P - 1);
		
		return dmn;
	}
	
	/**
	 * @param data
	 * @param P - MA time period 
	 * @return
	 */
	public static double[] adx(Quote[] data, int P) {

		int L = data.length;
		int N = L + P;
		
		int i = 0;
		int j = 0;

		Quote q = data[0];
		Quote p = null;
		
		Quote[] quotes = new Quote[N];
		for (i = 0 ; i < N; i++) {
			quotes[i] = new Quote();
		}

		System.arraycopy(data, 0, quotes, P, L);
		
		i = P - 1;
		do {
			p = q.prev();
			quotes[i] = p;
			q = p;
		} while (i-- > 0);
		
		double tr = 0;
		double dmp = 0;
		double dmn = 0;
		double trP = 0;
		double dmpP = 0;
		double dmnP = 0;
		double trPs = 0;
		double dmpPs = 0;
		double dmnPs = 0;
		double dip = 0;
		double din = 0;
		double di_diff = 0;
		double di_sum = 0;
		
		double[] dx = new double[N];
		double[] adx = new double[L];
		
		for (i = 0; i < N; i++) {
			q = quotes[i];

			tr = ATR.tr(q);
			dmp = ADX.dmp(q);
			dmn = ADX.dmn(q);
	
			trP = M.sum(ATR.tr(q, P));
			dmpP = M.sum(ADX.dmp(q, P));     // +DM
			dmnP = M.sum(ADX.dmn(q, P));     // -DM
			
			trPs = trP - (trP / P) + tr;
			dmpPs = dmpP - (dmpP / P) + dmp;
			dmnPs = dmnP - (dmnP / P) + dmn;
			
			dip = 100 * dmpPs / trPs;        // +DI
			din = 100 * dmnPs / trPs;        // -DI
			
			di_diff = Math.abs(dip - din);
			di_sum = dip + din;
			
			dx[i] = 100 * di_diff / di_sum;
			
			if (i >= P) {
				double dx_sum = 0;
				for (j = 0; j < P; j++) {
					dx_sum += dx[i - j];
				}
				adx[i - P] = dx_sum / P;
			}
		}
		
		return adx;
	}
	
	/**
	 * TR for 14-days period originally used by J. Welles Wilder.
	 * 
	 * @param q
	 * @return
	 */
	public static double tr14(Quote q) {
		return M.sum(ATR.tr(q, 14));
	}
}
