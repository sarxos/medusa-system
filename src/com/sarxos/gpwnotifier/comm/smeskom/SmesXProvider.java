package com.sarxos.gpwnotifier.comm.smeskom;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXRequest;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXResponse;


public class SmesXProvider {

	private Marshaller marshaller = null;
	
	private Unmarshaller unmarshaller = null;
	
	public SmesXProvider() {
		try {
			String pckg = SmesXRequest.class.getPackage().getName();
			JAXBContext jc = JAXBContext.newInstance(pckg);
			
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			unmarshaller = jc.createUnmarshaller();
			
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot create JAXB context", e);
		}
		
	}
	
	public SmesXResponse send(SmesXRequest request) {

		String xml = toXML(request);
		
		return null;
	}
	
	protected String toXML(SmesXRequest request) {
		
		if (request == null) {
			throw new IllegalArgumentException("SmesX Request c annot be null");
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			marshaller.marshal(request, baos);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot marshall object", e);
		}
		
		return new String(baos.toByteArray());
	}
}
