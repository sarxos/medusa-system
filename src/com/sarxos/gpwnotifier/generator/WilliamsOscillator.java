package com.sarxos.gpwnotifier.generator;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;
import com.sarxos.gpwnotifier.entities.SignalType;



/**
 * Williams %R, or just %R, is a technical analysis oscillator showing the
 * current closing price in relation to the high and low of the past N days
 * (for a given N). It was developed by a publisher and promoter of trading
 * materials, Larry Williams. Its purpose is to tell whether a stock or
 * commodity market is trading near the high or the low, or somewhere in
 * between, of its recent trading range.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see 
 */
public class WilliamsOscillator implements SignalGenerator<Quote> {

	private int n = 0;
	
	private int low = 0;
	
	private int high = 0;
	
	/**
	 * 
	 * @param n - days interval
	 * @param low - buy line value (top)
	 * @param high - sell line value (bottom)
	 */
	public WilliamsOscillator(int n, int low, int high) {
		super();
		this.n = n;
		this.low = low;
		this.high = high;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	@Override
	public List<Signal> generate(Quote[] data, int range) {
	
		int ticks = data.length - range;
		if (ticks < 0) {
			throw new IllegalArgumentException("Data length and range delta cannot be negative");
		}
		
		List<Signal> signals = new LinkedList<Signal>();
		
		int i, k, p;  // position
		double min;   // min price in interval
		double max;   // max price in interval
		
		double tmp;
		double r = 0;
		
		SignalType type = null;
		
		for (i = 0; i <= range; i++) { 
			
			p = data.length - range + i - 1;
			min = data[p].getLow();
			max = data[p].getHigh();
			
			// find min and max prices within interval 
			for (k = n; k > 0; k--) {
				tmp = data[p - k].getLow(); 
				if (tmp < min) {
					min = tmp;
				}
				tmp = data[p - k].getHigh(); 
				if (tmp > max) {
					max = tmp;
				}
			}
			
			// calculate %R
			tmp = 100 + (data[p].getClose() - max) * 100 / (max - min);
			
			// check whether %R gain SELL or BUY signal 
			if (i > 0) {
				
				type = null;
				if (r > high && tmp < high) {
					type = SignalType.SELL;
				} else if (r < low && tmp > low) {
					type = SignalType.BUY;
				}
				
				if (type != null) { 
					Date date = data[p].getDate();
					Signal signal = new Signal(date, type); 
					signals.add(signal);
				}
			}
			
			r = tmp; 
		}
		
		return signals;
	}
}
