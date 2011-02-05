package com.sarxos.medusa.market;

import java.util.List;



public interface SignalGenerator<T extends Quote> {

	public List<Signal> generate(T[] data, int range);

	public Signal generate(T data);
}
