package com.sarxos.medusa.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.medusa.util.DateUtils;


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

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(HistoryProviderSim.class.getSimpleName());

	@Override
	public List<Quote> getLastQuotes(Symbol symbol) throws ProviderException {
		throw new RuntimeException("Get last quotes is not implemented");
	}

	@Override
	public List<Quote> getAllQuotes(Symbol symbol) throws ProviderException {

		String tmpdir = CFG.getProperty("core", "tmpdir");
		File f = new File(tmpdir + "/history/" + symbol.getName() + ".mst");

		List<Quote> quotes = null;

		if (!f.exists()) {
			try {
				FileUtils.touch(f);
			} catch (IOException e) {
				throw new RuntimeException("Cannot touch file " + f, e);
			}

			quotes = buildMSTFile(symbol, f);
		} else {
		
			QuotesIterator<Quote> qi = null;
			InputStream is = null;
			try {
				is = FileUtils.openInputStream(f);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			qi = new QuotesIterator<Quote>(is);
			quotes = new LinkedList<Quote>(qi.collection());
			qi.close();
		}
		
		return quotes;
	}

	private List<Quote> buildMSTFile(Symbol symbol, File f) throws ProviderException {

		LOG.info("Building history MST file for symbol " + symbol);

		List<Quote> toWrite = new LinkedList<Quote>();
		QuotesIterator<Quote> qi = getIntradayQuotes(symbol);

		Quote q = null;
		Date date = null;

		double open = 0;
		double high = Double.MIN_VALUE;
		double low = Double.MAX_VALUE;
		double close = 0;
		long volume = 0;

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(DateUtils.fromCGL("19800101")); // some old date

		int tmp = -1, day = -1;

		if (qi.hasNext()) {
			q = qi.next();
			date = q.getDate();
			open = q.getOpen();
			high = q.getHigh();
			low = q.getLow();
			volume = q.getVolume();
			calendar.setTime(q.getDate());
		}

		while (qi.hasNext()) {

			q = qi.next();
			day = calendar.get(Calendar.DAY_OF_YEAR);
			calendar.setTime(q.getDate());

			if (tmp != day) {
				toWrite.add(new Quote(symbol, date, open, high, low, close, volume));
				date = q.getDate();
				open = q.getOpen();
				high = q.getHigh();
				close = q.getClose();
				low = q.getLow();
				volume = q.getVolume();
				tmp = day;
			} else {
				high = high > q.getHigh() ? high : q.getHigh();
				low = low < q.getLow() ? low : q.getLow();
				close = q.getClose();
				volume += q.getVolume();
				date = q.getDate();
			}
		}

		
		StringBuilder sb = new StringBuilder("<TICKER>,<DTYYYYMMDD>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>,<OPENINT>\n");
		
		FileOutputStream fos = null;
		try {
			 fos = FileUtils.openOutputStream(f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		for (Quote wq : toWrite) {
			
			sb.append(wq.getSymbol().getName()).append(',');
			sb.append(DateUtils.toCGL(wq.getDate())).append(',');
			sb.append(wq.getOpen()).append(',');
			sb.append(wq.getHigh()).append(',');
			sb.append(wq.getLow()).append(',');
			sb.append(wq.getClose()).append(',');
			sb.append(wq.getVolume()).append(',');
			sb.append("1").append('\n'); // open interests (not taken into account)
			
			try {
				IOUtils.write(sb, fos);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			sb.delete(0, sb.length());
		}

		IOUtils.closeQuietly(fos);

		return toWrite;
	}

	@Override
	public QuotesIterator<Quote> getIntradayQuotes(Symbol symbol) throws ProviderException {

		String tmpdir = CFG.getPath("core", "tmpdir");
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

	public static void main(String[] args) throws ProviderException {
		HistoryProviderSim hps = new HistoryProviderSim();
		List<Quote> quotes = hps.getAllQuotes(Symbol.FW20M11);
		System.out.println(quotes.size());
	}
}
