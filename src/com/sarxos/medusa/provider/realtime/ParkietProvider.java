package com.sarxos.medusa.provider.realtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cyberneko.html.parsers.DOMParser;
import org.json.parser.JSONArray;
import org.json.parser.JSONException;
import org.json.parser.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sarxos.medusa.market.BidAsk;
import com.sarxos.medusa.market.Calendarium;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.util.Configuration;


/**
 * Real time data provider for parkiet.com shareware service. This provider can
 * serve all kind of stock symbol data.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see http://www.parkiet.com
 */
public class ParkietProvider implements RealTimeProvider {

	/**
	 * Quotes updater. Once obtained quote will be stored in the quotation map
	 * and will be updated periodically with the 15s interval.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	private class QuotesUpdater implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Update quotes in the quotation map.
		 * 
		 * @throws ProviderException
		 */
		private void update() throws ProviderException {

			Calendarium c = Calendarium.getInstance();
			if (!c.isMarketOpen()) {
				System.out.println("omit update");
				return;
			}

			String date = TIME_FORMAT.format(last);
			String update = String.format(UPDATE_URL, topicID, componentID, date);

			last = new Date();

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("topicId", topicID));
			pairs.add(new BasicNameValuePair("componentId", componentID));
			pairs.add(new BasicNameValuePair("czas", date));

			HttpUriRequest post = affectFirefox(new HttpPost(update));
			try {
				((HttpPost) post).setEntity(new UrlEncodedFormEntity(pairs));
			} catch (UnsupportedEncodingException e) {
				throw new ProviderException(e);
			}

			JSONArray array = null;
			try {
				array = new JSONArray(execute(post));
			} catch (JSONException e) {
				throw new ProviderException(e);
			}

			JSONObject o = null;
			String name = null;
			int n = array.length();

			try {

				for (int i = 0; i < n; i++) {

					o = (JSONObject) array.get(i);

					name = o.optString("nazwa");
					if (name == null || name.length() == 0) {
						// different kind of JSON object
						continue;
					}

					name = name.trim();

					Symbol symbol = Symbol.valueOfName(name);
					if (symbol != null && quotations.containsKey(symbol)) {
						Quote q = jsonToQuote(o);
						quotations.get(symbol).copyFrom(q);
					}
				}
			} catch (JSONException e) {
				throw new ProviderException(e);
			}
		}
	}

	/**
	 * Parkiet.com service URL.
	 */
	public static final String PARKIET_URL = "http://www.parkiet.com";

	/**
	 * Single quote bid/ask main address.
	 */
	public static final String BID_ASK_URL = "http://www.parkiet.com/OknoOfert.html?wykres=false&opoz=false&refresh=60&isin=";

	/**
	 * Cyclic update URL.
	 */
	public static final String UPDATE_URL = "http://www.parkiet.com/Quotations?topicId=%s&componentId=%s&czas=%s";

	/**
	 * Time format in service quotes.
	 */
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");

	/**
	 * Date format in service quotes.
	 */
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	/**
	 * Configuration instance.
	 */
	private static final Configuration CFG = Configuration.getInstance();

	/**
	 * HTTP client.
	 */
	private final DefaultHttpClient client;

	/**
	 * Parkiet.com user's name.
	 */
	private String userName = null;

	/**
	 * Parkiet.com user's password.
	 */
	private String password = null;

	/**
	 * Is user logged in.
	 */
	private boolean loggedIn = false;

	/**
	 * Default DOM parser.
	 */
	private final DOMParser parser = new DOMParser();

	/**
	 * Default XPath factory.
	 */
	private final XPathFactory factory = XPathFactory.newInstance();

	/**
	 * XPtah expressions mapping.
	 */
	private final Map<String, XPathExpression> expressions = new HashMap<String, XPathExpression>();

	/**
	 * Quotes list main address.
	 */
	private String address = null;

	/**
	 * Received HTML length.
	 */
	private int length = 0;

	/**
	 * DOM parsed from HTML.
	 */
	private Document dom = null;

	/**
	 * Quotes cache.
	 */
	private Map<Symbol, Quote> quotations = new HashMap<Symbol, Quote>();

	/**
	 * Last update date.
	 */
	private Date last = null;

	/**
	 * Topic ID obtained from service (internal logic). This field is critical
	 * for receiving and updating quotes.
	 */
	private String topicID = null;

	/**
	 * Component ID obtained from service (internal logic). This field is
	 * critical for receiving and updating quotes.
	 */
	private String componentID = null;

	/**
	 * Quotes updater.
	 */
	private QuotesUpdater updater = new QuotesUpdater();

	/**
	 * Constructor.
	 */
	public ParkietProvider() {

		client = new DefaultHttpClient();

		// proxy

		String phost = (String) System.getProperties().get("http.proxyHost");
		String pport = (String) System.getProperties().get("http.proxyPort");

		if (phost == null && pport == null) {
			phost = CFG.getProperty("core", "proxy_host");
			pport = CFG.getProperty("core", "proxy_port");
		}

		if (phost != null && pport != null) {

			int port = -1;
			try {
				port = Integer.parseInt(pport);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Incorrect proxy port '" + pport + "'", e);
			}

			HttpHost proxy = new HttpHost(phost, port, "http");
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		// parser features

		try {
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", true);
			parser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-comment-delims", true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// date settings

		last = new Date();

		Calendar c = new GregorianCalendar();
		c.setTime(last);
		c.set(Calendar.HOUR_OF_DAY, 8);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		last = c.getTime();

		// quotes updater

		Thread runner = new Thread(updater);
		runner.setDaemon(true);
		runner.start();
	}

	/**
	 * Parse input HTML with cyberneko parser and return DOM document.
	 * 
	 * @param html - input HTML string
	 * @return DOM document
	 * @throws ProviderException
	 */
	protected Document parseHTML(String html) throws ProviderException {

		if (html == null) {
			throw new IllegalArgumentException("HTML to parse cannot be null");
		}

		if (length != html.length() || dom == null) {

			ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
			InputSource is = new InputSource(bais);

			try {
				parser.parse(is);
			} catch (Exception e) {
				throw new ProviderException("Cannot parse HTML", e);
			}

			length = html.length();
			dom = parser.getDocument();
		}

		return dom;
	}

	/**
	 * Return cached {@link XPathExpression} or create new one and put in the
	 * cache map if not found. In the next call of this method cached expression
	 * will be returned.
	 * 
	 * @param expr - expression to compile
	 * @return New one or cached expression
	 * @throws ProviderException
	 */
	protected XPathExpression getExpression(String expr) throws ProviderException {

		if (expr == null) {
			throw new IllegalArgumentException("XPath expression to compile cannot be null");
		}

		XPathExpression expression = expressions.get(expr);

		if (expression == null) {
			XPath xpath = factory.newXPath();
			try {
				expression = xpath.compile(expr);
			} catch (XPathExpressionException e) {
				throw new ProviderException("Incorrect XPath expression " + expr, e);
			}
			expressions.put(expr, expression);
		}

		return expression;
	}

	/**
	 * Evaluate XPath expression and return matched nodes list.
	 * 
	 * @param node - input DOM document
	 * @param expression - expression to evaluate
	 * @return Return list of matched nodes
	 * @throws ProviderException in case of evaluation problem
	 */
	protected NodeList getNodeList(Node node, XPathExpression expression) throws ProviderException {
		try {
			return (NodeList) expression.evaluate(node, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new ProviderException(e);
		}
	}

	@Override
	public Quote getQuote(Symbol symbol) throws ProviderException {

		if (!isLoggedIn()) {
			login();
		} else {
			purgeDOM();
		}

		Quote q = quotations.get(symbol);
		if (q == null) {

			Date d = new Date();
			Calendar c = new GregorianCalendar();
			c.setTime(d);
			c.add(Calendar.HOUR_OF_DAY, -12);

			String date = TIME_FORMAT.format(c.getTime());
			String update = String.format(UPDATE_URL, topicID, componentID, date);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("topicId", topicID));
			pairs.add(new BasicNameValuePair("componentId", componentID));
			pairs.add(new BasicNameValuePair("czas", date));

			HttpUriRequest post = affectFirefox(new HttpPost(update));
			try {
				((HttpPost) post).setEntity(new UrlEncodedFormEntity(pairs));
			} catch (UnsupportedEncodingException e) {
				throw new ProviderException(e);
			}

			q = getQuote0(symbol, execute(post));

			quotations.put(symbol, q);
		}

		return q;
	}

	private Quote getQuote0(Symbol symbol, String json) throws ProviderException {

		JSONArray array = null;
		try {
			array = new JSONArray(json);
		} catch (JSONException e) {
			throw new ProviderException(e);
		}

		JSONObject o = null;
		String name = null;
		int n = array.length();

		try {

			for (int i = 0; i < n; i++) {

				o = (JSONObject) array.get(i);
				name = o.optString("nazwa");

				if (name == null || name.length() == 0) {
					// ignore - wrong JSON object
					continue;
				}

				name = name.trim();

				if (symbol.getName().equals(name)) {
					return jsonToQuote(o);
				}
			}
		} catch (JSONException e) {
			throw new ProviderException(e);
		}

		return null;
	}

	protected Quote jsonToQuote(JSONObject o) throws ProviderException, JSONException {

		String name = o.getString("nazwa");
		String date = o.getString("data_sesji");
		String time = o.getString("czas");
		time = time.substring(time.indexOf('>') + 1, time.lastIndexOf('<'));
		date = date + " " + time;

		Date ndate = null;
		try {
			ndate = DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new ProviderException(e);
		}

		String open = o.getString("o").replaceAll(",", ".");
		String high = o.getString("h").replaceAll(",", ".");
		String low = o.getString("l").replaceAll(",", ".");
		String close = o.getString("c").replaceAll(",", ".");
		String volume = o.getString("v").replaceAll(" ", "");
		String bid = o.getString("bid").replaceAll(",", ".");
		String ask = o.getString("ask").replaceAll(",", ".");

		String[] check = new String[] { open, high, low, close, volume };
		for (int j = 0; j < check.length; j++) {
			if ("--".equals(check[j])) {
				throw new ProviderException("No updates for symbol " + name);
			}
		}

		BidAsk ba = new BidAsk();
		ba.setBid(Double.parseDouble(bid));
		ba.setAsk(Double.parseDouble(ask));

		Quote q = new Quote();
		q.setDate(ndate);
		q.setOpen(Double.parseDouble(open));
		q.setHigh(Double.parseDouble(high));
		q.setLow(Double.parseDouble(low));
		q.setClose(Double.parseDouble(close));
		q.setVolume(Long.parseLong(volume));
		q.setBidAsk(ba);

		return q;
	}

	@Override
	public boolean canServe(Symbol symbol) {
		return true;
	}

	/**
	 * Return node value.
	 * 
	 * @param node
	 * @param expression
	 * @return
	 * @throws ProviderException
	 */
	protected String getNodeValue(Node node, XPathExpression expression) throws ProviderException {
		try {
			return (String) expression.evaluate(node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new ProviderException(e);
		}
	}

	private HttpUriRequest affectFirefox(HttpUriRequest req) {
		req.setHeader("Host", "www.parkiet.com");
		req.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; pl; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8");
		req.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		req.setHeader("Accept-Language", "pl,en-us;q=0.7,es;q=0.3");
		req.setHeader("Accept-Charset", "ISO-8859-2,utf-8;q=0.7,*;q=0.7");
		req.setHeader("Keep-Alive", "115");
		req.setHeader("Accept-Encoding", "gzip,deflate");
		req.setHeader("Proxy-Connection", "keep-alive");
		req.setHeader("Connection", "keep-alive");
		req.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		return req;
	}

	/**
	 * @return user name used to login to www.parkiet.com
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName - new user name to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password - new password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Execute HTTP request.
	 * 
	 * @param req
	 * @return Response HTML
	 * @throws ProviderException
	 */
	protected String execute(HttpUriRequest req) throws ProviderException {

		ByteArrayOutputStream baos = null;
		try {

			HttpResponse response = client.execute(req);
			HttpEntity entity = response.getEntity();
			HttpEntity orig = entity;
			Header[] headers = null;

			int size = 32768;

			headers = response.getHeaders("Content-Length");
			if (headers.length > 0) {
				String val = headers[0].getValue();
				try {
					int s = Integer.parseInt(val);
					if (s < 1024E+3) {
						size = s;
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			headers = response.getHeaders("Content-Encoding");
			if (headers.length > 0) {
				String val = headers[0].getValue();
				if ("gzip".equals(val)) {
					entity = new GzipDecompressingEntity(entity);
				} else if ("deflat".equals(val)) {
					entity = new DeflateDecompressingEntity(entity);
				}
			}

			int s = (int) entity.getContentLength();
			if (s > 0) {
				size = s;
			}

			baos = new ByteArrayOutputStream(size + 1);
			entity.writeTo(baos);
			orig.getContent().close();

		} catch (Exception e) {
			throw new ProviderException(e);
		}

		String html = null;

		if (baos != null && baos.size() > 0) {
			html = new String(baos.toByteArray());
			baos.reset();
		}

		return html;
	}

	/**
	 * Login user into the service.
	 * 
	 * @throws ProviderException
	 */
	public void login() throws ProviderException {
		if (!isLoggedIn()) {
			this.login0();
		}
	}

	/**
	 * Login internal impl.
	 * 
	 * @throws ProviderException
	 */
	private void login0() throws ProviderException {

		String html = null;
		HttpUriRequest get = affectFirefox(new HttpGet(PARKIET_URL));

		html = execute(get);

		if (html == null) {
			throw new ProviderException(
				"No output HTML has been read from " + PARKIET_URL + " " +
				"service");
		}

		String usr = getUserName();
		String pwd = getPassword();

		if (usr == null) {
			usr = CFG.getProperty("parkiet.com", "username");
		}
		if (pwd == null) {
			pwd = CFG.getProperty("parkiet.com", "password");
		}

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("login", usr));
		pairs.add(new BasicNameValuePair("password", pwd));
		pairs.add(new BasicNameValuePair("zaloguj", findSubmitValue(html)));

		HttpUriRequest post = affectFirefox(new HttpPost(PARKIET_URL));
		try {
			((HttpPost) post).setEntity(new UrlEncodedFormEntity(pairs));
		} catch (UnsupportedEncodingException e) {
			throw new ProviderException(e);
		}

		html = execute(post);

		loggedIn = checkIfLoggedIn(html);

		if (!loggedIn) {
			throw new ProviderException("Unable to login");
		}

		address = getQuotesHref(html);
		topicID = findTopicID(address);

		if (address == null) {
			throw new ProviderException("Quotes href has not been found!");
		}

		html = execute(affectFirefox(new HttpGet(address)));
		componentID = findConmponentID(html);

		if (html == null || html.length() == 0) {
			throw new ProviderException("Cannot read HTML from request");
		}
	}

	private String findTopicID(String address) {
		int p = address.lastIndexOf('/') + 1;
		int k = address.lastIndexOf('.');
		return address.substring(p, k);
	}

	private String findConmponentID(String html) {
		String marker = "<table id=\"tab";
		int p = html.indexOf(marker) + marker.length();
		int k = html.indexOf("\"", p);
		return html.substring(p, k);
	}

	/**
	 * Find submit value for submit button.
	 * 
	 * @param html
	 * @return
	 * @throws ProviderException
	 */
	private String findSubmitValue(String html) throws ProviderException {

		Document dom = parseHTML(html);
		XPathExpression expression = getExpression("//INPUT[@name='zaloguj']");
		NodeList nodes = getNodeList(dom, expression);

		if (nodes != null) {
			int n = nodes.getLength();
			if (n == 1) {
				return nodes.item(0).getAttributes().getNamedItem("value").getNodeValue();
			} else {
				throw new ProviderException("Too " + (n == 0 ? "few" : "many") + " elements matched " + n);
			}
		} else {
			throw new ProviderException("XPath evaluation returned null");
		}
	}

	/**
	 * Check whether or not user is currently logged in.
	 * 
	 * @param html
	 * @return true if user is logged in, false otherwise
	 * @throws ProviderException
	 */
	private boolean checkIfLoggedIn(String html) throws ProviderException {

		String usr = getUserName();
		if (usr == null) {
			usr = CFG.getProperty("parkiet.com", "username");
		}

		return html.indexOf(">" + usr + "<") != -1;
	}

	/**
	 * Find quotes href in HTML string.
	 * 
	 * @param html - input HTML
	 * @return
	 * @throws ProviderException
	 */
	private String getQuotesHref(String html) throws ProviderException {

		Document dom = parseHTML(html);
		XPathExpression expression = getExpression("//A[@href][text()='Notowania']");

		Node node = null;
		NodeList nodes = null;

		String address = null;

		nodes = getNodeList(dom, expression);
		if (nodes.getLength() > 0) {

			node = nodes.item(0);
			expression = getExpression("following-sibling::UL/LI/A[text()='Ci¹g³e - GPW']");
			nodes = getNodeList(node, expression);

			if (nodes.getLength() > 0) {
				node = nodes.item(0);
				address = PARKIET_URL + node.getAttributes().getNamedItem("href").getNodeValue();
			}
		}

		return address;
	}

	/**
	 * @return true if user is logged in the service, false otherwise
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * Purge saved DOM field and length for HTML input.
	 */
	protected void purgeDOM() {
		length = 0;
		dom = null;
	}

	public static void main(String[] args) throws ProviderException, InterruptedException {
		ParkietProvider pp = new ParkietProvider();

		long to = System.currentTimeMillis();
		pp.login();
		System.out.println(System.currentTimeMillis() - to);

		to = System.currentTimeMillis();
		System.out.println(pp.getQuote(Symbol.KGH));
		System.out.println(System.currentTimeMillis() - to);

		Thread.sleep(60000);

		to = System.currentTimeMillis();
		System.out.println(pp.getQuote(Symbol.KGH));
		System.out.println(System.currentTimeMillis() - to);

		Thread.sleep(60000);

		to = System.currentTimeMillis();
		System.out.println(pp.getQuote(Symbol.KGH));
		System.out.println(System.currentTimeMillis() - to);

		to = System.currentTimeMillis();
	}
}
