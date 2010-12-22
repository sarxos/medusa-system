package com.sarxos.gpwnotifier.task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import com.sarxos.gpwnotifier.data.DataProviderException;
import com.sarxos.gpwnotifier.data.bossa.BossaDataProvider;
import com.sarxos.gpwnotifier.data.db.DBDAO;
import com.sarxos.gpwnotifier.data.db.QuotesAudit;
import com.sarxos.gpwnotifier.market.Paper;
import com.sarxos.gpwnotifier.market.Quote;
import com.sarxos.gpwnotifier.market.Symbol;
import com.sarxos.gpwnotifier.trader.PlannedTask;
import com.sarxos.gpwnotifier.trader.Wallet;


/**
 * Reconcile missing quotes data.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ReconcileQuotesDataTask extends PlannedTask {

	private GregorianCalendar calendar = new GregorianCalendar();
	
	private Wallet wallet = Wallet.getInstance();
	
	private QuotesAudit qa = new QuotesAudit();
	
	private DBDAO qdao = new DBDAO();

	private BossaDataProvider bdp = new BossaDataProvider();

	
	public ReconcileQuotesDataTask() {
		
		Date now = new Date();
		Date execution = null;
		
		GregorianCalendar calendar = new GregorianCalendar();
		
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 5);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		execution = calendar.getTime();
		if (execution.getTime() < now.getTime()) {
			calendar.add(Calendar.DATE, +1);
		}

		execution = calendar.getTime();
		
		setExecutionTime(execution);
		setExecutionPeriod(PlannedTask.PEERIOD_DAY);
	}
	
	@Override
	public void run() {
		
		List<Paper> papers = wallet.getPapers();

		int i, ad, am, ay, bd, bm, by;
		
		for (Paper paper : papers) {
			
			Symbol symbol = paper.getSymbol();
			Date[] missing = qa.audit(symbol);
			
			if (missing.length > 0) {
				
				if (missing.length <= 6) {
					
					boolean required = false;
					
					Quote q = null;
					Date d = null;
					
					List<Quote> last6 = null;
					try {
						last6 = bdp.getLastQuotes(symbol);
					} catch (DataProviderException e) {
						e.printStackTrace();
					}
					
					ListIterator<Quote> qi = last6.listIterator();
					
					while (qi.hasNext()) { 
						
						q = qi.next();
						d = q.getDate();
						
						calendar.setTime(d);
						
						bd = calendar.get(Calendar.DAY_OF_MONTH);
						bm = calendar.get(Calendar.MONTH);
						by = calendar.get(Calendar.YEAR);
					
						required = false;
						
						for (i = 0; i < missing.length; i++) {
							calendar.setTime(missing[i]);
							ad = calendar.get(Calendar.DAY_OF_MONTH);
							am = calendar.get(Calendar.MONTH);
							ay = calendar.get(Calendar.YEAR);
							
							if (ad == bd && am == bm && ay == by) {
								required = true;
								break;
							}
						}
						
						if (!required) {
							qi.remove();
						}
					}

					qdao.addQuotes(symbol, last6);
					
				} else {
					throw new RuntimeException(
							"Need to reconsile data manually. Data length to " +
							"reconsile = " + missing.length
					);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		
		Wallet wallet = Wallet.getInstance();
		wallet.add(new Paper(Symbol.KGH, 60));
		
		ReconcileQuotesDataTask rqdt = new ReconcileQuotesDataTask();
		rqdt.run();
		
	}
}
