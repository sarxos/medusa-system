package com.sarxos.medusa.data;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.Test;

import com.sarxos.medusa.market.Quote;


public class QuotesIteratorTest {

	private static final String RESOURCE = "com/sarxos/medusa/data/FW20M11.prn";

	@Test
	public void test_close() {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesIterator<Quote> qi = new QuotesIterator<Quote>(is);

		qi.close();

		try {
			is.read();
		} catch (IOException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void test_hasNext() {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesIterator<Quote> qi = new QuotesIterator<Quote>(is);

		for (int i = 0; i < 10; i++) {
			qi.hasNext();
			qi.hasNext();
			try {
				qi.next();
			} catch (NoSuchElementException e) {
				Assert.assertEquals(9, i);
			}
		}

		qi.close();
	}

	@Test
	public void test_next() {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesIterator<Quote> qi = new QuotesIterator<Quote>(is);

		List<Quote> quotes = new LinkedList<Quote>();

		while (qi.hasNext()) {
			quotes.add(qi.next());
		}

		Assert.assertEquals(9, quotes.size());
	}

	@Test
	public void test_forward() throws ParseException {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesIterator<Quote> qi = new QuotesIterator<Quote>(is);

		List<Quote> quotes = new LinkedList<Quote>();

		qi.forward(QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100622"));
		while (qi.hasNext()) {
			quotes.add(qi.next());
		}

		Assert.assertEquals(7, quotes.size());
	}

	@Test
	public void test_forwardMultiple() throws ParseException {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesIterator<Quote> qi = new QuotesIterator<Quote>(is);

		List<Quote> quotes = new LinkedList<Quote>();

		qi.forward(QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100622"));
		qi.forward(QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100623"));
		while (qi.hasNext()) {
			quotes.add(qi.next());
		}

		Assert.assertEquals(4, quotes.size());
	}

}
