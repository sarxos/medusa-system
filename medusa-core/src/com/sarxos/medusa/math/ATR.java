package com.sarxos.medusa.math;

import com.sarxos.medusa.market.Quote;


/**
 * Average True Range.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ATR {

	/**
	 * <p>
	 * Developed by J. Welles Wilder, the Average True Range (ATR) is an
	 * indicator that measures volatility. As with most of his indicators,
	 * Wilder designed ATR with commodities and daily prices in mind.
	 * Commodities are frequently more volatile than stocks. They were are often
	 * subject to gaps and limit moves, which occur when a commodity opens up or
	 * down its maximum allowed move for the session. A volatility formula based
	 * only on the high-low range would fail to capture volatility from gap or
	 * limit moves. Wilder created Average True Range to capture this "missing"
	 * volatility. It is important to remember that ATR does not provide an
	 * indication of price direction, just volatility.
	 * </p>
	 * 
	 * <p>
	 * First ATR:
	 * </p>
	 * 
	 * <p>
	 * TRa(n) = max(high(Q(n)), close(Q(n - 1)))<br>
	 * TRb(n) = max(low(Q(n)), close(Q(n - 1)))<br>
	 * TR(n) = TRa(n) - TRb(n)<br>
	 * ATR(n) = sum(TR, 0 .. n) / n;
	 * </p>
	 * 
	 * <p>
	 * Recursive equation (e.g. ISPAG use it):
	 * </p>
	 * 
	 * <p>
	 * ATR(n) = (ATR(n - 1) * (P - 1) + TR(n)) / P
	 * </p>
	 * 
	 * @param data - Quotes input data
	 * @param P - ATR period
	 * @return ATR vector
	 */
	public static double[] atr(Quote[] data, int P) {

		if (P == 0) {
			throw new IllegalArgumentException("ATR period must be positive");
		}

		double tr = 0; // TR
		double tra = 0; // TRa
		double trb = 0; // TRb
		double oatr = 0; // old ATR

		int i = 0;
		int j = 0;

		Quote q = null;
		Quote p = null;

		double[] atr = new double[data.length];

		q = data[0];
		do {
			p = q.prev();
			tra = Math.max(q.getHigh(), p.getClose());
			trb = Math.min(q.getLow(), p.getClose());
			tr += tra - trb;
			q = p;
		} while (j++ < P - 1);
		atr[0] = tr / P;

		for (i = 1; i < data.length; i++) {
			oatr = atr[i - 1];
			q = data[i];
			p = q.prev();
			tra = Math.max(q.getHigh(), p.getClose());
			trb = Math.min(q.getLow(), p.getClose());
			tr = tra - trb;
			atr[i] = (oatr * ((double) P - 1) + tr) / P;
		}
		return atr;
	}

	public static double atr(Quote q, int P) {
		Quote[] quotes = new Quote[] { q };
		return atr(quotes, P)[0];
	}

	/**
	 * Generate ATR vector for 14-days period. 14-days period was originally
	 * used by ATR developer J. Welles Wilder.
	 * 
	 * @param data
	 * @return
	 */
	public static double[] atr14(Quote[] data) {
		return atr(data, 14);
	}

	/**
	 * Generate ATR vector for 2-days period. 2-days period is used in Rejczak's
	 * system.
	 * 
	 * @param data
	 * @return ATR vector
	 */
	public static double[] atr2(Quote[] data) {
		return atr(data, 2);
	}

	/**
	 * Generate ATR vector for 2-days period. 2-days period is used in Rejczak's
	 * system.
	 * 
	 * @param data
	 * @return ATR vector
	 */
	public static double atr2(Quote data) {
		return atr(new Quote[] { data }, 2)[0];
	}

	/**
	 * Return true range.
	 * 
	 * @param data
	 * @return True Ranges array
	 */
	public static double[] tr(Quote[] data) {

		int i = 0;
		int N = data.length;

		Quote q = null;
		Quote p = null;

		double tr_a = 0;
		double tr_b = 0;

		double[] tr = new double[N];

		for (i = 0; i < N; i++) {
			q = data[i];
			p = q.prev();
			tr_a = Math.max(q.getHigh(), p.getClose());
			tr_b = Math.min(q.getLow(), p.getClose());
			tr[i] = tr_a - tr_b;
		}

		return tr;
	}

	/**
	 * <p>
	 * TRa(n) = max(high(Q(n)), close(Q(n - 1)))<br>
	 * TRb(n) = max(low(Q(n)), close(Q(n - 1)))<br>
	 * TR(n) = TRa(n) - TRb(n)<br>
	 * </p>
	 * 
	 * @param q
	 * @return True Range
	 */
	public static double tr(Quote q) {
		Quote p = q.prev();

		double tr_a = Math.max(q.getHigh(), p.getClose());
		double tr_b = Math.min(q.getLow(), p.getClose());

		return tr_a - tr_b;
	}

	public static double[] tr(Quote q, int N) {

		double[] tr = new double[N];

		int i = 0;
		do {
			tr[i] = tr(q);
			q = q.prev();
		} while (i++ < N - 1);

		return tr;
	}
}
