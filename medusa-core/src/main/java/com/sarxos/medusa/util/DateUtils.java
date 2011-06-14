package com.sarxos.medusa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sarxos.medusa.data.QuotesStreamReader;


public class DateUtils {

	public static boolean isToday(Date date) {

		GregorianCalendar calendar = new GregorianCalendar();

		calendar.setTime(new Date());

		int ad, am, ay, bd, bm, by;

		ad = calendar.get(Calendar.DAY_OF_MONTH);
		am = calendar.get(Calendar.MONTH);
		ay = calendar.get(Calendar.YEAR);

		calendar.setTime(date);

		bd = calendar.get(Calendar.DAY_OF_MONTH);
		bm = calendar.get(Calendar.MONTH);
		by = calendar.get(Calendar.YEAR);

		if (ad == bd && am == bm && ay == by) {
			return true;
		}

		return false;
	}

	public static Date fromCGL(String str) {
		SimpleDateFormat sdf = QuotesStreamReader.DATE_FORMAT_SHORT;
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			throw new RuntimeException("Cannot parse date " + str, e);
		}
	}

	public static String toCGL(Date date) {
		SimpleDateFormat sdf = QuotesStreamReader.DATE_FORMAT_SHORT;
		return sdf.format(date);
	}
}
