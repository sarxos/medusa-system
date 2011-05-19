package com.sarxos.medusa.data;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.sarxos.medusa.market.Quote;


public class QuotesStreamReaderTest {

	private static final String RESOURCE = "com/sarxos/medusa/data/FW20M11.prn";

	@Test
	public void test_read() {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesStreamReader qsr = new QuotesStreamReader(is);

		try {
			int i = 0;
			Quote q = null;
			do {
				q = qsr.read();
				if (q != null) {
					i++;
				}
			} while (q != null);

			// we have 9 quotes in the test file
			Assert.assertEquals(9, i);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void test_unmarshall() throws IOException, ParseException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesStreamReader qsr = new QuotesStreamReader(is);
		Quote q = qsr.read();
		Assert.assertEquals(2000.50, q.getOpen());
		Assert.assertEquals(2005.00, q.getHigh());
		Assert.assertEquals(1995.00, q.getLow());
		Assert.assertEquals(2000.00, q.getClose());
	}

	@Test
	public void test_readArray() {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesStreamReader qsr = new QuotesStreamReader(is);
		Quote[] quotes = new Quote[10];

		try {
			int i = qsr.read(quotes);
			Assert.assertEquals(9, i);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int k = 0;
		for (int i = 0; i < quotes.length; i++) {
			if (quotes[i] != null) {
				k++;
			}
		}

		Assert.assertEquals(9, k);
	}

	@Test
	public void test_ready() {

		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesStreamReader qsr = new QuotesStreamReader(is);

		try {
			Assert.assertTrue(qsr.ready());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		}

		Quote q = null;
		do {
			try {
				q = qsr.read();
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("Unexpected exception");
			}
		} while (q != null);

		try {
			Assert.assertFalse(qsr.ready());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		}

		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_close() {
		InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		QuotesStreamReader qsr = new QuotesStreamReader(is);

		try {
			qsr.close();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		}

		try {
			is.read();
			Assert.fail("Input stream should be also closed");
		} catch (IOException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void test_seek() throws ParseException {

		InputStream is = null;
		QuotesStreamReader qsr = null;
		Date date = null;
		int i = 0;

		is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		qsr = new QuotesStreamReader(is);
		date = QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100621");

		i = 0;
		try {
			qsr.seek(date);
			i = qsr.read(new Quote[10]);
			Assert.assertEquals(9, i);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		qsr = new QuotesStreamReader(is);
		date = QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100622");

		i = 0;
		try {
			qsr.seek(date);
			i = qsr.read(new Quote[10]);
			Assert.assertEquals(7, i);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
		qsr = new QuotesStreamReader(is);

		i = 0;
		try {
			qsr.seek(QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100621"));
			qsr.seek(QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100622"));
			qsr.seek(QuotesStreamReader.DATE_FORMAT_SHORT.parse("20100623"));
			i = qsr.read(new Quote[10]);
			Assert.assertEquals(4, i);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
