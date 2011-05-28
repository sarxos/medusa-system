package com.sarxos.medusa.market;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sarxos.medusa.market.annotation.Synthetic;
import com.sarxos.medusa.util.DateUtils;


/**
 * Symbol utilities.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class SymbolUtils {

	/**
	 * Tell whether or not given future symbol is synthetic.
	 * 
	 * @param symbol - symbol to check
	 * @return true i f symbol is synthetic, false otherwise
	 */
	public static boolean isSynthetic(Symbol symbol) {
		if (symbol == null) {
			throw new IllegalArgumentException("Symobl to check cannot be null");
		}
		Synthetic synth = null;
		try {
			synth = symbol.getClass().getField(symbol.toString()).getAnnotation(Synthetic.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return synth != null;
	}

	/**
	 * Return full future symbol for the synthetic symbol and given date.
	 * Resultant date is within 3-monts future validity interval.
	 * 
	 * @param symbol - synthetic future symbol
	 * @param date - date to get symbol for
	 * @return Will return full future symbol valid in given date
	 */
	public static Symbol forDate(Symbol symbol, Date date) {

		if (!SymbolUtils.isSynthetic(symbol)) {
			throw new IllegalArgumentException(
				"Only synthetic futures symbols can be used to get date " +
				"for it.");
		}

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		String letter = null;

		int month = (int) (Math.ceil((double) (calendar.get(Calendar.MONTH) + 1) / 3) * 3);
		switch (month) {
			case 3:
				letter = getLetterForMonth(calendar, month, "H", "M");
				break;
			case 6:
				letter = getLetterForMonth(calendar, month, "M", "U");
				break;
			case 9:
				letter = getLetterForMonth(calendar, month, "U", "Z");
				break;
			case 12:
				letter = getLetterForMonth(calendar, month, "Z", "H");
				break;
		}

		String postfix = null;
		int year = calendar.get(Calendar.YEAR);
		if (year < 2009) {
			postfix = Integer.toString(year).substring(3);
		} else {
			postfix = Integer.toString(year).substring(2);
		}

		String name = symbol + letter + postfix;
		try {
			return Symbol.valueOf(name);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Missing future symbol for '" + name + "'", e);
		}
	}

	/**
	 * Return {@link Date} object pointing to the 3'rd Friday of month - 2 days.
	 * 
	 * @param calendar - calendar object
	 * @return Return Date pointing to 3'rd Friday - 2 days.
	 */
	private static Date find3rdFriday(Calendar calendar) {
		Date orig = calendar.getTime();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		calendar.add(Calendar.DATE, +21 - 2); // 2 days to buy new future
		Date date = calendar.getTime();
		calendar.setTime(orig);
		return date;
	}

	/**
	 * Return future letter for given date and month.
	 * 
	 * @param calendar - current calendar object
	 * @param month - month to get letter for (3, 6, 9, 12)
	 * @param p - letter before 3'rd Friday
	 * @param n - letter after 3'rd Friday
	 * @return Return future letter for current date
	 */
	private static String getLetterForMonth(Calendar calendar, int month, String p, String n) {
		Date date = calendar.getTime();
		calendar.set(Calendar.MONTH, month - 1);
		Date friday = find3rdFriday(calendar);
		calendar.setTime(date);
		return date.getTime() < friday.getTime() ? p : n;
	}

	public static void main(String[] args) {
		Symbol s = Symbol.FQQQ;
		Symbol f = s.forDate(DateUtils.fromCGL("20110621"));
		System.out.println(f);
	}
}
