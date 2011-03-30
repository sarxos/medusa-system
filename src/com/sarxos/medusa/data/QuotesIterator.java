package com.sarxos.medusa.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.sarxos.medusa.market.Future;
import com.sarxos.medusa.market.Index;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.util.Configuration;


/**
 * Create quotes iterator on the base of quote symbol. This constructor requires
 * PRN file to be available in the Medusa temporary directory and to not be
 * locked to read by any other thread or process.<br>
 * <br>
 * 
 * To check whether or not PRN file exist see "tmpfile" closure in the Medusa
 * config. Go into this location and check if PRN for given symbol is available
 * there.<br>
 * <br>
 * 
 * Please also note that default temporary directory is ignored by te repository
 * management system and therefore one have to download required PRN files
 * manually. Check other PRN utilities for details - there should be some
 * command to do that automatically.
 * 
 * @param <E> - quotes type - can be either {@link Quote}, {@link Future} or
 *            other type (like {@link Index}).
 * @author Bartosz Firyn (SarXos)
 */
public class QuotesIterator<E extends Quote> implements Iterator<E> {

	/**
	 * Static configuration instance.
	 */
	private static final Configuration CFG = Configuration.getInstance();

	/**
	 * Symbol to operate on (optional, can be null).
	 */
	private Symbol symbol = null;

	/**
	 * Underlying quotes stream reader.
	 */
	private QuotesStreamReader qsr = null;

	/**
	 * Create quotes iterator on the base of quote symbol. This constructor
	 * requires PRN file to be available in the Medusa temporary directory and
	 * to not be locked to read by any other thread or process.<br>
	 * <br>
	 * 
	 * To check whether or not PRN file exist see "tmpfile" closure in the
	 * Medusa config. Go into this location and check if PRN for given symbol is
	 * available there.<br>
	 * <br>
	 * 
	 * Please also note that default temporary directory is ignored by te
	 * repository management system and therefore one have to download required
	 * PRN files manually. Check other PRN utilities for details - there should
	 * be some command to do that automatically.
	 * 
	 * @param symbol - symbol to open
	 * @throws IOException if PRN file does not exist
	 */
	public QuotesIterator(Symbol symbol) throws IOException {

		if (symbol == null) {
			throw new IllegalArgumentException("Symbol cannot be null");
		}

		this.symbol = symbol;

		String tmpdir = CFG.getProperty("core", "tmpdir");
		String name = symbol.getName();
		String fname = tmpdir + "/intraday/" + name + ".prn";

		init(new FileInputStream(fname));
	}

	/**
	 * Create quotes iterator on the base of input stream (this should be a
	 * stream to the file or resource with Metastock quotes).
	 * 
	 * @param is
	 */
	public QuotesIterator(InputStream is) {
		if (is == null) {
			throw new IllegalArgumentException("Input stream cannot be null");
		}
		init(is);
	}

	private void init(InputStream is) {
		this.qsr = new QuotesStreamReader(is);
	}

	private E next = null;

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasNext() {
		try {
			next = (E) qsr.read();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return next != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		E quote = next;
		if (next != null) {
			next = null;
		} else {
			try {
				quote = (E) qsr.read();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return quote;
	}

	@Override
	public void remove() {
		throw new IllegalStateException(
			"Remove operation within " + getClass().getSimpleName() + " " +
			"is not supported");
	}

	/**
	 * @return Return symbol
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * Close the underlying quotes stream.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		qsr.close();
	}

	/**
	 * Create collection from this iterator. This method is sometimes useful but
	 * shall not be overused due to its performance impact. Quotes iterator is a 
	 * lightweight object allowing us to iterate through the all quotes without
	 * necessity to allocate large amounts of memory. When you convert it to 
	 * collection this advantage does not take effect (it means you change
	 * lightweight object to very heavy weighted).
	 * 
	 * @return Return new collection of all quotes.
	 */
	public Collection<E> collection() {
		List<E> quotes = new LinkedList<E>();
		while (hasNext()) {
			quotes.add(next());
		}
		return quotes;
	}
	
	public static void main(String[] args) throws IOException {
		QuotesIterator<Quote> qi = new QuotesIterator<Quote>(Symbol.CPS);
		int i = 0;
		while (qi.hasNext()) {
			qi.next();
			i++;
		}
		System.out.println(i);
	}
}
