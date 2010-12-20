/* Main.java
 * -----------------------------------------------------------------------------
 * 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * -----------------------------------------------------------------------------
 * JFB	1.0		17-Jul-2003		Created today.
 */

package com.mac.verec.datafeed.metastock ;

import com.mac.verec.models.* ;
import java.lang.Object ;
import java.lang.String ;
import java.lang.System ;
import java.util.Enumeration ;

public class Main {
	public static void main(String args[]) {
		String path = "../../quotes/" ;
		if (args.length > 0) path = args[0] ;

		Reader r = new Reader(path, true) ;
		Enumeration instruments = r.getInstruments() ;
		
		while(instruments.hasMoreElements()) {
			Instrument instr = (Instrument) instruments.nextElement() ;

			System.out.println(instr.name + "-" + instr.symbol) ;
			System.out.println("... " + instr.quotes[0]) ;
			System.out.println("... " + instr.quotes[1]) ;
			System.out.println("... " + instr.quotes[instr.quotes.length-1]) ;
		}
	}
}
