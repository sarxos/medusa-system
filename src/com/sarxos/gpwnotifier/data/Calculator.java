package com.sarxos.gpwnotifier.data;

import java.util.List;

import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;



public class Calculator {

	private Quote[] data = null;
	
	private int range = -1;
	
	
	public Calculator(List<Quote> data, int range) {
		setData(data);
		setRange(range);
	}
	
	public void setData(List<Quote> data) {
		if (data == null) {
			throw new IllegalArgumentException("Data cannot be null");
		}
		this.data = data.toArray(new Quote[data.size()]);
	}
	
	public void setRange(int range) {
		if (range <= 0) {
			throw new IllegalArgumentException("Range cannot be zero or negative");
		}
		this.range = range;
	}
	
	public List<Signal> calculate(SignalGenerator<Quote> generator) {
		return generator.generate((Quote[])data, range);
	}
}
