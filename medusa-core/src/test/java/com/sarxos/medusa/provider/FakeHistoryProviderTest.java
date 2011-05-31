package com.sarxos.medusa.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.FakeHistoryProvider;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.util.Configuration;


public class FakeHistoryProviderTest {

	private static final Configuration CFG = Configuration.getInstance();
	private static final Symbol SYMBOL = Symbol.FW20M11;
	private static final String PRN = "com/sarxos/medusa/sim/FW20M11.prn";

	@BeforeClass
	public static void init() throws IOException {

		CFG.setProperty("core", "tmpdir", "test/tmp");

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
		File dir = new File("test");
		if (!FileUtils.deleteQuietly(dir)) {
			FileUtils.forceDeleteOnExit(dir);
		}
	}

	@Test
	public void test_getIntradayQuotes() throws ProviderException {

		FakeHistoryProvider fhp = new FakeHistoryProvider();
		QuotesIterator<Quote> qi = fhp.getIntradayQuotes(SYMBOL);

		Assert.assertEquals(9, qi.collection().size());
	}
}
