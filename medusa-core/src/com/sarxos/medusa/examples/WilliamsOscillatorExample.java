package com.sarxos.medusa.examples;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;
import com.sarxos.gpwnotifier.entities.SignalType;
import com.sarxos.gpwnotifier.generator.WilliamsOscillator;

public class WilliamsOscillatorExample {

	public static void main(String[] args) throws QuotesReaderException {
		
		// for 10 days window, low 20, high 80
		SignalGenerator<Quote> williams = new WilliamsOscillator(10, 20, 80);
		QuotesReader<Quote> reader = new StoqReader<Quote>(Quote.class);
		
		List<Quote> data = reader.read(new File("data/kgh_d.csv").toURI());
		List<Signal> signals = williams.generate(data.toArray(new Quote[data.size()]), 100);
		
		Signal signal = null;
		Date date = null;
		SignalType type = null;
		
		Iterator<Signal> iterator = signals.iterator();
		
		while (iterator.hasNext()) {
			signal = iterator.next();
			date = signal.getDate();
			type = signal.getType();
			
			System.out.println(Quote.DATE_FORMAT.format(date) + " " + type);
		}
	}
}
