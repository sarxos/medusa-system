package com.sarxos.medusa.market;

import java.util.List;
import java.util.Map;


public interface SignalGenerator<T extends Quote> {

	public List<Signal> generate(T[] data, int range);

	public Signal generate(T data);
	
	public Map<String, Object> getParameters();
	
	public void setParameters(Map<String, Object> params);
}
