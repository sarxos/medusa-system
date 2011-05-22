package com.sarxos.medusa.trader;

import junit.framework.Assert;

import org.junit.Test;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


public class PriceEventTest {

	@Test
	public void test_previousPrice() {
		Observer o = new Observer(Symbol.QQQ);
		Quote q = new Quote();
		double previous = 1.0;
		PriceEvent pe = new PriceEvent(o, previous, 2.0, q);
		Assert.assertEquals(previous, pe.getPreviousPrice());
	}

	@Test
	public void test_currentPrice() {
		Observer o = new Observer(Symbol.QQQ);
		Quote q = new Quote();
		double current = 2.0;
		PriceEvent pe = new PriceEvent(o, 1.0, current, q);
		Assert.assertEquals(current, pe.getCurrentPrice());
	}

	@Test
	public void test_source() {
		Observer o = new Observer(Symbol.QQQ);
		Quote q = new Quote();
		PriceEvent pe = new PriceEvent(o, 1.0, 2.0, q);
		Assert.assertSame(o, pe.getSource());
	}

	@Test
	public void test_quote() {
		Observer o = new Observer(Symbol.QQQ);
		Quote q = new Quote();
		PriceEvent pe = new PriceEvent(o, 1.0, 2.0, q);
		Assert.assertSame(q, pe.getQuote());
	}
}
