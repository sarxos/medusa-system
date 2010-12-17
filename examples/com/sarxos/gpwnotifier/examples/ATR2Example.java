package com.sarxos.gpwnotifier.examples;

import java.io.File;
import java.util.List;

import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.entities.Index;
import com.sarxos.gpwnotifier.entities.Quote;
import com.sarxos.gpwnotifier.math.ATR;


public class ATR2Example {

	public static void main(String[] args) throws QuotesReaderException {
		
		QuotesReader<Index> reader = new StoqReader<Index>(Index.class);
		List<Index> data = reader.read(new File("data/kgh_d.csv").toURI());
		
		int N = 20;
		
		Quote[] quotes = new Quote[N];
		for (int i = 0; i < N; i++) {
			quotes[i] = data.get(data.size() - N + i);
		}

		System.out.println(ATR.atr(quotes[N - 1], 2) * .5 + quotes[N - 2].getClose());
	}
}
