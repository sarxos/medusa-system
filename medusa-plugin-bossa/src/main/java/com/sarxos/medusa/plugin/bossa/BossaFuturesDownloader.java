package com.sarxos.medusa.plugin.bossa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.sarxos.medusa.http.MedusaHttpClient;
import com.sarxos.medusa.http.MedusaHttpException;
import com.sarxos.medusa.market.Symbol;


public class BossaFuturesDownloader {

	/**
	 * Default used date format.
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public boolean download(Symbol symbol, String date) throws MedusaHttpException {
		try {
			return download(Symbol.FW20M11, DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			throw new MedusaHttpException(e);
		}
	}

	public boolean download(Symbol symbol, Date date) throws MedusaHttpException {

		String dstr = DATE_FORMAT.format(date);

		MedusaHttpClient client = new MedusaHttpClient();

		ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("today", "off"));
		nvps.add(new BasicNameValuePair("backUrl", "$backUrl"));
		nvps.add(new BasicNameValuePair("data", dstr));
		nvps.add(new BasicNameValuePair("nazwa", symbol.getName()));
		nvps.add(new BasicNameValuePair("format", "mst"));
		nvps.add(new BasicNameValuePair("time_period_l", "tick"));
		nvps.add(new BasicNameValuePair("time_period", "0"));
		nvps.add(new BasicNameValuePair("send", "on"));

		HttpPost post = new HttpPost("http://bossa.pl/index.jsp?layout=intraday&page=2&news_cat_id=80&zakladka=Kontrakty&today=off");
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
		} catch (UnsupportedEncodingException e) {
			throw new MedusaHttpException(e);
		}

		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (Exception e) {
			throw new MedusaHttpException("Cannot execute POST call");
		} finally {
			if (response != null) {
				HttpEntity entity = response.getEntity();
				try {
					File f = new File(symbol + "." + dstr + ".prn");
					File p = f.getParentFile();
					if (!p.exists()) {
						p.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(f);
					entity.writeTo(fos);
				} catch (IOException e) {
					throw new MedusaHttpException(e);
				}
				try {
					entity.getContent().close();
				} catch (Exception e) {
					throw new MedusaHttpException(e);
				}
			}
		}

		// today=off&
		// backUrl=%24backUrl&
		// data=2011-05-05&
		// nazwa=FW20M11&
		// format=mst&
		// time_period_l=tick&
		// time_period=0&send=on

		return false;
	}

	public static void main(String[] args) throws MedusaHttpException {
		BossaFuturesDownloader d = new BossaFuturesDownloader();
		d.download(Symbol.FW20M11, "2011-05-05");
	}
}
