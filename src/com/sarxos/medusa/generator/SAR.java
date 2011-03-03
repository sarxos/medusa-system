package com.sarxos.medusa.generator;

import static com.sarxos.medusa.market.SignalType.BUY;
import static com.sarxos.medusa.market.SignalType.DELAY;
import static com.sarxos.medusa.market.SignalType.SELL;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Signal;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.math.TA;


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
 * @see com.sarxos.medusa.math.TA
 * @deprecated It's something wrong with SAR generator, <b>do not use it</b>!
 */
@Deprecated
public class SAR implements SignalGenerator<Quote> {

	/**
	 * Acceleration factor.
	 */
	private double a = 0;

	/**
	 * Maximum incremental acceleration factor value.
	 */
	private double m = 0;

	/**
	 * Create new SAR generator with acceleration and max step given as the
	 * input arguments.
	 * 
	 * @param a - acceleration (normally 0.02)
	 * @param m - maximum step (normally 0.2)
	 */
	public SAR(double a, double m) {
		this.a = a;
		this.m = m;
	}

	@Override
	public List<Signal> generate(Quote[] quotes, int R) {

		if (R > quotes.length) {
			throw new IllegalArgumentException("Range exceeds quotes array length");
		}

		int N = quotes.length;
		int S = quotes.length - 1 - R;

		List<Signal> signals = new LinkedList<Signal>();

		for (int i = S; i < N; i++) {
			signals.add(generate(quotes[i]));
		}

		return signals;
	}

	@Override
	public Signal generate(Quote q) {

		double s1 = TA.sar(q, 1, a, m)[0]; // SAR value
		double c1 = q.getClose(); // close price for given quote

		double s2 = TA.sar(q.prev(), 1, a, m)[0]; // SAR value
		double c2 = q.prev().getClose(); // close price for given quote

		boolean up = s1 < c1 && s2 < c2;
		boolean dn = s1 > c1 && s2 > c2;

		if (up) {
			return new Signal(q, BUY);
		} else if (dn) {
			return new Signal(q, SELL);
		} else {
			return new Signal(q, DELAY);
		}
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a", Double.toString(a));
		map.put("m", Double.toString(m));
		return map;
	}

	@Override
	public void setParameters(Map<String, String> params) {
		a = Double.parseDouble(params.get("a"));
		m = Double.parseDouble(params.get("m"));
	}
}
