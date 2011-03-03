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
	 * EMA diff.
	 * 
	 * @return
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
}
