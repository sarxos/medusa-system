package com.sarxos.medusa.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.util.Configuration;


/**
 * Fake history provider won't get quotes from the original source. Instead it
 * will simply open local copy of quotes PRN file. Exception will be thrown if
 * there is no such file.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see RealTimeProviderSim
 */
public class HistoryProviderSim implements HistoryProvider {

	/**
	 * Configuration
	 */
	private static final Configuration CFG = Configuration.getInstance();

	@Override
	public List<Quote> getLastQuotes(Symbol symbol) throws ProviderException {
		throw new RuntimeException("Get last quotes is not implemented");
	}

	@Override
	public List<Quote> getAllQuotes(Symbol symbol) throws ProviderException {

		String tmpdir = CFG.getProperty("core", "tmpdir");
		File f = new File(tmpdir + "/history/" + symbol.getName() + ".mst");

		if (!f.exists()) {
			try {
				FileUtils.touch(f);
			} catch (IOException e) {
				throw new RuntimeException("Cannot touch file " + f, e);
			}

			buildMSTFile(symbol, f);
		}
		
		QuotesIterator<Quote> qi = null;
		InputStream is = null;
		try {
			is = FileUtils.openInputStream(f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		qi = new QuotesIterator<Quote>(is);
		LinkedList<Quote> quotes = new LinkedList<Quote>(qi.collection());
		qi.close();
		
		return quotes;
	}

	private void buildMSTFile(Symbol symbol, File f) throws ProviderException {

		QuotesIterator<Quote> qi = getIntradayQuotes(symbol);
		Quote q = null;

		double open = 0;
		double high = Double.MIN_VALUE;
		double low = Double.MAX_VALUE;
		double close = 0;
		long volume = 0;

		while (qi.hasNext()) {
			q = qi.next();

			// TODO build quotes history from intraday

			// <TICKER>,<DTYYYYMMDD>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>,<OPENINT>
			// FW20M11,20100621,2426,2440,2418,2432,95,64
		}
	}

	@Override
	public QuotesIterator<Quote> getIntradayQuotes(Symbol symbol) throws ProviderException {

		String tmpdir = CFG.getProperty("core", "tmpdir");
		File f = new File(tmpdir + "/intraday/" + symbol.getName() + ".prn");

		if (!f.exists()) {
			throw new ProviderException("Missing file " + f.getPath());
		}

		InputStream is = null;
		try {
			is = FileUtils.openInputStream(f);
		} catch (IOException e) {
			throw new ProviderException("Cannot open stream from file " + f.getPath());
		}

		return new QuotesIterator<Quote>(is);
	}
}
