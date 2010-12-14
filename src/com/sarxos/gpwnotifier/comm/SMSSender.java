package com.sarxos.gpwnotifier.comm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.sarxos.gpwnotifier.data.DataProviderException;

public class SMSSender {

	public static void main(String[] args) throws Exception {



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
			get = new HttpGet("http://www.orange.pl/");
			get.setHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			html = new String(baos.toByteArray());
			entity.consumeContent();
			baos.reset();
		} catch (Exception e) {
			throw new DataProviderException(e);
		}		
		
		try {
			get = new HttpGet("http://www.orange.pl/start.phtml");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
			response = client.execute(get);
			entity = response.getEntity();
			entity.writeTo(baos);
			html = new String(baos.toByteArray());
			entity.consumeContent();
			baos.reset();
		} catch (Exception e) {
			throw new DataProviderException(e);
		}		
		
		// https://www.orange.pl/start.phtml?_DARGS=/gear/infoportal/header/user-box.jsp
		
		HttpPost post = new HttpPost("https://www.orange.pl/zaloguj.phtml?_DARGS=/gear/static/signInLoginBox.jsp");

		post.setHeader("Host", "www.orange.pl");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.375.99 Safari/533.4");
		post.setHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Referer", "http://www.orange.pl/zaloguj.phtml");
		post.setHeader("Origin", "http://www.orange.pl");

		post.getParams().setBooleanParameter("http.protocol.expect-continue", false);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("_dyncharset", "UTF-8"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.loginErrorURL", "http://www.orange.pl/zaloguj.phtml"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.loginErrorURL", "509934614"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.login", "Ttxdtd7"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.login", ""));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.password", "Ttxdtd7"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.value.password", ""));
		
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.login.x", "0"));
		nvps.add(new BasicNameValuePair("/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.login.y", "0"));
		nvps.add(new BasicNameValuePair("_D:/amg/ptk/map/core/formhandlers/AdvancedProfileFormHandler.login", ""));
		
		nvps.add(new BasicNameValuePair("_DARGS", "/gear/static/signInLoginBox.jsp"));
		
		UrlEncodedFormEntity reqentity = new UrlEncodedFormEntity(nvps);

		post.setEntity(reqentity);
		response = client.execute(post);

		Header location = response.getHeaders("Location")[0];
		
//		Header[] headers = response.getAllHeaders();
//		for (int i = 0; i < headers.length; i++) {
//			System.out.println(headers[i]);
//		}

//		System.out.println(response.getStatusLine());
		//System.out.println(response.getEntity().toString());
	
		System.out.println(location);
		
		entity = response.getEntity();
		entity.writeTo(baos);
		html = new String(baos.toByteArray());
		entity.consumeContent();
		baos.reset();

		System.out.println(html);

		//		try {
		//			get = new HttpGet("http://www.rynek.bizzone.pl/?node=1");
		//			get = affectFirefox(get);
		//	        response = client.execute(get);
		//			entity = response.getEntity();
		//			entity.writeTo(baos);
		//			html = new String(baos.toByteArray());
		//			entity.consumeContent();
		//			baos.reset();
		//		} catch (Exception e) {
		//			throw new Exception(e);
		//		}
		//
		//		if (html == null || "".equals(html)) {
		//			throw new Exception("Cannot read page data - empty response received");
		//		}

	}

	private static HttpGet affectFirefox(HttpGet get) {
		get.setHeader("Host", "www.rynek.bizzone.pl");
		get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; pl; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8");
		get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader("Accept-Language", "pl,en-us;q=0.7,es;q=0.3");
		get.setHeader("Accept-Charset", "ISO-8859-2,utf-8;q=0.7,*;q=0.7");
		get.setHeader("Keep-Alive", "115");
		get.setHeader("Proxy-Connection", "keep-alive");
		return get;
	}

}
