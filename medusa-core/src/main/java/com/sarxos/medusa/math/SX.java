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

	/**
	 * Detach quotes into the separated double arrays. Output value is as
	 * follow:<br>
	 * <br>
	 * 
	 * <pre>
	 * double[][] { 
	 *     double[] open,
	 *     double[] high,
	 *     double[] low,
	 *     double[] close
	 * }
	 * </pre>
	 * 
	 * @param q - last quote to detach
	 * @param N - how many quotes shall be detached
	 * @return double[][]
	 */
	public static double[][] detach(Quote q, int N) {

		double[] open = new double[N];
		double[] high = new double[N];
		double[] low = new double[N];
		double[] close = new double[N];

		for (int i = N - 1; i >= 0; i--) {

			open[i] = q.getOpen();
			high[i] = q.getHigh();
			low[i] = q.getLow();
			close[i] = q.getClose();

			q = q.prev();
		}

		return new double[][] { open, high, low, close };
	}

	/**
	 * Create list of quotes for given N-days period. Take last quote as the
	 * input argument.
	 * 
	 * @param q - last quote
	 * @param N - time interval (days)
	 * @return Return new N-elements array of quotes
	 */
	public static Quote[] list(Quote q, int N) {
		Quote[] quotes = new Quote[N];
		for (int i = N - 1; i >= 0; i--) {
			quotes[i] = q;
			q = q.prev();
		}
		return quotes;
	}

	/**
	 * Reverse input vector.
	 * 
	 * @param v - vector to be
	 * @return
	 */
	public static double[] reverse(double[] v) {
		int n = v.length;
		double[] r = new double[n];
		for (int i = 0; i < n; i++) {
			r[n - i - 1] = v[i];
		}
		return r;
	}
}
