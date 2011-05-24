package com.sarxos.medusa.trader;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sarxos.medusa.data.QuotesRegistry;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Signal;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.trader.DecisionMaker.NullEventHandler;
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

	private static class TestNullEventHandler implements NullEventHandler {

		public NullEvent ne = null;

		@Override
		public void handleNull(NullEvent ne) {
			this.ne = ne;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_nullHandler() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		QuotesRegistry qr = EasyMock.createMock(QuotesRegistry.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		NullEventHandler neh = new TestNullEventHandler();
		dm.setNullHandler(neh);
		Assert.assertEquals(neh, dm.getNullHandler());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_handleNull() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		try {
			dm.handleNull(EasyMock.createMock(NullEvent.class));
		} catch (Exception e) {
			Assert.fail("No expected exception");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_handleNull2() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		TestNullEventHandler neh = new TestNullEventHandler();
		dm.setNullHandler(neh);
		NullEvent ne = EasyMock.createMock(NullEvent.class);
		dm.handleNull(ne);
		Assert.assertNotNull(neh.ne);
		Assert.assertSame(ne, neh.ne);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_decisionListener() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		DecisionListener dl = EasyMock.createMock(DecisionListener.class);
		Assert.assertTrue(dm.addDecisionListener(dl));
		Assert.assertNotNull(dm.getDecisionListeners());
		Assert.assertEquals(2, dm.getDecisionListeners().length);
		Assert.assertFalse(dm.addDecisionListener(dl));
		Assert.assertEquals(2, dm.getDecisionListeners().length);
		Assert.assertTrue(dm.removeDecisionListener(dl));
		Assert.assertFalse(dm.removeDecisionListener(dl));
		Assert.assertEquals(1, dm.getDecisionListeners().length);
		Assert.assertSame(t, dm.getDecisionListeners()[0]);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_priceChangeNullEvent() {
		Trader t = EasyMock.createMock(Trader.class);
		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		DecisionMaker dm = new DecisionMaker(t, sg);
		TestNullEventHandler neh = new TestNullEventHandler();
		dm.setNullHandler(neh);
		NullEvent ne = EasyMock.createMock(NullEvent.class);
		dm.priceChange(ne);
		Assert.assertNotNull(neh.ne);
		Assert.assertSame(ne, neh.ne);
	}

	private static class TestDecisionListener implements DecisionListener {

		public DecisionEvent de = null;

		@Override
		public void decisionChange(DecisionEvent de) {
			this.de = de;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_priceChange() {

		Symbol s = Symbol.QQQ;
		Quote q = new Quote(s, new Date(), 2.0, 3.0, 1.0, 3.0, 4000);
		Paper p = new Paper(s);

		Trader t = EasyMock.createStrictMock(Trader.class);
		TestDecisionListener tdl = new TestDecisionListener();

		EasyMock.expect(t.getPaper()).andReturn(p);
		t.decisionChange(EasyMock.anyObject(DecisionEvent.class));
		EasyMock.expectLastCall().andDelegateTo(tdl);
		EasyMock.replay(t);

		List<Quote> quotes = new LinkedList<Quote>();
		for (int i = 0; i < 100; i++) {
			Quote qu = new Quote();
			if (quotes.size() > 0) {
				qu.setPrev(quotes.get(i - 1));
				quotes.get(i - 1).setNext(qu);
			}
			quotes.add(qu);
		}

		QuotesRegistry qr = EasyMock.createMock(QuotesRegistry.class);
		EasyMock.expect(qr.getQuotes(EasyMock.eq(s))).andReturn(quotes);
		EasyMock.replay(qr);

		SignalGenerator<Quote> sg = EasyMock.createMock(SignalGenerator.class);
		EasyMock.expect(sg.generate(EasyMock.eq(q))).andReturn(new Signal(q, SignalType.BUY));
		EasyMock.replay(sg);

		DecisionMaker dm = new DecisionMaker(t, sg, qr);

		Observer o = EasyMock.createMock(Observer.class);

		PriceEvent pe = new PriceEvent(o, 20, 21, q);
		dm.priceChange(pe);

		EasyMock.verify(t);

		DecisionEvent de = tdl.de;

		Assert.assertNotNull(de);
		Assert.assertSame(SignalType.BUY, de.getSignalType());
		Assert.assertSame(p, de.getPaper());
		Assert.assertSame(q, de.getQuote());
		Assert.assertSame(dm, de.getSource());
	}
}
