package com.sarxos.medusa.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;


public class MedusaHttpClientTest {

	private static final String HOST_KEY = MedusaHttpClient.PROXY_HOST_KEY;
	private static final String PORT_KEY = MedusaHttpClient.PROXY_PORT_KEY;

	private static final String RESOURCE = "com/sarxos/medusa/http/test.in";

	private static final Handler HANDLER = new AbstractHandler() {

		public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
		throws IOException, ServletException {

			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_OK);

			OutputStream os = response.getOutputStream();
			InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);

			IOUtils.copy(is, os);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);

			((Request) request).setHandled(true);
		}
	};

	private static final Server SERVER = new Server(8080);

	@BeforeClass
	public static void init() throws Exception {
		SERVER.setHandler(HANDLER);
		SERVER.start();
	}

	@AfterClass
	public static void cleanup() throws Exception {
		SERVER.stop();

		// cleanup
		File test = new File("test.out");
		if (!FileUtils.deleteQuietly(test)) {
			FileUtils.forceDeleteOnExit(test);
		}
	}

	@Test
	public void test_proxy() {

		String oldHost = System.getProperties().getProperty(HOST_KEY);
		String oldPort = System.getProperties().getProperty(PORT_KEY);

		String host = "www.wp.pl";
		String port = "8080";

		System.setProperty(HOST_KEY, host);
		System.setProperty(PORT_KEY, port);

		MedusaHttpClient client = new MedusaHttpClient();
		HttpHost proxy = client.getProxy();

		Assert.assertNotNull(proxy);
		Assert.assertEquals(host, proxy.getHostName());
		Assert.assertEquals(Integer.parseInt(port), proxy.getPort());

		// cleanup
		if (oldHost != null) {
			System.setProperty(HOST_KEY, oldHost);
		} else {
			System.getProperties().remove(HOST_KEY);
		}
		if (oldPort != null) {
			System.setProperty(PORT_KEY, oldPort);
		} else {
			System.getProperties().remove(PORT_KEY);
		}
	}

	@Test
	public void test_download() throws URISyntaxException, HttpException {

		URI uri = new URI("http://localhost:8080");
		File test = new File("test.out");

		MedusaHttpClient client = new MedusaHttpClient();
		client.download(uri.toString(), test);

		Assert.assertEquals(10, test.length());
	}
}
