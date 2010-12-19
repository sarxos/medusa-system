package com.sarxos.gpwnotifier;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.market.Index;



public class GPWNotifier {

	public static void main(String[] args) throws URISyntaxException, QuotesReaderException {
		
		QuotesReader<Index> qr = new StoqReader<Index>(Index.class);
		
		File f = new File("kgh_d.csv");
		
		List<Index> index = qr.read(f.toURI());
		
		System.out.println(index.size());
		
	}
	
}
