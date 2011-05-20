package com.sarxos.medusa.sim;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.HistoryProvider;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.util.Configuration;


/**
 * Fake history provider won't get quotes from the original source. Instead it
 * will simply open local copy of quotes PRN file. Exception will be thrown if
 * there is no such file.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class FakeHistoryProvider implements HistoryProvider {

	/**
	 * Configuration
	 */
	private static final Configuration CFG = Configuration.getInstance();

	@Override
	public List<Quote> getLastQuotes(Symbol symbol) throws ProviderException {
		throw new RuntimeException("Get last quotes not implmented");
	}

	@Override
	public List<Quote> getAllQuotes(Symbol symbol) throws ProviderException {
		throw new RuntimeException("Get all quotes not implmented");
	}

	@Override
	public QuotesIterator<Quote> getIntradayQuotes(Symbol symbol) throws ProviderException {

		String tmpdir = CFG.getProperty("core", "tmpdir");
		File f = new File(tmpdir + "/intraday/" + symbol.getName() + ".prn");

		InputStream is = null;
		try {
			is = FileUtils.openInputStream(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new QuotesIterator<Quote>(is);
	}
}
