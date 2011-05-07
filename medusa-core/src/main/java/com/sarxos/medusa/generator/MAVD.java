package com.sarxos.medusa.generator;

import static com.sarxos.medusa.market.SignalType.BUY;
import static com.sarxos.medusa.market.SignalType.DELAY;
import static com.sarxos.medusa.market.SignalType.SELL;
import static com.sarxos.medusa.market.SignalType.WAIT;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sarxos.medusa.market.AbstractGenerator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Signal;
import com.sarxos.medusa.market.Signal.Value;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.math.MA;


/**
 * <b>MAVD</b> = <b>M</b>oving <b>A</b>verages <b>V</b>ariation &
 * <b>D</b>erivative.
 * 
 * <p>
 * This system is <b>NOT</b> good for chaotic trends (e.g. TVN), but it is very
 * good for stable market (e.g. BRE, KGHM).
 * </p>
 * 
 * <p>
 * D(n) = EMA(Q(n), A) - SMA(Q(n), B)<br>
 * G(n) = d(EMA(Q(n), C))<br>
 * <br>
 * 
 * B &lt;=&gt; D(n) &gt; 0 && G(n) &gt; 0<br>
 * D &lt;=&gt; D(n) &gt; 0 && G(n) &lt; 0<br>
 * S &lt;=&gt; D(n) &lt; 0
 * </p>
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MAVD extends AbstractGenerator<Quote> {

	private int A = 5;

	private int B = 15;

	private int C = 30;

	public MAVD() {
	}

	public MAVD(int A, int B, int C) {
		init(A, B, C);
	}

	public void init(int A, int B, int C) {
		if (A < 2) {
			throw new IllegalArgumentException("EMA period canot be less then 2");
		}
		if (B < 2) {
			throw new IllegalArgumentException("SMA period canot be less then 2");
		}
		if (C < 2) {
			throw new IllegalArgumentException("EMAD period canot be less then 2");
		}

		this.A = A;
		this.B = B;
		this.C = C;
	}

	@Override
	public List<Signal> generate(Quote[] data, int R) {

		List<Signal> signals = new LinkedList<Signal>();

		Quote[] quotes = new Quote[R];

		System.arraycopy(data, data.length - R - 1, quotes, 0, R);

		SignalType signal = null;
		Quote q = null;

		double[] ema = MA.ema(quotes, A);
		double[] sma = MA.sma(quotes, B);
		double[] emad = MA.emad(quotes, C);

		double delta = 0;

		boolean delay = false;

		for (int i = 0; i < R; i++) {
			q = quotes[i];
			delta = ema[i] - sma[i];
			if (delta > 0 && !delay) {
				if (emad[i] > 0) {
					if (signal != BUY) {
						signal = BUY;
						signals.add(new Signal(q.getDate(), signal, q, emad[i]));
						delay = false;
					}
				} else {
					delay = true;
				}
			} else if (delta < 0) {
				if (signal != SELL) {
					signal = SELL;
					signals.add(new Signal(q.getDate(), signal, q, emad[i]));
					delay = false;
				}
			}

			if (delay) {
				if (emad[i] > 0) {
					if (signal != BUY) {
						signal = BUY;
						signals.add(new Signal(q.getDate(), signal, q, emad[i]));
						delay = false;
					}
				}
			}
		}

		return signals;
	}

	@Override
	public Signal generate(Quote q) {

		// do not treat trading price seriously - close price really matters,
		// take it from previous day
		q = q.prev();

		// calculate necessary coefficients
		double e = MA.ema(q, A);
		double s = MA.sma(q, B);
		double d = MA.emad(q, C);

		// initially just wait
		Signal signal = new Signal(q, WAIT);

		if (e - s > 0) {
			if (d > 0 /* && e1 - s1 < 0 */) {
				signal = new Signal(q, BUY);
			} else {
				signal = new Signal(q, DELAY);
			}
		} else {
			signal = new Signal(q, SELL);
		}

		if (isOutputting()) {
			signal.addValue(new Value("EMA", e));
			signal.addValue(new Value("SMA", s));
		}

		return signal;
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("A", Integer.toString(A));
		params.put("B", Integer.toString(B));
		params.put("C", Integer.toString(C));
		return params;
	}

	@Override
	public void setParameters(Map<String, String> params) {
		int A = Integer.parseInt(params.get("A").toString());
		int B = Integer.parseInt(params.get("B").toString());
		int C = Integer.parseInt(params.get("C").toString());
		init(A, B, C);
	}
}
