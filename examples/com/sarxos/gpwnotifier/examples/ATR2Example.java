package com.sarxos.gpwnotifier.examples;

import java.io.File;
import java.util.List;

import com.sarxos.medusa.data.QuotesReader;
import com.sarxos.medusa.data.QuotesReaderException;
import com.sarxos.medusa.market.Index;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.math.ATR;
import com.sarxos.medusa.util.StoqReader;


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
