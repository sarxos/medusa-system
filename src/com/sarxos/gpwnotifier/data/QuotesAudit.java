package com.sarxos.gpwnotifier.data;
import java.util.Date;
import java.util.List;

import com.sarxos.gpwnotifier.market.Quote;


public class QuotesAudit {

	public static void main(String[] args) {
		// TODO parametrize
		QuotesAudit.audit("KGH");
	}

	public static Date[] audit(String symbol) {
	
		QuotesDAO qdao = new QuotesDAO();
		List<Quote> quotes = qdao.getQuotes(symbol);

		// TODO
		// read all quotes after last quote and before now where
		// date in set of working days
		
		return null;
	}
	
}
