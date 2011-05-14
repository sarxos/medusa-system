package com.sarxos.medusa.plugin.mystock;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.sarxos.medusa.http.MedusaHttpClient;


@PluginImplementation
@Author(name = "Bartosz Firyn")
public class MyStock {

	public boolean login(String usr, String pwd) throws Exception {

		MedusaHttpClient client = new MedusaHttpClient();

		// get login page
		HttpGet get = new HttpGet("http://www.mystock.pl/user/login.do");
		client.executeVoid(get);

		// send credentials
		ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("login", usr));
		nvps.add(new BasicNameValuePair("password", pwd));
		HttpPost post = new HttpPost("http://www.mystock.pl/logowanie/");
		post.setEntity(new UrlEncodedFormEntity(nvps));
		client.executeVoid(post);

		return false;
	}

	public static void main(String[] args) throws Exception {
		MyStock mpl = new MyStock();
		mpl.login("bfiryn", "test1234");
	}

}
