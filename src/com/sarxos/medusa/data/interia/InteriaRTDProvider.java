package com.sarxos.medusa.data.interia;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.sarxos.medusa.data.DataProviderException;
import com.sarxos.medusa.data.RealTimeDataProvider;
import com.sarxos.medusa.http.HTTPClient;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


public class InteriaRTDProvider implements RealTimeDataProvider {

	private String[] symbols = new String[] {
			"KGH", "BRE"
	};
	
	private Map<String, Boolean> map = new HashMap<String, Boolean>();
	
	public InteriaRTDProvider() {
		for (int i = 0; i < symbols.length; i++) {
			map.put(symbols[i], Boolean.TRUE);
		}
	}
	
	@Override
	public boolean canServe(Symbol symbol) {
		return map.get(symbol.toString()) == Boolean.TRUE;
	}

	@Override
	public Quote getQuote(Symbol symbol) throws DataProviderException {
		
		if (!canServe(symbol)) {
			String name = getClass().getSimpleName(); 
			throw new DataProviderException(name + " cannot serve data for symbol " + symbol);
		}
		
		HTTPClient client = HTTPClient.getInstance();
		
		String html = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			HttpGet get = new HttpGet("http://mojeinwestycje.interia.pl/gie/notgpw/notc/c_akcje");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			entity.writeTo(baos);
			html = new String(baos.toByteArray());
			entity.consumeContent();
			baos.reset();
			
		} catch (Exception e) {
			throw new DataProviderException(e);
		}

		if (html == null || "".equals(html)) {
			throw new DataProviderException("Cannot read page data - empty response received");
		}

		int p, k;
		String fragment = null;
		String part = null;
		
		k = html.indexOf("(" + symbol + ")");
		
		p = html.indexOf("<td ", k);
		k = html.indexOf("</td>", p);
		double odnies = readValue(html.substring(p, k));
		
		p = html.indexOf("<td ", k);
		k = html.indexOf("</td>", p);
		double otw = readValue(html.substring(p, k));
		
		p = html.indexOf("<td ", k);
		k = html.indexOf("</td>", p);
		double max = readValue(html.substring(p, k));
		
		p = html.indexOf("<td ", k);
		k = html.indexOf("</td>", p);
		double min = readValue(html.substring(p, k));
		
		p = html.indexOf("<td ", k);
		k = html.indexOf("</td>", p);
		double ost = readValue(html.substring(p, k));

		p = html.indexOf("<td ", k);
		k = html.indexOf("</td>", p);
		// percentage change in the table - omit this one
		
		p = html.indexOf("<td ", k);
		k = html.indexOf("</td>", p);
		double wol = readValue(html.substring(p, k));

		return new Quote(new Date(), otw, max, min, ost, (long)wol);
	}

	private double readValue(String fragment) throws DataProviderException {
		
		String part = fragment.replaceAll(",", ".");
		part = part.replaceAll("(\\B\\s\\B)|(&nbsp;)", "");
		
		try {
			Pattern pat = Pattern.compile("\\d+\\.?\\d+?");
			Matcher matcher = pat.matcher(part);
			if (matcher.find()) {
				part = matcher.group();
			} else {
				part = "";
			}
		} catch (Exception e) {
			throw new DataProviderException("Matching exception", e);
		}
		
		if ("".equals(part)) {
			throw new DataProviderException("Value for symbol not found in [" + part + "]");
		}

		double value = 0;
		try {
			value = Double.valueOf(part);
		} catch (NumberFormatException e) {
			throw new DataProviderException("Cannot parse - wrong fragment obtained (" + part + ")", e); 
		}
		
		return value;
	}
	
	public static void main(String[] args) throws DataProviderException {
		
		InteriaRTDProvider idp = new InteriaRTDProvider();
		
		System.out.println(idp.getQuote(Symbol.KGH));
		
	}
	
}
