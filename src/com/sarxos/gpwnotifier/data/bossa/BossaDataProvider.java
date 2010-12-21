package com.sarxos.gpwnotifier.data.bossa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.sarxos.gpwnotifier.data.DataProviderException;
import com.sarxos.gpwnotifier.data.RealTimeDataProvider;
import com.sarxos.gpwnotifier.http.HTTPClient;
import com.sarxos.gpwnotifier.market.Quote;
import com.sarxos.gpwnotifier.market.Symbol;


public class BossaDataProvider implements RealTimeDataProvider {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	@Override
	public boolean canServe(Symbol symbol) {
		return false;
	}

	@Override
	public double getValue(Symbol symbol) throws DataProviderException {
		if (!canServe(symbol)) {
			String name = getClass().getSimpleName(); 
			throw new DataProviderException(name + " cannot serve data for symbol " + symbol);
		}
		return 0;
	}

	/**
	 * @return Return last 6 quotes.
	 * @throws DataProviderException 
	 */
	public Quote[] getLastQuotes(String symbol) throws DataProviderException {

		File f = new File("data/few_last.zip");
		
		try {
			HTTPClient client = HTTPClient.getInstance();
			HttpGet get = new HttpGet("http://bossa.pl/pub/metastock/mstock/sesjaall/few_last.zip");
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			entity.writeTo(new FileOutputStream(f));
		} catch (Exception e) {
			throw new DataProviderException(e);
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
				
				f = new File("data/" + name);
				if (!f.exists()) {
					if (!f.createNewFile()) {
						throw new DataProviderException("Cannot create file " + f.getName());
					}
				}
				
				fos = new FileOutputStream(f);
				while ((i = is.read(bytes)) != -1) {
					fos.write(bytes, 0, i);
				}
				fos.close();
			}
		} catch (Exception e) {
			throw new DataProviderException(e);
		}

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String line = null;
		String[] parts = null;
		
		for (File file : files) {
			try {
				
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				
				while (br.ready()) {
					line = br.readLine();
					if (!line.startsWith(symbol)) {
						parts = line.split(",");
						break;
					}
				}
				
				if (!parts[0].equals(symbol)) {
					throw new DataProviderException(
							"Something with read method - tried to read " + symbol + " " + 
							"but read " + parts[0] + " instead!"
					);
				}
				
				// TODO implement long symbol and short symbol
				
			} catch (Throwable e) {
				throw new DataProviderException(e);
			}
			
		}
		
		
		return null;
	}
	
	public static void main(String[] args) throws DataProviderException {
		BossaDataProvider b = new BossaDataProvider();
		b.getLastQuotes("KGH");
	}
}
