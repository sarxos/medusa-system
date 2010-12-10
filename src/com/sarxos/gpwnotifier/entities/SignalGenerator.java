package com.sarxos.gpwnotifier.entities;

import java.util.List;



public interface SignalGenerator<T extends Quote> {

	public List<Signal> generate(T[] data, int range);
	
}
