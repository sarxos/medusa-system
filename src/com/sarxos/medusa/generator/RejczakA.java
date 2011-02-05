package com.sarxos.medusa.generator;

import java.util.LinkedList;
import java.util.List;

import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;
import com.sarxos.gpwnotifier.entities.SignalType;


/**
 * Pawel Rejczak based system for WIG20 futures. 
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class RejczakA implements SignalGenerator<Quote> {

	double p = 0;
	
	public RejczakA(double p) {
		this.p = p;
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
			
			// calculate last 2-days ATR
			a = atr2(i - 1, data) * p;
			
			// delta for current day
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

	
	// TODO change to the ATR.atr(..)
	protected double atr2(int n, Quote[] data) {
		
		// Rejczak system base always on last 2 days
		int k = 2;
		
		double tr = 0;
		double a = 0;
		double b = 0;
		Quote x = null;
		Quote y = null;
		
		for (int i = 0; i < k; i++) {
			x = data[n - i];
			y = data[n - i - 1];
			a = Math.max(x.getHigh(), y.getClose());
			b = Math.min(x.getLow(), y.getClose());
			tr += a - b;
		}
		
		double atr = tr / k;
		
		return atr;
	}
}
