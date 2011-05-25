package com.sarxos.medusa.market;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sarxos.medusa.util.DateUtils;


/**
 * Futures switcher.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class FuturesSwitcher {

	/**
	 * Return full future symbol for the synthetic symbol and given date.
	 * Resultant date is within 3-monts future validity interval.
	 * 
	 * @param symbol - synthetic future symbol
	 * @param date - date to get symbol for
	 * @return Will return full future symbol valid in given date
	 */
	public Symbol getSymbolForDate(Symbol symbol, Date date) {

		if (!SymbolUtils.isSynthetic(symbol)) {
			throw new IllegalArgumentException(
				"Only synthetic futures symbols can be used to get date " +
				"for it.");
		}

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		int month = calendar.get(Calendar.MONTH) + 1;
		switch (month) {
			case 1:
			case 2:
			case 3:
				// H - March
				break;
			case 4:
			case 5:
			case 6:
				// M - June
				break;
			case 7:
			case 8:
			case 9:
				// U - September
				break;
			case 10:
			case 11:
			case 12:
				// Z - December
				break;
		}

		// TODO finish - find 3'rd Friday in the month

		return null;
	}

	public static void main(String[] args) {
		FuturesSwitcher fs = new FuturesSwitcher();
		fs.getSymbolForDate(Symbol.FQQQ, DateUtils.fromCGL("20110201"));
	}
}
