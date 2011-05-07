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
public class JMAC extends AbstractGenerator<Quote> {

	private int A = 5;

	private int B = 15;

	private int C = 30;

	public JMAC() {
	}

	public JMAC(int A, int B, int C) {
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
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Signal generate(Quote q) {

		// calculate necessary coefficients
		double e2 = MA.jma(q, A, 0);
		double s2 = MA.jma(q, B, 0);
		//double d2 = MA.emad(q, C);

		// required to find optimal position opening moment
		// double e1 = MA.ema(q.prev(), A);
		// double s1 = MA.sma(q.prev(), B);

		// initially just wait
		Signal signal = new Signal(q, WAIT);

//		System.out.println(e2 + " " + s2);
		
		if (e2 - s2 > 0) {
			signal = new Signal(q, BUY);
		} else {
			signal = new Signal(q, SELL);
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
