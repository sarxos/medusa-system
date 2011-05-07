package com.sarxos.medusa.market;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import com.sarxos.medusa.market.Quote;


public class QuoteTest extends TestCase {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String DATE_STRING = "2010-12-30";
	private static final double OPEN = 10.5;
	private static final double CLOSE = 12.3;
	private static final double LOW = 9.4;
	private static final double HIGH = 13.7;
	private static final long VOLUME = 1234566;

	public void test_setGetQuoteDate() {

		Date date = null;
		try {
			date = DATE_FORMAT.parse(DATE_STRING);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		Quote q = new Quote();
		q.setDate(date);

		assertEquals(date, q.getDate());
		assertEquals(DATE_STRING, q.getDateString());
	}

	public void test_setGetOpen() {
		Quote q = new Quote();
		q.setOpen(OPEN);
		assertEquals(OPEN, q.getOpen());
	}

	public void test_setGetClose() {
		Quote q = new Quote();
		q.setClose(CLOSE);
		assertEquals(CLOSE, q.getClose());
	}

	public void test_setGetLow() {
		Quote q = new Quote();
		q.setLow(LOW);
		assertEquals(LOW, q.getLow());
	}

	public void test_setGetHigh() {
		Quote q = new Quote();
		q.setHigh(HIGH);
		assertEquals(HIGH, q.getHigh());
	}

	public void test_setGetVolume() {
		Quote q = new Quote();
		q.setVolume(VOLUME);
		assertEquals(VOLUME, q.getVolume());
	}

	public void test_constructorFirst() {

		Date date = null;
		try {
			date = DATE_FORMAT.parse(DATE_STRING);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		Quote q = new Quote(date, OPEN, HIGH, LOW, CLOSE, VOLUME);

		assertEquals(date, q.getDate());
		assertEquals(DATE_STRING, q.getDateString());
		assertEquals(OPEN, q.getOpen());
		assertEquals(CLOSE, q.getClose());
		assertEquals(LOW, q.getLow());
		assertEquals(HIGH, q.getHigh());
		assertEquals(VOLUME, q.getVolume());
	}

	public void test_constructorSecond() {

		Quote q = new Quote(DATE_STRING, OPEN, HIGH, LOW, CLOSE, VOLUME);

		Date date = null;
		try {
			date = DATE_FORMAT.parse(DATE_STRING);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		assertEquals(date, q.getDate());
		assertEquals(DATE_STRING, q.getDateString());
		assertEquals(OPEN, q.getOpen());
		assertEquals(CLOSE, q.getClose());
		assertEquals(LOW, q.getLow());
		assertEquals(HIGH, q.getHigh());
		assertEquals(VOLUME, q.getVolume());
	}

	public void test_quoteNextPrev() {

		Quote a = new Quote();
		Quote b = new Quote();
		Quote c = new Quote();

		a.setNext(b);
		b.setNext(c);

		b.setPrev(a);
		c.setPrev(b);

		assertEquals(a.prev(), null);
		assertEquals(b.prev(), a);
		assertEquals(c.prev(), b);

		assertEquals(a.next(), b);
		assertEquals(b.next(), c);
		assertEquals(c.next(), null);
	}

}
