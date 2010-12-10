package com.sarxos.gpwnotifier.generator;

import java.util.List;

import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;


/**
 * TODO impl
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class EMA implements SignalGenerator<Quote> {

	@Override
	public List<Signal> generate(Quote[] data, int range) {
		return null;
	}
	
	

}
