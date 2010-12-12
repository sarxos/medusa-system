package com.sarxos.gpwnotifier.examples;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.entities.Index;
import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.entities.Signal;
import com.sarxos.gpwnotifier.entities.SignalGenerator;
import com.sarxos.gpwnotifier.entities.SignalType;
import com.sarxos.gpwnotifier.generator.EMA3SMA10;


public class EMA3SMA10Example {

	public static void main(String[] args) throws QuotesReaderException {

		int N = 20;

		double charge = 3;

		QuotesReader<Index> reader = new StoqReader<Index>(Index.class);
		List<Index> data = reader.read(new File("data/wig20_d.csv").toURI());
		
		SignalGenerator<Quote> ema3sma10 = new EMA3SMA10();

		List<Signal> signals = ema3sma10.generate(data.toArray(new Quote[data.size()]), N);

		Signal signal = null;
		Date date = null;
		SignalType type = null;

		Iterator<Signal> iterator = signals.iterator();

		Signal tmp = null;
		int points = 0;
		int wallet = 0;

		while (iterator.hasNext()) {
			signal = iterator.next();
			date = signal.getDate();
			type = signal.getType();

			switch (signal.getType()) {
				case SELL:
					if (wallet == 0) {
						wallet -= 1;
						points -= charge * 2 / 3;
					} else if (wallet > 0) {
						wallet -= 2;
						points -= charge;
						Quote q1 = tmp.getQuote().next();
						Quote q2 = signal.getQuote().next();
						if (q1 != null && q2 != null) {
							points += Math.abs(q2.getOpen() - q1.getOpen());
						}
					} else {
						throw new ArithmeticException("Imposiible situation. Cannot sell negative wallet.");
					}
					break;
				case BUY:
					if (wallet == 0) {
						wallet += 1;
						points -= charge * 2 / 3;
					} else if (wallet < 0) {
						wallet += 2;
						points -= charge;
						Quote q1 = tmp.getQuote().next();
						Quote q2 = signal.getQuote().next();
						if (q1 != null && q2 != null) {
							points += Math.abs(q2.getOpen() - q1.getOpen());
						}
					} else {
						throw new ArithmeticException("Imposiible situation. Cannot buy positive wallet.");
					}
					break;
			}

			System.out.println(
					Quote.DATE_FORMAT.format(date) + " " + type + " " +
					(tmp != null ? tmp.getQuote().next() : "x") + " : " + signal.getQuote().next()
			);

			tmp = signal;
		}

		System.out.println("\nINCOME: " + (points * 10) + " PLN");
	}	

}
