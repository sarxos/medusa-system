package com.sarxos.medusa.trader;

import junit.framework.TestCase;

import org.junit.Ignore;

import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.sql.DBDAO;


@Ignore
public class TraderTest extends TestCase {

	public static class TestTrader extends Trader {

		@Override
		public void decisionChange(DecisionEvent event) {
		}

		public TestTrader(String name, SignalGenerator<Quote> siggen, Symbol paper, RealTimeProvider provider) {
			super(name, siggen, paper, provider);
		}

		public TestTrader(String name, SignalGenerator<Quote> siggen, Symbol paper) {
			super(name, siggen, paper);
		}
	}

	private static DBDAO dbdao = DBDAO.getInstance();
	private static final String NAME = "TestTRader";
	private static final SignalGenerator<Quote> SIGGEN = new MAVD();
	private static final Symbol SYMBOL = Symbol.QQQ;
	private static final Paper PAPER = new Paper(SYMBOL, 100, 0);

	public void test_CreateTrader() throws Exception {

		Trader t;

		dbdao.removeTrader(NAME);

		t = DBDAO.getInstance().getTrader(NAME);
		assertNull(t);

		t = new StocksTrader(NAME, SIGGEN, SYMBOL);
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
		t.setCurrentQuantity(100);
		Thread.sleep(2000); // wait for persistence writer

		t = DBDAO.getInstance().getTrader(NAME);
		assertNotNull(t);
		assertEquals(Position.LONG, t.getPosition());
		assertEquals(100, t.getCurrentQuantity());

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
