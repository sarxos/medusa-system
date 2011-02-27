package com.sarxos.medusa.math;

import java.util.List;

import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;


/**
 * Developed by Welles Wilder, the Parabolic SAR refers to a price and time
 * based trading system. Wilder called this the "Parabolic Time/Price System".
 * SAR stands for "<b>S</b>top <b>A</b>nd <b>R</b>everse", which is the actual
 * indicator used in the system. SAR trails price as the trend extends over
 * time. The indicator is below prices when prices are rising and above prices
 * when prices are falling. In this regard, the indicator stops and reverses
 * when the price trend reverses and breaks above or below the indicator.<br>
 * <br>
 * 
 * Input arguments are:
 * <ul>
 * <li>a - acceleration factor (classically 0.02),</li>
 * <li>ma - maximum incremental acceleration factor value (classically 0.2).</li>
 * </ul>
 * 
 * Acceleration factor (a) starting at e.g. 0.02, increases by 0.02 each time
 * the extreme point makes a new high. This factor can reach a maximum of e.g.
 * 0.20, no matter how long the uptrend extends.<br>
 * <br>
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class SAR {

	private static Core ta = SX.TA.getCore();

	/**
	 * Calculate SAR for N-days period. Use acceleration and max step size given
	 * as the arguments.
	 * 
	 * @param q - actual quote to start counting from
	 * @param N - time interval (days)
	 * @param a - acceleration factor (classic dimension is about 0.02)
	 * @param ma - maximum incremental acceleration factor value (SAR is more
	 *            sensible if it is higher, normally about 0.2)
	 * @return
	 */
	public static double[] sar(Quote q, int N, double a, double ma) {

		if (N < 1) {
			throw new IllegalArgumentException("SAR period cannot be null");
		}

		// last element from TA SAR output is always == 0, make input array one
		// element wider
		int P = N + 1;

		// detach quotes into the separated double arrays, one for open price,
		// one for high, one for low, etc...
		double[][] quotes = SX.detach(q, P);

		double[] high = quotes[1];
		double[] low = quotes[2];
		double[] sar = new double[P]; // SAR from TA
		double[] ret = new double[N]; // to return - take original width

		ta.sar(0, P - 1, high, low, a, ma, new MInteger(), new MInteger(), sar);

		// last element is always == 0, leave it
		System.arraycopy(sar, 0, ret, 0, N);

		return ret;
	}

	public static void main(String[] args) {

		List<Quote> quotes = QuotesRegistry.getInstance().getQuotes(Symbol.KGH);
		Quote q = quotes.get(quotes.size() - 1);

		int N = 30;

		Quote[] qus = SX.list(q, N);
		double[] close = SX.detach(q, N)[3];
		double[] sar = sar(q, N, .04, .80);

		for (int i = 0; i < sar.length; i++) {
			System.out.println(qus[i].getDateString() + " " + (double) Math.round(((sar[i] - close[i]) * 100)) / 100);
		}

	}
}
