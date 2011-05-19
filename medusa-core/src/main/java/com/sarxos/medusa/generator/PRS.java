package com.sarxos.medusa.generator;

import static com.sarxos.medusa.market.SignalType.BUY;
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
import com.sarxos.medusa.math.ATR;
import com.sarxos.medusa.math.SX;


/**
 * <b>PRS</b> = <b>P</b>awel <b>R</b>ejczak based <b>S</b>ystem for WIG20
 * futures.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see http://www.eurobankier.pl/futures/index.html
 */
public class PRS extends AbstractGenerator<Quote> {

	private double P = 0;

	public PRS(double P) {
		this.P = P;
	}

	@Override
	public List<Signal> generate(Quote[] data, int N) {

		List<Signal> signals = new LinkedList<Signal>();

		Quote q = null;
		SignalType signal = null;
		double a = 0;
		double delta = 0;

		int L = data.length;

		Quote[] futures = new Quote[N];

		System.arraycopy(data, L - N - 1, futures, 0, N);

		double atr[] = ATR.atr2(futures);
		double qdiffs[] = SX.qdiff(q, N);

		for (int i = 0; i < atr.length; i++) {
			a = atr[i] + P;
			delta = qdiffs[i];

			if (delta > 0 && delta > a) {
				if (signal != SignalType.BUY) {
					signal = SignalType.BUY;
					signals.add(new Signal(futures[i].getDate(), signal, q, a));
				}
			} else if (delta < 0 && delta < a) {
				if (signal != SignalType.SELL) {
					signal = SignalType.SELL;
					signals.add(new Signal(futures[i].getDate(), signal, q, a));
				}
			}
		}

		return signals;
	}

	@Override
	public Signal generate(Quote f) {

		double a = ATR.atr2(f) * P;
		double d = SX.qdiff(f);

		if (d > 0 && d > a) {
			return new Signal(f, BUY);
		} else if (d < 0 && d < a) {
			return new Signal(f, SELL);
		}

		return new Signal(f, WAIT);
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("P", Double.toString(P));
		return params;
	}

	@Override
	public void setParameters(Map<String, String> params) {
		P = Double.parseDouble(params.get("P").toString());
	}
}

// for (int i = data.length - N - 1; i < data.length; i++) {
//
// q = data[i];
//
// // calculate last 2-days ATR
// a = ATR.atr2(data) * p;
//
// // delta for current day
// delta = q.getClose() - q.getOpen();
//
// if (delta > 0 && delta > a) {
// if (signal != SignalType.BUY) {
// signal = SignalType.BUY;
// signals.add(new Signal(q.getDate(), signal, q, a));
// }
// } else if (delta < 0 && delta < a) {
// if (signal != SignalType.SELL) {
// signal = SignalType.SELL;
// signals.add(new Signal(q.getDate(), signal, q, a));
// }
// }
// }