package com.sarxos.medusa.task;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.data.DBDAOException;
import com.sarxos.medusa.data.QuotesAudit;
import com.sarxos.medusa.data.QuotesIterator;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.provider.HistoryProvider;
import com.sarxos.medusa.provider.ProviderException;
import com.sarxos.medusa.provider.Providers;
import com.sarxos.medusa.provider.history.BossaProvider;
import com.sarxos.medusa.trader.PlannedTask;
import com.sarxos.medusa.trader.Trader;
import com.sarxos.medusa.trader.Wallet;


/**
 * Reconcile missing quotes data.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ReconcileQuotesDataTask extends PlannedTask {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ReconcileQuotesDataTask.class.getSimpleName());

	private GregorianCalendar calendar = new GregorianCalendar();

	private Wallet wallet = Wallet.getInstance();

	private QuotesAudit qa = new QuotesAudit();

	private DBDAO qdao = DBDAO.getInstance();

	private HistoryProvider provider = new BossaProvider();

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
		setExecutionPeriod(PlannedTask.PERIOD_DAY);
	}

	@Override
	public void run() {

		LOG.info("Performing quotes data reconciliation");

		List<Trader> traders = null;

		try {
			traders = qdao.getTraders();
		} catch (DBDAOException e1) {
			e1.printStackTrace();
		}

		if (traders == null) {
			throw new RuntimeException("Traders list cannot be null here!");
		}

		List<Paper> papers = new ArrayList<Paper>(traders.size() + 1);
		for (Trader t : traders) {
			papers.add(t.getPaper());
		}

		int i, ad, am, ay, bd, bm, by;

		for (Paper paper : papers) {

			Symbol symbol = paper.getSymbol();

			List<Quote> quotes = qdao.getQuotes(symbol);
			if (quotes.size() == 0) {
				download(symbol);
			}

			Date[] missing = qa.audit(symbol);

			if (missing.length > 0) {

				if (LOG.isInfoEnabled()) {
					String k = Integer.toString(missing.length);
					String s = paper.getSymbol().toString();
					LOG.info(format("Missing %s quotes from %s symbol", k, s));
				}

				List<Quote> add = null;

				boolean required = false;

				Quote q = null;
				Date d = null;

				try {
					if (missing.length <= 6) {
						add = provider.getLastQuotes(symbol);
					} else {
						add = provider.getAllQuotes(symbol);
					}
				} catch (ProviderException e) {
					LOG.error(e.getMessage(), e);
				}

				ListIterator<Quote> qli = add.listIterator();

				while (qli.hasNext()) {

					q = qli.next();
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
						qli.remove();
					}
				}

				qdao.addQuotes(symbol, add);

				if (LOG.isInfoEnabled()) {
					String s = symbol.toString();
					String msg = format("Quotes reconsiliation for symbol %s finished", s);
					LOG.info(msg);
				}

				if (LOG.isInfoEnabled()) {
					String s = symbol.toString();
					String msg = format("Starting intraday reconciliation for symbol %s", s);
					LOG.info(msg);
				}

				try {
					QuotesIterator<Quote> qi = provider.getIntradayQuotes(symbol);
					if (!qi.hasNext()) {
						LOG.error("Cannot reconcile intraday quotes for symbol " + symbol);
					}
				} catch (ProviderException e) {
					LOG.error(e.getMessage(), e);
				}

				if (LOG.isInfoEnabled()) {
					String s = symbol.toString();
					String msg = format("Intraday reconciliation for symbol %s has been finished", s);
					LOG.info(msg);
				}
			}
		}
	}

	public void download(Symbol symbol) {

		LOG.info("Downloading quotes for symbol " + symbol);

		HistoryProvider hp = Providers.getHistoryProvider();
		try {
			List<Quote> quotes = hp.getAllQuotes(symbol);
			qdao.addQuotes(symbol, quotes);
		} catch (ProviderException e) {
			throw new RuntimeException(e);
		}

		LOG.info("Downloading intraday quotes for symbol " + symbol);

		try {
			QuotesIterator<Quote> qi = hp.getIntradayQuotes(symbol);
			if (!qi.hasNext()) {
				LOG.error("Cannot reconcile intraday quotes for symbol " + symbol);
			}
		} catch (ProviderException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {

		// Wallet wallet = Wallet.getInstance();
		// wallet.addPaper(new Paper(Symbol.KGH, 60));

		ReconcileQuotesDataTask rqdt = new ReconcileQuotesDataTask();
		rqdt.run();

	}
}
