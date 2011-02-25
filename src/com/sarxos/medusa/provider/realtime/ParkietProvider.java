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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
	 * Parkiet.com service URL.
	 */
	public static final String PARKIET_URL = "http://www.parkiet.com";

	/**
	 * Time format in service quotes.
	 */
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");

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
	 * Quotes address.
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
	 * Constructor.
	 */
	public ParkietProvider() {

		client = new DefaultHttpClient();

		// proxy

		String phost = (String) System.getProperties().get("http.proxyHost");
		String pport = (String) System.getProperties().get("http.proxyPort");

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

		HttpUriRequest get = affectFirefox(new HttpGet(address));

		return getQuoteFromHTML(symbol, execute(get));
	}

	@Override
	public boolean canServe(Symbol symbol) {
		return true;
	}

	/**
	 * Read HTML, parse it and find symbol data inside.
	 * 
	 * @param symbol - symbol to find
	 * @param html - input HTML
	 * @return Return new quote
	 * @throws ProviderException
	 */
	protected Quote getQuoteFromHTML(Symbol symbol, String html) throws ProviderException {

		Document dom = parseHTML(html);
		String xpath = "//DIV[@class='quotations']/TABLE[@title='Dane rzeczywiste']/TBODY";
		XPathExpression expression = getExpression(xpath);
		NodeList nodes = getNodeList(dom, expression);
		Node node = null;

		if (nodes.getLength() > 0) {

			node = nodes.item(0);
			expression = getExpression("TR/TD[@class='nazwa']/A[text()='" + symbol.getName() + "']");
			nodes = getNodeList(node, expression);

			if (nodes.getLength() > 0) {
				node = nodes.item(0).getParentNode().getParentNode();
			} else {
				throw new ProviderException("Cannot find symbol " + symbol.getName());
			}

			String time = getNodeValue(node, getExpression("TD[@class='czas']/SPAN/text()"));
			String price = getNodeValue(node, getExpression("TD[@class='c']/text()"));
			String open = getNodeValue(node, getExpression("TD[@class='o']/text()"));
			String high = getNodeValue(node, getExpression("TD[@class='h']/text()"));
			String low = getNodeValue(node, getExpression("TD[@class='l']/text()"));
			String volume = getNodeValue(node, getExpression("TD[@class='v']/text()"));

			Date now = new Date();
			Date date = null;
			try {
				date = TIME_FORMAT.parse(time);
			} catch (ParseException e) {
				throw new ProviderException(e);
			}

			Calendar tmp = new GregorianCalendar();
			tmp.setTime(date);

			Calendar calendar = new GregorianCalendar();

			Calendarium c = Calendarium.getInstance();
			while (c.isFreeDay(now)) {
				now = c.getPreviousWorkingDay(now);
			}

			calendar.setTime(now);
			calendar.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, tmp.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, tmp.get(Calendar.SECOND));
			calendar.set(Calendar.MILLISECOND, 0);

			now = calendar.getTime();

			double dopen = Double.parseDouble(open.replaceAll(",", "."));
			double dhigh = Double.parseDouble(high.replaceAll(",", "."));
			double dlow = Double.parseDouble(low.replaceAll(",", "."));
			double close = Double.parseDouble(price.replaceAll(",", "."));
			long dvolume = Long.parseLong(volume.replaceAll(" ", ""));

			return new Quote(now, dopen, dhigh, dlow, close, dvolume);

		} else {
			throw new ProviderException("Cannot match elements for XPath " + xpath);
		}
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
			NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				Node n = nodes.item(0);
				return n.getNodeValue();
			}
		} catch (XPathExpressionException e) {
			throw new ProviderException(e);
		}
		return null;
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

		if (address == null) {
			throw new ProviderException("Quotes href has not been found!");
		}
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

		Document dom = parseHTML(html);
		XPathExpression expression = getExpression("//A[@href][text()='" + usr + "']");
		NodeList nodes = getNodeList(dom, expression);

		return nodes != null && nodes.getLength() > 0;
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

	public static void main(String[] args) throws ProviderException {
		ParkietProvider pp = new ParkietProvider();
		// pp.login();
		long to = System.currentTimeMillis();
		System.out.println(pp.getQuote(Symbol.KGH));
		System.out.println(System.currentTimeMillis() - to);
		to = System.currentTimeMillis();
		System.out.println(pp.getQuote(Symbol.KGH));
		System.out.println(System.currentTimeMillis() - to);
		to = System.currentTimeMillis();
		System.out.println(pp.getQuote(Symbol.KGH));
		System.out.println(System.currentTimeMillis() - to);
		to = System.currentTimeMillis();
	}
}
