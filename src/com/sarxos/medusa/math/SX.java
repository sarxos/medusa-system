package com.sarxos.medusa.math;

import com.sarxos.medusa.market.Quote;


public class SX {

	/**
	 * Return Q diff for given quote.
	 * 
	 * @param q - quote to calculate diff
	 * @return close(Q) - open(Q)
	 */
	public static double qdiff(Quote q) {
		return q.getClose() - q.getOpen();
	}

	/**
	 * Return Q diff for given N-days time period.
	 * 
	 * @param q - last quote
	 * @param N - period
	 * @return Will return N-element double vector.
	 */
	public static double[] qdiff(Quote q, int N) {

		int k = N - 1;
		double[] diffs = new double[N];

		do {
			diffs[k] = qdiff(q);
			q = q.prev();
		} while (k-- > 0);

		return diffs;
	}
}
