package com.sarxos.medusa.trader;

import junit.framework.TestCase;

import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;


public class DecisionMakerTest extends TestCase {

	private static final Symbol SYMBOL = Symbol.QQQ;
	private static final SignalGenerator<Quote> SIGGEN = new MAVD();

}
