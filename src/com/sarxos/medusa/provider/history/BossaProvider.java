package com.sarxos.medusa.provider.history;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.HistoryProvider;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.util.DateUtils;
import com.sarxos.smesx.http.NaiveSSLClient;


public class BossaProvider implements HistoryProvider {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	@Override
	public List<Quote> getLastQuotes(Symbol symbol) throws ProviderException {

		boolean download = true;

		File f = new File("data/tmp/few_last.zip");

		if (f.exists()) {
			Date modified = new Date(f.lastModified());
			if (DateUtils.isToday(modified)) {
				download = false;
			}
		}

		if (download) {
			try {
				NaiveSSLClient client = NaiveSSLClient.getInstance();
				HttpGet get = new HttpGet("http://bossa.pl/pub/metastock/mstock/sesjaall/few_last.zip");

				synchronized (client) {
					HttpResponse response = client.execute(get);
					HttpEntity entity = response.getEntity();
					entity.writeTo(new FileOutputStream(f));
					entity.getContent().close();
				}
			} catch (Exception e) {
				throw new ProviderException(e);
			}
		}

		List<File> files = new LinkedList<File>();

		ZipEntry entry = null;
		String name = null;

		FileOutputStream fos = null;
		InputStream is = null;

		int i = -1;
		byte[] bytes = new byte[1024];

		try {

			ZipFile zip = new ZipFile(f);

			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {

				entry = entries.nextElement();
				name = entry.getName();

				if (!name.matches("\\d+\\.prn")) {
					continue;
				}

				is = zip.getInputStream(entry);

				f = new File("data/tmp/" + name);
				if (!f.exists()) {
					if (!f.createNewFile()) {
						throw new ProviderException("Cannot create file " + f.getName());
					}
				}

				fos = new FileOutputStream(f);
				while ((i = is.read(bytes)) != -1) {
					fos.write(bytes, 0, i);
				}
				fos.close();

				files.add(f);
			}
		} catch (Exception e) {
			throw new ProviderException(e);
		}

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String line = null;
		String[] parts = null;

		Date date = null;
		double open = 0;
		double high = 0;
		double low = 0;
		double close = 0;
		long volume = 0;

		List<Quote> quotes = new LinkedList<Quote>();

		for (File file : files) {
			try {

				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);

				while (br.ready()) {
					line = br.readLine();
					if (line.startsWith(symbol.getName())) {
						parts = line.split(",");
						break;
					}
				}

				// <TICKER>,<DTYYYYMMDD>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>
				// KGHM,20101220,156.70,158.20,155.00,157.90,394584

				if (parts.length < 7) {
					throw new ProviderException(
							"Something is wrong with data - should be 7 elements, " +
							"found " + parts.length + " instead!");
				}
				if (!parts[0].equals(symbol.getName())) {
					throw new ProviderException(
							"Something is wrong with read method - tried to read " + symbol + " " +
							"but read " + parts[0] + " instead!");
				}

				date = DATE_FORMAT.parse(parts[1]);
				open = Double.valueOf(parts[2]);
				high = Double.valueOf(parts[3]);
				low = Double.valueOf(parts[4]);
				close = Double.valueOf(parts[5]);
				volume = Long.valueOf(parts[6]);

				quotes.add(new Quote(date, open, high, low, close, volume));

			} catch (Throwable e) {
				throw new ProviderException(e);
			}

		}

		return quotes;
	}

	@Override
	public List<Quote> getAllQuotes(Symbol symbol) throws ProviderException {

		boolean download = true;

		File f = new File("data/tmp/mstcgl.zip");

		if (f.exists()) {
			Date modified = new Date(f.lastModified());
			if (DateUtils.isToday(modified)) {
				download = false;
			}
		}

		if (download) {
			try {
				NaiveSSLClient client = NaiveSSLClient.getInstance();
				HttpGet get = new HttpGet("http://bossa.pl/pub/metastock/cgl/mstcgl.zip");
				synchronized (client) {
					HttpResponse response = client.execute(get);
					HttpEntity entity = response.getEntity();
					entity.writeTo(new FileOutputStream(f));
				}
			} catch (Exception e) {
				throw new ProviderException(e);
			}
		}

		File file = null;

		ZipEntry entry = null;
		String name = null;

		FileOutputStream fos = null;
		InputStream is = null;

		int i = -1;
		byte[] bytes = new byte[32 * 1024];

		try {

			File sessall = new File("data/tmp/mstcgl");
			if (!sessall.exists()) {
				if (!sessall.mkdirs()) {
					throw new ProviderException("Cannot create directory " + f.getName());
				}
			}

			ZipFile zip = new ZipFile(f);

			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {

				entry = entries.nextElement();
				name = entry.getName();

				if (!name.matches(symbol.getName() + "\\.mst")) {
					continue;
				}

				is = zip.getInputStream(entry);

				f = new File("data/tmp/mstcgl/" + name);
				if (!f.exists()) {
					if (!f.createNewFile()) {
						throw new ProviderException("Cannot create file " + f.getName());
					}
				}

				fos = new FileOutputStream(f);
				while ((i = is.read(bytes)) != -1) {
					fos.write(bytes, 0, i);
				}
				fos.close();

				file = f;
				break;
			}
		} catch (Exception e) {
			throw new ProviderException(e);
		}

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String line = null;
		String[] parts = null;

		Date date = null;
		double open = 0;
		double high = 0;
		double low = 0;
		double close = 0;
		long volume = 0;

		List<Quote> quotes = new LinkedList<Quote>();

		try {

			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);

			while (br.ready()) {

				line = br.readLine();
				parts = line.split(",");

				// <TICKER>,<DTYYYYMMDD>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>
				// KGHM,20101220,156.70,158.20,155.00,157.90,394584

				// headers line
				if (parts[0].startsWith("<TICKER>")) {
					continue;
				}

				if (parts.length < 7) {
					throw new ProviderException(
							"Something is wrong with data - should be 7 elements, " +
							"found " + parts.length + " instead!");
				}
				if (!parts[0].equals(symbol.getName())) {
					throw new ProviderException(
							"Something is wrong with read method - tried to read " + symbol + " " +
							"but read " + parts[0] + " instead!");
				}

				date = DATE_FORMAT.parse(parts[1]);
				open = Double.valueOf(parts[2]);
				high = Double.valueOf(parts[3]);
				low = Double.valueOf(parts[4]);
				close = Double.valueOf(parts[5]);
				volume = Long.valueOf(parts[6]);

				quotes.add(new Quote(date, open, high, low, close, volume));
			}

		} catch (Throwable e) {
			throw new ProviderException(e);
		}

		return quotes;
	}

	public static void main(String[] args) throws ProviderException {
		BossaProvider b = new BossaProvider();
		List<Quote> quotes = b.getAllQuotes(Symbol.KGH);
		for (int i = 0; i < quotes.size(); i++) {
			System.out.println(quotes.get(i));
		}
	}
}
