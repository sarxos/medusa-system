package com.sarxos.gpwnotifier.examples;

import java.io.File;
import java.util.List;

import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.market.Index;
import com.sarxos.gpwnotifier.math.MA;


public class MAExample {

	public static void main(String[] args) throws QuotesReaderException {
		
		QuotesReader<Index> reader = new StoqReader<Index>(Index.class);
		List<Index> data = reader.read(new File("data/wig20_d.csv").toURI());		

		int n = data.size();
		int N = 40;
		
		Index[] quotes = new Index[N];
		
		for (int i = 0; i < N; i++) {
			quotes[i] = data.get(n - N + i);
		}
		
		double[] ema3 = MA.ema(quotes, 3);
		double[] sma10 = MA.sma(quotes, 10);
		double delta = 0;
		
		for (int i = 0; i < N; i++) {
			delta = ema3[i] - sma10[i];
			System.out.println(quotes[i].getDateString() + " " + 
					(delta > 0 ? "U" : "D") + " " + 
					ema3[i]);
		}
		
		
		
	}
}
