package com.sarxos.medusa.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.FakeRealTimeProvider;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.medusa.util.DateUtils;


public class FakeRealTimeProviderTest {

	private static final Configuration CFG = Configuration.getInstance();
	private static final Symbol SYMBOL = Symbol.FW20M11;
	private static final String PRN = "com/sarxos/medusa/sim/FW20M11.prn";

	@BeforeClass
	public static void init() throws IOException {

		CFG.setProperty("core", "tmpdir", "test/tmp");
		CFG.setProperty("data", "history", "com.sarxos.medusa.sim.FakeHistoryProvider");

		File test = new File(CFG.getProperty("core", "tmpdir") + "/intraday/FW20M11.prn");
		FileUtils.touch(test);

		FileOutputStream fos = FileUtils.openOutputStream(test);
		InputStream is = CFG.getClass().getClassLoader().getResourceAsStream(PRN);

		IOUtils.copy(is, fos);
		IOUtils.closeQuietly(is);
		IOUtils.closeQuietly(fos);
	}

	@AfterClass
	public static void cleanup() throws IOException {

		File test = new File(CFG.getProperty("core", "tmpdir") + "/intraday/FW20M11.prn");

		if (!FileUtils.deleteQuietly(test)) {
			FileUtils.forceDeleteOnExit(test);
		}

		File dir = new File("test");
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			if (!FileUtils.deleteQuietly(dir)) {
				FileUtils.forceDeleteOnExit(dir);
			}
		}
	}

	@Test
	public void test_getQuote() throws ProviderException {
		Date start = DateUtils.fromCGL("20100621");
		Date end = DateUtils.fromCGL("20100623");

		FakeRealTimeProvider sp = new FakeRealTimeProvider(SYMBOL, start, end);

		for (int i = 0; !sp.isReached(); i++) {
			Quote q = sp.getQuote(SYMBOL);
			// System.out.println(i + " " + q);
			if (i < 5) {
				Assert.assertNotNull(q);
			} else {
				Assert.assertNull(q);
			}
		}
	}

	@Test
	public void test_getQuote2() throws ProviderException {
		Date start = DateUtils.fromCGL("20100623");
		FakeRealTimeProvider sp = new FakeRealTimeProvider(SYMBOL, start, null);
		for (int i = 0; !sp.isReached(); i++) {
			Quote q = sp.getQuote(SYMBOL);
			// System.out.println(i + " " + q);
			if (i < 4) {
				Assert.assertNotNull(q);
			} else {
				Assert.assertNull(q);
			}
		}
	}
}
