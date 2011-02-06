package com.sarxos.medusa.data;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;


public class PersistanceProvider {

	private static XStream streamer = new XStream();
	
	public static String marshalGenParams(SignalGenerator<Quote> siggen) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, Object> params = siggen.getParameters();
		OutputStreamWriter osw = new OutputStreamWriter(baos, Charset.defaultCharset());
		CompactWriter cw = new CompactWriter(osw);
		streamer.marshal(params, cw);
		return new String(baos.toByteArray(), Charset.defaultCharset());
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> unmarshalGenParams(String xml) {
		return (Map<String, Object>) streamer.fromXML(xml);
	}
	
}
