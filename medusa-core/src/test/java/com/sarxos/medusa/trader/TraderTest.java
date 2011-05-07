package com.sarxos.medusa.trader;

import junit.framework.TestCase;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.trader.StocksTrader;
import com.sarxos.medusa.trader.Trader;


public class TraderTest extends TestCase {

	private static final DBDAO dbdao = DBDAO.getInstance();
	private static final String NAME = "TestTRader";
	private static final SignalGenerator<Quote> SIGGEN = new MAVD();
	private static final Paper PAPER = new Paper(Symbol.QQQ, 100, 0);

	public void test_CreateTrader() throws Exception {

		Trader t;

		dbdao.removeTrader(NAME);

		t = DBDAO.getInstance().getTrader(NAME);
		assertNull(t);

		t = new StocksTrader(NAME, SIGGEN, PAPER);
		Thread.sleep(2000); // wait for persistence writer

		t = DBDAO.getInstance().getTrader(NAME);
		assertNotNull(t);
		assertEquals(t.getName(), NAME);
		assertEquals(t.getGeneratorClassName(), SIGGEN.getClass().getName());
		assertEquals(t.getGenerator(), SIGGEN);
		assertEquals(t.getSymbol(), PAPER.getSymbol());
		assertEquals(t.getPosition(), Position.SHORT);
	}

	public void test_ReadTrader() throws Exception {

		Trader t = DBDAO.getInstance().getTrader(NAME);
		assertNotNull(t);
		assertEquals(t.getName(), NAME);
		assertEquals(t.getGeneratorClassName(), SIGGEN.getClass().getName());
		assertEquals(t.getGenerator(), SIGGEN);
		assertEquals(t.getSymbol(), PAPER.getSymbol());
		assertEquals(t.getPosition(), Position.SHORT);
	}

	public void test_UpdateTrader() throws Exception {

		Trader t = DBDAO.getInstance().getTrader(NAME);
		assertNotNull(t);

		t.setPosition(Position.LONG);
		assertEquals(t.getPosition(), Position.LONG);
		t.setPaperQuantity(100);
		Thread.sleep(2000); // wait for persistence writer

		t = DBDAO.getInstance().getTrader(NAME);
		assertNotNull(t);
		assertEquals(Position.LONG, t.getPosition());
		assertEquals(100, t.getPaperQuantity());

		t.setPosition(Position.SHORT);
		assertEquals(t.getPosition(), Position.SHORT);
		Thread.sleep(2000); // wait for persistence writer

		t = DBDAO.getInstance().getTrader(NAME);
		assertNotNull(t);
		assertEquals(t.getPosition(), Position.SHORT);
	}

	public void test_DeleteTrader() throws Exception {

		Trader t = DBDAO.getInstance().getTrader(NAME);
		assertNotNull(t);

		dbdao.removeTrader(NAME);

		t = DBDAO.getInstance().getTrader(NAME);
		assertNull(t);
	}
}
