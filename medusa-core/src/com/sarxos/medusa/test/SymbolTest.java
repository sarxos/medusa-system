package com.sarxos.medusa.test;

import junit.framework.TestCase;

import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Symbol;


public class SymbolTest extends TestCase {

	private static final Symbol SYMBOL_A = Symbol.KGH;
	private static final Symbol SYMBOL_B = Symbol.BRE;
	private static final int QUANTITY = 10;
	private static final int DESIRED_A = 20;
	private static final int DESIRED_B = 40;

	public void test_paperSymbol() {

		Paper p = new Paper(SYMBOL_A, DESIRED_A);
		assertEquals(p.getSymbol(), SYMBOL_A);
		p.setSymbol(SYMBOL_B);
		assertEquals(p.getSymbol(), SYMBOL_B);
	}

	public void test_paperDesiredQuantity() {
		Paper p = new Paper(SYMBOL_A, DESIRED_A);
		assertEquals(p.getSymbol(), SYMBOL_A);
		p.setDesiredQuantity(DESIRED_B);
		assertEquals(p.getDesiredQuantity(), DESIRED_B);
	}

	public void test_paperQuantity() {
		Paper p = new Paper(SYMBOL_A, DESIRED_A);
		p.setQuantity(QUANTITY);
		assertEquals(p.getQuantity(), QUANTITY);
	}
}
