import com.sarxos.medusa.db.DBDAO;
import com.sarxos.medusa.generator.MAVD;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.trader.Trader;

import junit.framework.TestCase;


public class DBDAOTraderTest extends TestCase {

	private static final String NAME = "Buka";
	private static final Symbol SYM = Symbol.KGH;
	private static final SignalGenerator<Quote> SIGGEN = new MAVD(3, 13, 30); 
	
	private DBDAO dbdao = null;
	private Trader trader = null;
	
	public DBDAOTraderTest() {
		this.dbdao = DBDAO.getInstance();
		this.trader = new Trader(NAME, SIGGEN, SYM);
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
			assertEquals(SIGGEN.getClass(), t.getGenerator().getClass());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	
	
}
