package com.sarxos.medusa.trader;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.trader.Observer.NullEvent;
import com.sarxos.medusa.trader.Observer.State;


public class ObserverTest {

	private static class TestProvider implements RealTimeProvider {

		double price = 100;

		@Override
		public Quote getQuote(Symbol symbol) throws ProviderException {
			double p = price += 100;
			return new Quote(new Date(), 90, p, 90, p, 2000);
		}

		@Override
		public boolean canServe(Symbol symbol) {
			return symbol == Symbol.QQQ;
		}
	}

	private static class TestPriceListener2 implements PriceListener {

		@Override
		public void priceChange(PriceEvent pe) {
			System.out.println(pe);
		}
	}

	@Test
	public void test_interval() {
		Observer o = new Observer(Symbol.QQQ);
		int interval = 2; // 2 seconds
		o.setInterval(interval);
		Assert.assertEquals(interval, o.getInterval());
		try {
			o.setInterval(-10); // time interval cannot be negative
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void test_start() throws InterruptedException {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		o.setInterval(1); // 1 second
		o.start();
		Thread.sleep(100);
		Assert.assertEquals(State.RUNNIG, o.getState());
		o.stop();
	}

	@Test
	public void test_pause() {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		o.setInterval(1); // 1 second
		o.start();
		o.pause();
		Assert.assertEquals(State.PAUSED, o.getState());
		o.stop();
	}

	@Test
	public void test_resume() {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		o.setInterval(1); // 1 second
		o.start();
		o.pause();
		o.resume();
		Assert.assertEquals(State.RUNNIG, o.getState());
		o.stop();
	}

	@Test
	public void test_states() throws InterruptedException {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		o.setInterval(1); // 1 second
		o.start();
		o.pause();
		Assert.assertEquals(-1.0, o.getPrice());
		Thread.sleep(2000);
		Assert.assertEquals(-1.0, o.getPrice());
		o.resume();
		Thread.sleep(2000);
		Assert.assertTrue(o.getPrice() > 0);
		o.stop();
	}

	@Test
	public void test_getPrice() throws InterruptedException {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		o.setInterval(1); // 1 second
		Assert.assertEquals(-1.0, o.getPrice());
		o.start();
		Thread.sleep(500);
		Assert.assertEquals(200.0, o.getPrice());
		o.stop();
	}

	@Test
	public void test_provider() {
		RealTimeProvider p = new TestProvider();
		Observer o = new Observer(Symbol.QQQ, p);
		Assert.assertSame(p, o.getProvider());
	}

	@Test
	public void test_provider2() {
		Observer o = new Observer(Symbol.QQQ);
		try {
			o.setProvider(new TestProvider());
		} catch (IllegalArgumentException e) {
			Assert.fail("There should be no exception here");
		}
		o = new Observer(Symbol.BRE);
		try {
			o.setProvider(new TestProvider());
			Assert.fail("There should be exception here");
		} catch (IllegalArgumentException e) {
			System.out.println("Expected exception: " + e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_provider3() {
		RealTimeProvider p = new TestProvider();
		Observer o = new Observer(Symbol.QQQ, p);
		o.observe(Symbol.BRE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_provider4() {
		RealTimeProvider p = new RealTimeProvider() {

			@Override
			public Quote getQuote(Symbol symbol) throws ProviderException {
				return null;
			}

			@Override
			public boolean canServe(Symbol symbol) {
				return false;
			}
		};
		new Observer(Symbol.BRE, p);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_provider5() {
		RealTimeProvider p = new RealTimeProvider() {

			@Override
			public Quote getQuote(Symbol symbol) throws ProviderException {
				return null;
			}

			@Override
			public boolean canServe(Symbol symbol) {
				return false;
			}
		};
		Observer o = new Observer(Symbol.BRE);
		o.setProvider(p);
	}

	@Test
	public void test_observe() {
		RealTimeProvider p = new TestProvider();
		Observer o = new Observer(Symbol.QQQ, p);
		try {
			o.observe(Symbol.QQQ);
		} catch (IllegalArgumentException e) {
			Assert.fail("There should be no exception here");
		}
		try {
			o.observe(Symbol.BRE);
			Assert.fail("There should be exception here");
		} catch (IllegalArgumentException e) {
			System.out.println("Expected exception: " + e.getMessage());
		}
	}

	@Test
	public void test_symbol() {
		RealTimeProvider p = new TestProvider();
		Observer o = new Observer(Symbol.QQQ, p);
		Assert.assertSame(Symbol.QQQ, o.getSymbol());
	}

	@Test
	public void test_listeners() {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		PriceListener li = new TestPriceListener2();
		boolean added = o.addPriceListener(li);
		Assert.assertTrue(added);
		PriceListener[] pl = o.getPriceListeners();
		Assert.assertEquals(1, pl.length);
		Assert.assertSame(li, pl[0]);
		added = o.addPriceListener(li);
		Assert.assertFalse(added);
		pl = o.getPriceListeners();
		Assert.assertEquals(1, pl.length);
		li = new TestPriceListener2();
		added = o.addPriceListener(li);
		Assert.assertTrue(added);
		pl = o.getPriceListeners();
		Assert.assertEquals(2, pl.length);
		Assert.assertSame(li, pl[1]);
		o.removePriceListener(pl[0]);
		pl = o.getPriceListeners();
		Assert.assertEquals(1, pl.length);
		o.removePriceListener(pl[0]);
		pl = o.getPriceListeners();
		Assert.assertEquals(0, pl.length);
	}

	@Test
	public void test_runner() {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		Thread r = o.getRunner();
		Assert.assertNotNull(r);
		Thread r2 = o.getRunner();
		Assert.assertSame(r, r2);
	}

	private static class TestPriceListener implements PriceListener {

		public boolean nullz = false;

		@Override
		public void priceChange(PriceEvent pe) {
			if (pe instanceof NullEvent) {
				nullz = true;
			}
		}
	}

	@Test
	public void test_nullEvent() throws InterruptedException {
		Observer o = new Observer(Symbol.QQQ, new TestProvider());
		TestPriceListener list = new TestPriceListener();
		o.addPriceListener(list);
		o.setInterval(1); // 1 second
		o.start();
		Thread.sleep(500);
		o.setProvider(new RealTimeProvider() {

			// should cause null-event
			@Override
			public Quote getQuote(Symbol symbol) throws ProviderException {
				return null;
			}

			@Override
			public boolean canServe(Symbol symbol) {
				return true;
			}
		});
		Thread.sleep(1000);
		o.stop();
		Assert.assertTrue(list.nullz);
	}
}
