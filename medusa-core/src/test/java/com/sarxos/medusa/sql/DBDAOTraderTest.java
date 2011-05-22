package com.sarxos.medusa.sql;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Ignore;

import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.sql.DBDAO;
import com.sarxos.medusa.trader.Trader;
import com.sarxos.medusa.trader.TraderTest;
import com.sarxos.medusa.trader.TraderTest.TestTrader;


@Ignore
public class DBDAOTraderTest extends TestCase {

	private static final String NAME = "Buka";
	private static final Paper PAPER = new Paper(Symbol.KGH, 100, 0);
	private static final SignalGenerator<Quote> SIGGEN = new MAVD(3, 13, 30);
	private static final Position POSITION = Position.SHORT;

	private DBDAO dbdao = null;
	private Trader trader = null;

	@BeforeClass
	public void init() {
		this.dbdao = DBDAO.getInstance();
		this.trader = new TestTrader(NAME, SIGGEN, PAPER);
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
			assertEquals(PAPER.getSymbol(), t.getSymbol());
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
