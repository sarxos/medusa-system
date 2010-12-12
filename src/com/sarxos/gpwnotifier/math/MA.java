package com.sarxos.gpwnotifier.math;

import com.sarxos.gpwnotifier.entities.Quote;


// TODO change to iterative equations
public class MA {

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
	
	public static double[] ema(Quote[] data, int N) {
		
		if (N <= 0) {
			throw new IllegalArgumentException("WMA interval must be positive");
		}
		
		double[] ema = new double[data.length];
		double em = 0;
		
		int i = 0;
		int j = 0;
		int k = 0;
		
		double a = 2 / (N + 1);
		double pow = 0;
		
		Quote q = null;
		
		for (i = data.length - 1; i >= 0; i--) {
			em = 0;
			j = 0;
			k = 0;
			q = data[i];
			do {
				pow = Math.pow(1 - a, N - j);
				em += q.getClose() * pow;
				k += pow;
				q = q.prev();
			} while (j++ < N - 1);
			ema[i] = em / k;
		}
		
		return ema;
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
