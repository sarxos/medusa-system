package com.sarxos.medusa.market;

import junit.framework.TestCase;

import com.sarxos.medusa.market.Symbol;


public class PaperTest extends TestCase {

	private static final String SYMBOL_1 = Symbol.KGH.getName();
	private static final String SYMBOL_2 = Symbol.BRE.getName();

	public void test_findSymbol() {
		Symbol s = Symbol.valueOfName(SYMBOL_1);
		assertEquals(s, Symbol.KGH);
		s = Symbol.valueOfName(SYMBOL_2);
		assertEquals(s, Symbol.BRE);
	}
}
