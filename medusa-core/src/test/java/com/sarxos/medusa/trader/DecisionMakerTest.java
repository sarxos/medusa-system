package com.sarxos.medusa.trader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.trader.Observer.NullEvent;


public class DecisionMakerTest extends TestCase {

	@Test
	public void test_constructor1() {
		new DecisionMaker();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_constructor2() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		new DecisionMaker(t, sg);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_trader() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		Assert.assertSame(t, dm.getTrader());
		t = EasyMock.createMock(Trader.class);
		dm.setTrader(t);
		Assert.assertSame(t, dm.getTrader());
		try {
			dm.setTrader(null);
			Assert.fail("There should be an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_signalGenerator() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		Assert.assertSame(sg, dm.getSignalGenerator());
		sg = EasyMock.createMock(SignalGenerator.class);
		dm.setSignalGenerator(sg);
		Assert.assertSame(sg, dm.getSignalGenerator());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_registry() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		QuotesRegistry qr = EasyMock.createMock(QuotesRegistry.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		dm.setQuotesRegistry(qr);
		Assert.assertSame(qr, dm.getQuotesRegistry());
		try {
			dm.setQuotesRegistry(null);
			Assert.fail("Exception shall be thrown here");
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_handleNull1() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		QuotesRegistry qr = EasyMock.createMock(QuotesRegistry.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		dm.setQuotesRegistry(qr);
		dm.handleNull(EasyMock.createMock(NullEvent.class));
	}
}
