package com.sarxos.medusa.math;

import java.util.HashMap;
import java.util.Map;

import com.sarxos.medusa.market.Quote;


public class MA {

	private static Map<Integer, Double> ema_pows = new HashMap<Integer, Double>();

	public static double sma(Quote data, int N) {
		Quote[] quotes = new Quote[] { data };
		return MA.sma(quotes, N)[0];
	}

	public static double[] sma(Quote[] data, int N) {

		if (N <= 0) {
			throw new IllegalArgumentException("SMA interval must be positive");
		}

		double[] sma = new double[data.length];
		double sm = 0;

		int i = 0;
		int j = 0;

		Quote q = null;

		for (i = data.length - 1; i >= 0; i--) {
			sm = 0;
			j = 0;
			q = data[i];
			do {
				sm += q.getClose();
				q = q.prev();
			} while (j++ < N - 1);
			sma[i] = sm / N;
		}

		return sma;
	}

	public static double wma(Quote q, int N) {
		Quote[] data = new Quote[] { q };
		return wma(data, N)[0];
	}

	public static double[] wma(Quote[] data, int N) {

		if (N <= 0) {
			throw new IllegalArgumentException("WMA interval must be positive");
		}

		double[] wma = new double[data.length];
		double wm = 0;

		int i = 0;
		int j = 0;
		int k = 0;

		Quote q = null;

		for (i = data.length - 1; i > 0; i--) {
			wm = 0;
			j = 0;
			k = 0;
			q = data[i];
			do {
				wm += q.getClose() * (N - j);
				k += N - j;
				q = q.prev();
			} while (j++ < N - 1);
			wma[i] = wm / k;
		}

		return wma;
	}

	public static double ema(Quote data, int N) {
		if (data == null) {
			throw new IllegalArgumentException("Quote to calculate EMA cannot be null");
		}
		Quote[] quotes = new Quote[] { data };
		return MA.ema(quotes, N)[0];
	}

	public static double[] ema(Quote[] data, int N) {

		if (N <= 0) {
			throw new IllegalArgumentException("EMA interval must be positive");
		}

		double[] ema = new double[data.length];
		double em = 0;

		int i = 0;
		int j = 0;
		int u = 0;

		double k = 0;
		double a = 2 / (N + 1);
		double h = 1 - a;
		double pow = 0;

		Quote q = null;
		Integer key = null;
		Double value = null;

		do {
			u = N - j;
			key = Integer.valueOf(u);
			if (ema_pows.get(key) == null) {
				value = Double.valueOf(Math.pow(h, u));
				ema_pows.put(key, value);
			}
		} while (j++ < N - 1);

		for (i = data.length - 1; i >= 0; i--) {
			em = 0;
			j = 0;
			k = 0;
			q = data[i];
			do {
				u = N - j;
				pow = ema_pows.get(u);
				if (q == null) {
					System.err.println("q is null " + j);
				}
				em += q.getClose() * pow;
				k += pow;
				q = q.prev();
			} while (j++ < N - 1);
			ema[i] = em / k;
		}

		return ema;
	}

	public static double emad(Quote data, int N) {
		Quote[] quotes = new Quote[] { data };
		return MA.emad(quotes, N)[0];
	}

	public static double smad(Quote data, int N) {
		Quote[] quotes = new Quote[] { data.prev(), data };
		double[] sma = MA.sma(quotes, N);
		return M.diff(sma)[0];
	}

	/**
	 * EMA derivative.
	 * 
	 * @param data - input quotes array (last element is the newest)
	 * @param N - period
	 * @return Will return double array
	 */
	public static double[] emad(Quote[] data, int N) {
		Quote[] quotes = new Quote[data.length + 1];

		quotes[0] = data[0].prev();

		System.arraycopy(data, 0, quotes, 1, data.length);

		double[] ema = MA.ema(quotes, N);
		double[] dema = new double[data.length];

		for (int i = 1; i < ema.length; i++) {
			dema[i - 1] = ema[i] - ema[i - 1];
		}

		return dema;
	}

	/**
	 * <b>M</b>odified <b>M</b>oving <b>A</b>varage.
	 * 
	 * @param data - input quotes array (last element is the newest)
	 * @param N - period
	 * @return Will return double array
	 */
	public static double[] mma(Quote[] data, int N) {

		double[] mma = new double[data.length];

		mma[0] = mma(data[0], N);

		for (int i = 1; i < data.length; i++) {
			mma[i] = (mma[i - 1] * (N - 1) + data[i].getClose()) / N;
		}

		return mma;
	}

	/**
	 * <b>M</b>odified <b>M</b>oving <b>A</b>varage.
	 * 
	 * @param q - input quote
	 * @param N - period
	 * @return Will return double value
	 * @see http
	 *      ://autotradingstrategy.wordpress.com/2009/11/30/modified-moving-
	 *      average
	 */
	public static double mma(Quote q, int N) {

		double[][] qs = SX.detach(q, N);
		double[] p = qs[3];

		double ps = 0;
		double ks = 0;

		for (int i = 0; i < p.length; i++) {
			ps += p[i];
			ks += (N - (2 * i + 1)) * p[i] / 2;
		}

		return ps / N - 6 * ks / (N * (N + 1));
	}

	/**
	 * Calculate <b>J</b>urik's <b>M</b>oving <b>A</b>verage for given quote
	 * point.
	 * 
	 * @param q - quote
	 * @param K - MA degree
	 * @param P - phase
	 * @return Return double value
	 */
	public static double jma(Quote q, double K, double P) {
		double[] c = SX.reverse(SX.detach(q, 80)[3]);
		double[] jma = JRK.jrk(c, K, P);
		return jma[jma.length - 1];
	}
}
