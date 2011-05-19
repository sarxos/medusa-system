package com.sarxos.medusa.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import com.sarxos.medusa.data.QuotesReaderException;
import com.sarxos.medusa.data.QuotesRemoteReader;
import com.sarxos.medusa.market.Quote;


public class StoqReader<T extends Quote> implements QuotesRemoteReader<T> {

	private Class<T> clazz = null;

	public StoqReader(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public List<T> read(URI uri) throws QuotesReaderException {

		if (uri == null) {
			throw new IllegalArgumentException("URI to read cannot be null");
		}

		String uristr = uri.toString();

		try {

			URL url = uri.toURL();
			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			CSVReader reader = new CSVReader(isr);

			HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<T>();
			strategy.setType(clazz);
			strategy.setColumnMapping(getColumnMapping());
			CsvToBean<T> csv = new CsvToBean<T>();

			List<T> quotes = csv.parse(strategy, reader);

			for (int i = 0; i < quotes.size() - 1; i++) {
				Quote a = quotes.get(i);
				Quote b = quotes.get(i + 1);
				a.setNext(b);
				b.setPrev(a);
			}

			return quotes;

		} catch (MalformedURLException e) {
			String msg = "URI is malformed " + uristr;
			throw new QuotesReaderException(msg, e);
		} catch (IOException e) {
			String msg = "IO exception when reading URI " + uristr;
			throw new QuotesReaderException(msg, e);
		} catch (SecurityException e) {
			throw new QuotesReaderException(e);
		} catch (NoSuchFieldException e) {
			throw new QuotesReaderException(e);
		}
	}

	private Map<String, String> columns = null;

	/**
	 * Return column mapping for CSV.
	 * 
	 * @return Map &lt;String, String&gt;
	 * @throws NoSuchFieldException when any of required fields does not exist
	 *             in the type class
	 * @throws SecurityException in case of security violation
	 */
	public Map<String, String> getColumnMapping() throws SecurityException, NoSuchFieldException {
		if (columns == null) {

			columns = new HashMap<String, String>();

			String name = null;
			String column = null;

			List<Class<? super T>> classes = new LinkedList<Class<? super T>>();
			Class<? super T> c = clazz;

			do {
				classes.add(c);
			} while ((c = c.getSuperclass()) != null);

			for (Class<? super T> cs : classes) {
				Field[] fields = cs.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Field f = fields[i];
					StoqColumn sq = f.getAnnotation(StoqColumn.class);
					if (sq != null) {
						name = f.getName();
						column = sq.value();
						columns.put(column, name);
					}
				}
			}
		}
		return columns;
	}
}
