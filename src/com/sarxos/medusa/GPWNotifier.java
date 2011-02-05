package com.sarxos.medusa;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import com.sarxos.medusa.data.QuotesReader;
import com.sarxos.medusa.data.QuotesReaderException;
import com.sarxos.medusa.data.stoq.StoqReader;
import com.sarxos.medusa.market.Index;



public class GPWNotifier {

	public static void main(String[] args) throws URISyntaxException, QuotesReaderException {
		
		QuotesReader<Index> qr = new StoqReader<Index>(Index.class);
		
		File f = new File("kgh_d.csv");
		
		List<Index> index = qr.read(f.toURI());
		
		System.out.println(index.size());
		
	}
	
}
