package com.sarxos.medusa.plugin.dde;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.plugin.dde.impl.NOL3;


/**
 * To execute this example you have to export FW20M11 (or other symbol) to the
 * DDEManager in the NOL3 application. It won't run without it.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class NOL3Example {

	public static void main(String[] args) {

		NOL3 nol = new NOL3();
		try {
			nol.connect();
			Quote q = nol.getQuote("FW20M11");
			System.out.println("quote: " + q);

			Thread.sleep(5000);

			q = nol.getQuote("FW20M11");
			System.out.println("quote: " + q);

		} catch (Exception e) {
			e.printStackTrace();
			if (nol.isConnected()) {
				try {
					nol.disconnect();
				} catch (DDEException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
