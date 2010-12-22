package com.sarxos.gpwnotifier.data.bizzone;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sarxos.gpwnotifier.data.RealTimeDataProvider;
import com.sarxos.gpwnotifier.data.DataProviderException;
import com.sarxos.gpwnotifier.market.Symbol;


/**
 * Bizzone real time data provider.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see http://www.rynek.bizzone.pl/?node=1
 */
public class BizzoneDataProvider implements RealTimeDataProvider {

	@Override
	public double getValue(Symbol symbol) throws DataProviderException {

		if (!canServe(symbol)) {
			String name = getClass().getSimpleName(); 
			throw new DataProviderException(name + " cannot serve data for symbol " + symbol);
		}
		
		String proxy_host = (String)System.getProperties().get("http.proxyHost");
		String proxy_port = (String)System.getProperties().get("http.proxyPort");

		DefaultHttpClient client = new DefaultHttpClient();
		
		if (proxy_host != null && proxy_port != null) {
			System.out.println(proxy_host);
			System.out.println(proxy_port);
			int port = Integer.parseInt(proxy_port);
			HttpHost proxy = new HttpHost(proxy_host, port, "http");
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		HttpResponse response = null;
		HttpEntity entity = null;
		HttpGet get = null;

		String html = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			get = new HttpGet("http://www.rynek.bizzone.pl/?node=1");
			get = affectFirefox(get);
	        response = client.execute(get);
			entity = response.getEntity();
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
		
		String sessid = findSessionID(html);

		try {
			get = new HttpGet("http://www.rynek.bizzone.pl/lib/php/p_glowna.php?sesid=" + sessid);
			get = affectFirefox(get);
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			html = new String(baos.toByteArray());
			entity.consumeContent();
			baos.reset();
		} catch (Exception e) {
			throw new DataProviderException(e);
		}

		String mapping = getMappingForSymbol(symbol);
		
		int p, k;
		
		p = html.indexOf(mapping);
		p = html.indexOf("<td ", p);
		k = html.indexOf("</td>", p);
		
		String part = html.substring(p, k + 1); 
		
		try {
			Pattern pat = Pattern.compile("\\d+\\.\\d+");
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

	private HttpGet affectFirefox(HttpGet get) {
		get.setHeader("Host", "www.rynek.bizzone.pl");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; pl; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8");
        get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        get.setHeader("Accept-Language", "pl,en-us;q=0.7,es;q=0.3");
        get.setHeader("Accept-Charset", "ISO-8859-2,utf-8;q=0.7,*;q=0.7");
        get.setHeader("Keep-Alive", "115");
        get.setHeader("Proxy-Connection", "keep-alive");
        return get;
	}
	
	@Override
	public boolean canServe(Symbol symbol) {
		switch (symbol) {
			case WIG20:
//			case DAX:
//			case BUX:
//			case NASDAQ:
//			case DJI:
//			case SP:
//			case NIKKEI:
				return true;
			default:
				return false;
		}
	}
	
	private String findSessionID(String html) {
		String search = "sesid=";
		int p = html.indexOf(search);
		int k = html.indexOf("\"", p);
		String path = html.substring(p + search.length(), k);
		return path;
	}
	
	private String getMappingForSymbol(Symbol symbol) throws DataProviderException {
		switch (symbol) {
			case WIG20:return "WIG20";
//			case DAX: return "Niemcy-DAX";
//			case BUX: return "Wegry-BUX";
//			case NASDAQ: return "NASDAQ_Co";
//			case DJI: return "USA-DJI";
//			case SP: return "USA-S&P500";
//			case NIKKEI: return "Tokio-Nikkei";
		}
		throw new DataProviderException("No mapping found for symbol " + symbol);
	}
	
	public static void main(String[] args) throws DataProviderException {
		BizzoneDataProvider provider = new BizzoneDataProvider();
		double value = provider.getValue(Symbol.WIG20);
		System.out.println(value);
	}
}
