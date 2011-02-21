package com.sarxos.medusa.test;

import junit.framework.TestCase;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.trader.Trader;


public class DBDAOTraderTest extends TestCase {

	private static final String NAME = "Buka";
	private static final Symbol SYM = Symbol.KGH;
	private static final SignalGenerator<Quote> SIGGEN = new MAVD(3, 13, 30);
	private static final Position POSITION = Position.SHORT;

	private DBDAO dbdao = null;
	private Trader trader = null;

	public DBDAOTraderTest() {
		this.dbdao = DBDAO.getInstance();
		this.trader = new Trader(NAME, SIGGEN, SYM);
		this.trader.setPosition(POSITION);
	}

	public void test_addTrader() {
		try {
			boolean ok = dbdao.addTrader(trader);
			assertTrue(ok);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public void test_getTrader() {
		Trader t = null;
		try {
			t = dbdao.getTrader(NAME);
			assertEquals(NAME, t.getName());
			assertEquals(SYM, t.getSymbol());
			assertEquals(POSITION, t.getPosition());
			assertEquals(SIGGEN.getClass(), t.getGenerator().getClass());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public void test_updateTrader() {
		Trader t = null;
		try {
			t = dbdao.getTrader(NAME);
			t.setPosition(Position.LONG);
			dbdao.updateTrader(t);
			t = dbdao.getTrader(NAME);
			assertEquals(Position.LONG, t.getPosition());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public void test_removeTrader() {
		try {
			boolean removed = dbdao.removeTrader(NAME);
			assertTrue(removed);
			Trader t = dbdao.getTrader(NAME);
			assertNull(t);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
