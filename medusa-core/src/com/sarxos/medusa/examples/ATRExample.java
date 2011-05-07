package com.sarxos.medusa.examples;

import java.io.File;
import java.util.List;

import com.sarxos.medusa.data.QuotesRemoteReader;
import com.sarxos.medusa.data.QuotesReaderException;
import com.sarxos.medusa.market.Index;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.math.ATR;
import com.sarxos.medusa.util.StoqReader;


public class ATRExample {

	public static void main(String[] args) throws QuotesReaderException {
		
		QuotesRemoteReader<Index> reader = new StoqReader<Index>(Index.class);
		List<Index> data = reader.read(new File("data/wig20_d.csv").toURI());
		
		int N = 20;
		
		Quote[] quotes = new Quote[N];
		for (int i = 0; i < N; i++) {
			quotes[i] = data.get(data.size() - N + i);
		}
		
		double[] atrs = ATR.atr14(quotes);
		for (int i = 0; i < atrs.length; i++) {
			System.out.print(quotes[i] + " === " + atrs[i] + "\n");
		}
	}
}
