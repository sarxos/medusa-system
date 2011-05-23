package com.sarxos.medusa.trader;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.SignalType;
import com.sarxos.medusa.market.Symbol;


public class DecisionEventTest {

	@SuppressWarnings("unchecked")
	private static final SignalGenerator<Quote> SIGGEN = EasyMock.createMock(SignalGenerator.class);
	private static final Trader TRADER = EasyMock.createMock(Trader.class);
	private static final DecisionMaker DM = new DecisionMaker(TRADER, SIGGEN);
	private static final Symbol SYMBOL = Symbol.QQQ;
	private static final Paper PAPER = new Paper(SYMBOL);
	private static final Quote QUOTE = new Quote();
	private static final SignalType SIGNAL = SignalType.BUY;

	@Test
	public void test_source() {
		DecisionEvent de = new DecisionEvent(DM, PAPER, QUOTE, SIGNAL);
		Assert.assertEquals(DM, de.getSource());
	}

	@Test
	public void test_paper() {
		DecisionEvent de = new DecisionEvent(DM, PAPER, QUOTE, SIGNAL);
		Assert.assertEquals(PAPER, de.getPaper());
	}

	@Test
	public void test_quote() {
		DecisionEvent de = new DecisionEvent(DM, PAPER, QUOTE, SIGNAL);
		Assert.assertEquals(QUOTE, de.getQuote());
	}

	@Test
	public void test_signal() {
		DecisionEvent de = new DecisionEvent(DM, PAPER, QUOTE, SIGNAL);
		Assert.assertEquals(SIGNAL, de.getSignalType());
	}
}
