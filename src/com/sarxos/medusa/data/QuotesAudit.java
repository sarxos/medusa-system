package com.sarxos.medusa.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import com.sarxos.medusa.db.DBDAO;
import com.sarxos.medusa.market.Calendarium;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


public class QuotesAudit {

	/**
	 * Will return dates with lacking quotes.
	 *  
	 * @param symbol - symbol to check
	 * @return Array of {@link Date} objects.
	 */
	public Date[] audit(Symbol symbol) {
	
		DBDAO qdao = DBDAO.getInstance();
		List<Quote> quotes = qdao.getQuotes(symbol);

		Quote last = quotes.get(quotes.size() - 1);
		Calendar today = new GregorianCalendar();
		
		Calendarium calendarium = Calendarium.getInstance();
		
		today.setTime(new Date());
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.HOUR_OF_DAY, 0);
		
		Date date = last.getDate();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		
		List<Date> dates = new LinkedList<Date>();
		
		while (calendar.before(today)) {
			date = calendarium.getNextWorkingDay(date);
			calendar.setTime(date);
			if (calendar.before(today)) {
				dates.add(date);
			}
		}
		
		return dates.toArray(new Date[dates.size()]);
	}
	
}
