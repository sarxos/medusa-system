package com.sarxos.medusa.generator;

import java.util.LinkedList;
import java.util.List;

import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;
import com.sarxos.gpwnotifier.entities.SignalType;
import com.sarxos.gpwnotifier.math.ATR;


/**
 * <p>
 * Pawel Rejczak based system for WIG20 futures.
 * </p>
 * 
 * <p>
 * D(n) = close(Q(n)) - open(Q(n))<br> 
 * ATR2(n) = ATR(Q(n), 2)<br><br>
 * B &lt;=&gt; D(n) &gt; 0 && D(n) &gt; ATR2(n)<br>  
 * S &lt;=&gt; D(n) &lt; 0 && D(n) &lt; ATR2(n)
 * </p>
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class RejczakB implements SignalGenerator<Quote> {

	double R = 0;

	
	public RejczakB(double R) {
		this.R = R;
	}
	
	@Override
	public List<Signal> generate(Quote[] data, int range) {
		
		List<Signal> signals = new LinkedList<Signal>();
		
		Quote q = null;
		SignalType signal = null;
		double a = 0;
		double delta = 0;

		for (int i = data.length - range - 1; i < data.length; i++) {
			
			q = data[i];
			a = ATR.atr(new Quote[] {q}, 2)[0] * R;
			delta = q.getClose() - q.getOpen();
			
			if (delta > 0 && delta > a) {
				if (signal != SignalType.BUY) {
					signal = SignalType.BUY;
					signals.add(new Signal(q.getDate(), signal, q, a));
				}
			} else if (delta < 0 && delta < a) {
				if (signal != SignalType.SELL) {
					signal = SignalType.SELL;
					signals.add(new Signal(q.getDate(), signal, q, a));
				}
			}
		}
		
		return signals;
	}

	public void setR(double r) {
		R = r;
	}

	public double getR() {
		return R;
	}
}
