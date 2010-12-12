package com.sarxos.gpwnotifier.generator;

import java.util.LinkedList;
import java.util.List;

import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;
import com.sarxos.gpwnotifier.entities.SignalType;
import com.sarxos.gpwnotifier.math.MA;


/**
 * <p>
 * This system is <b>NOT</b> good for chaotic trends (e.g. TVN), but it is
 * very good for stable market (e.g. BRE, KGHM). 
 * </p>
 * 
 * <p>
 * D(n) = EMA(Q(n), A) - SMA(Q(n), B)<br>
 * G(n) = d(EMA(Q(n), C))<br><br>
 * 
 * B &lt;=&gt; D(n) &gt; 0 && G(n) &gt; 0<br>
 * D &lt;=&gt; D(n) &gt; 0 && G(n) &lt; 0<br>
 * S &lt;=&gt; D(n) &lt; 0
 * </p>
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MASD implements SignalGenerator<Quote> {

	private int A = 5;
	private int B = 15;
	private int C = 30;
	
	public MASD(int A, int B, int C) {
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
			
			//System.out.print(q.getDateString() + " ");
			
			delta = ema[i] - sma[i];
			if (delta > 0 && !delay) {
				if (emad[i] > 0) { 
					if (signal != SignalType.BUY) {
						signal = SignalType.BUY;
						signals.add(new Signal(q.getDate(), signal, q, emad[i]));
						delay = false;
						//System.out.print("sb ");
					}
				} else {
					delay = true;
					//System.out.print("dd ");
				}
			} else if (delta < 0) {
				if (signal != SignalType.SELL) {
					signal = SignalType.SELL;
					signals.add(new Signal(q.getDate(), signal, q, emad[i]));
					delay = false;
					//System.out.print("ss ");
				}
			}
			
			if (delay) {
				System.out.print("d ");
				if (emad[i] > 0) {
					if (signal != SignalType.BUY) {
						signal = SignalType.BUY;
						signals.add(new Signal(q.getDate(), signal, q, emad[i]));
						delay = false;
						//System.out.print("db ");
					}
				}
			}
			//System.out.println();
		}
		
		return signals;
	}
}
