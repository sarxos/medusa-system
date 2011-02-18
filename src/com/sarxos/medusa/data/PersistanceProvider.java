package com.sarxos.medusa.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;


public class PersistanceProvider {

	public static String marshalGenParams(SignalGenerator<Quote> siggen) {

		Map<String, String> params = siggen.getParameters();

		Set<Entry<String, String>> entries = params.entrySet();
		Iterator<Entry<String, String>> ei = entries.iterator();
		Entry<String, String> entry = null;

		StringBuffer sb = new StringBuffer();

		while (ei.hasNext()) {
			entry = ei.next();
			String key = entry.getKey();
			String val = entry.getValue().toString();
			sb.append(key).append(':').append(val).append(';');
		}

		return sb.toString();
	}

	public static Map<String, String> unmarshalGenParams(String str) {

		Map<String, String> map = new HashMap<String, String>();

		String[] entries = str.split(";");
		String entry = null;

		for (int i = 0; i < entries.length; i++) {
			entry = entries[i];
			if (entry != null && entry.length() > 0) {
				String[] kv = entry.split(":");
				if (kv.length != 2) {
					continue;
				}
				map.put(kv[0], kv[1]);
			}
		}

		return map;
	}
}
