package com.sarxos.medusa.market;

import org.junit.Assert;
import org.junit.Test;


public class PaperTest {

	@Test
	public void test_symbol() {
		Paper p = new Paper(Symbol.KGH);
		Assert.assertEquals(p.getSymbol(), Symbol.KGH);
	}

	@Test
	public void test_group() {
		Paper p = new Paper(Symbol.FQQQ);
		Assert.assertSame(SecuritiesGroup.FUTURES_QUOTES, p.getGroup());
		p = new Paper(Symbol.FW20M11);
		Assert.assertSame(SecuritiesGroup.FUTURES_INDEXES, p.getGroup());
	}
}
