package com.sarxos.medusa.plugin.bossa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.http.MedusaHttpClient;
import com.sarxos.medusa.http.MedusaHttpException;
import com.sarxos.medusa.market.Calendarium;
import com.sarxos.medusa.market.Symbol;


public class BossaFuturesDownloader {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BossaFuturesDownloader.class.getSimpleName());

	/**
	 * Default used date format.
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Download future inraday quotes for given symbol. Quotes are in Metastock
	 * format with tick as period unit.
	 * 
	 * @param symbol - future symbol to download quotes for
	 * @param date - trading date
	 * @return Will return PRN file with quotes in Metastock format
	 * @throws MedusaHttpException
	 */
	public File download(Symbol symbol, String date) throws MedusaHttpException {
		try {
			return download(Symbol.FW20M11, DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			throw new MedusaHttpException(e);
		}
	}

	/**
	 * Download future inraday quotes for given symbol. Quotes are in Metastock
	 * format with tick as period unit.
	 * 
	 * @param symbol - future symbol to download quotes for
	 * @param date - trading date
	 * @return Will return PRN file with quotes in Metastock format
	 * @throws MedusaHttpException
	 */
	public File download(Symbol symbol, Date date) throws MedusaHttpException {

		String dstr = DATE_FORMAT.format(date);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Downloading " + symbol + " quotes for " + dstr);
		}

		MedusaHttpClient client = new MedusaHttpClient();

		ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("today", "off"));
		nvps.add(new BasicNameValuePair("backUrl", "$backUrl"));
		nvps.add(new BasicNameValuePair("data", dstr));
		nvps.add(new BasicNameValuePair("nazwa", symbol.getName()));
		nvps.add(new BasicNameValuePair("format", "mst"));
		nvps.add(new BasicNameValuePair("time_period_l", "secs"));
		nvps.add(new BasicNameValuePair("time_period", "1"));
		nvps.add(new BasicNameValuePair("send", "on"));

		HttpPost post = new HttpPost("http://bossa.pl/index.jsp?layout=intraday&page=2&news_cat_id=80&zakladka=Kontrakty&today=off");
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
		} catch (UnsupportedEncodingException e) {
			throw new MedusaHttpException(e);
		}

		File file = null;
		HttpResponse response = null;
		try {
			response = client.execute(client.affect(post));
		} catch (Exception e) {
			throw new MedusaHttpException("Cannot execute POST call");
		} finally {

			if (response != null) {

				file = new File(getFileName(symbol, dstr));

				File p = file.getParentFile();
				if (!p.exists()) {
					p.mkdirs();
				}

				FileOutputStream fos = null;
				InputStream is = null;
				try {
					fos = new FileOutputStream(file);
					is = client.ungzip(response);

					int k = symbol.getName().length();
					byte[] bytes = new byte[k];
					if (is.markSupported()) {
						is.mark(k + 1);
						is.read(bytes);
						if (symbol.getName().equals(new String(bytes))) {
							is.reset();
							byte[] buffer = new byte[32768];
							int n = 0;
							while ((n = is.read(buffer)) != -1) {
								fos.write(buffer, 0, n);
							}
						} else {
							LOG.warn("No " + symbol + " intraday quotes found for " + dstr);
							return null;
						}
					} else {
						throw new MedusaHttpException("Unsupported stream (should be markable)");
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		return file;
	}

	protected String getFileName(Symbol symbol, String dstr) {
		return "data/tmp/futures/" + symbol + "." + dstr + ".prn";
	}

	public void download(Symbol symbol, String from, String to) throws MedusaHttpException {

		Date dfrom = null;
		Date dto = null;
		try {
			dfrom = DATE_FORMAT.parse(from);
			dto = DATE_FORMAT.parse(to);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendarium calendarium = Calendarium.getInstance();

		Date date = dfrom;

		if (calendarium.isFreeDay(date)) {
			date = calendarium.getNextWorkingDay(date);
		}

		if (date.getTime() > dto.getTime()) {
			LOG.warn("Incorrect time interval was provided");
		}

		while (date.getTime() <= dto.getTime()) {
			File f = new File(getFileName(symbol, DATE_FORMAT.format(date)));
			if (!f.exists()) {
				f = download(symbol, date);
			}
			date = calendarium.getNextWorkingDay(date);
		}
	}

	public static void main(String[] args) throws MedusaHttpException {
		BossaFuturesDownloader d = new BossaFuturesDownloader();
		d.download(Symbol.FW20M11, "2011-04-06", "2011-05-16");
	}
}
