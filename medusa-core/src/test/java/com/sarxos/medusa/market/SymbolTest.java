package com.sarxos.medusa.market;

import org.junit.Assert;
import org.junit.Test;


public class SymbolTest {

	private static final String SYMBOL_1 = Symbol.KGH.getName();
	private static final String SYMBOL_2 = Symbol.BRE.getName();

	@Test
	public void test_findSymbol() {
		Symbol s = Symbol.valueOfName(SYMBOL_1);
		Assert.assertSame(s, Symbol.KGH);
		s = Symbol.valueOfName(SYMBOL_2);
		Assert.assertSame(s, Symbol.BRE);
	}
}
