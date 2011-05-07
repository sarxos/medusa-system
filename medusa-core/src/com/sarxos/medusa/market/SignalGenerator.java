package com.sarxos.medusa.market;

import java.util.List;
import java.util.Map;


public interface SignalGenerator<T extends Quote> {

	public List<Signal> generate(T[] data, int range);

	public Signal generate(Quote quote);

	public Map<String, String> getParameters();

	public void setParameters(Map<String, String> params);
}
