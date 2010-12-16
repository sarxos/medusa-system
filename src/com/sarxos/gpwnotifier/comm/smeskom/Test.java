package com.sarxos.gpwnotifier.comm.smeskom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXRequest;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMS;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMSGetStatus;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMSStatus;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMSType;
import com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXSMSSend;

import static com.sarxos.gpwnotifier.comm.smeskom.v22.SmesXEntity.DATE_FORMAT; 


public class Test {

	public static void main(String[] args) throws ParseException {
		

		SmesXSMSSend send_sms = new SmesXSMSSend();
		send_sms.setMSISDN("+48509934614");
		send_sms.setBody("Test");
		send_sms.setExpireDate(DATE_FORMAT.parse("2010-12-24 12:00:00"));
		send_sms.setSendAfterDate(DATE_FORMAT.parse("2010-12-24 12:00:00"));
		send_sms.setSender("Bob");
		send_sms.setSMSType(SmesXSMSType.NORMAL);
		
		SmesXSMS sms1 = new SmesXSMS();
		sms1.setId("1234");
		sms1.setDeliveredAtDate(DATE_FORMAT.parse("2010-12-24 12:00:00"));
		sms1.setSentAtDate(DATE_FORMAT.parse("2010-12-24 11:00:00"));
		sms1.setStatus(SmesXSMSStatus.DELIVERED);
		
		SmesXSMSGetStatus status = new SmesXSMSGetStatus();
		status.addID("1234");
		status.addID("1235");
		status.addID("1236");
		status.addID("1237");
		status.addSMS(sms1);
		
		SmesXRequest req = new SmesXRequest();
		req.setUser("TestUsr");
		req.setPassword("Passwd");
		req.setOperation(status);

		
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			JAXBContext jc = JAXBContext.newInstance("com.sarxos.gpwnotifier.comm.smeskom.v22");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(req, baos);
			
			String str = new String(baos.toByteArray());
			
			System.out.println(str);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
			
			Unmarshaller u = jc.createUnmarshaller();
			req = (SmesXRequest) u.unmarshal(bais);
			
			
			System.out.println(req.getPassword());
			System.out.println(req.getUser());
			System.out.println(req.getVersion());
			System.out.println(req.getProtocol());
			
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		
//		req = (SmesXRequest) streamer.fromXML(str);
//
//		System.out.println(req.getPassword());
//		System.out.println(req.getUser());
//		System.out.println(req.getVersion());
//		System.out.println(req.getProtocol());
		
	}
	
}
