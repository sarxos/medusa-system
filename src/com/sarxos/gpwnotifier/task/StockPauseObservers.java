package com.sarxos.gpwnotifier.task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.sarxos.gpwnotifier.trader.DecisionMaker;
import com.sarxos.gpwnotifier.trader.Observer;
import com.sarxos.gpwnotifier.trader.PlannedTask;
import com.sarxos.gpwnotifier.trader.Trader;
import com.sarxos.gpwnotifier.trader.Wallet;


/**
 * Pause observers after regular session.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class StockPauseObservers extends PlannedTask {

	public StockPauseObservers() {

		Date now = new Date();
		Date execution = null;

		GregorianCalendar calendar = new GregorianCalendar();

		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE, 10);
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
		
		Trader trader = Trader.getInstance();
		
		List<DecisionMaker> decisionMakers = trader.getDecisionMakers();
		Observer observer = null;
		
		for (DecisionMaker maker : decisionMakers) {
			observer = maker.getObserver();
			observer.getSymbol();
		}
		
		Wallet wallet = Wallet.getInstance();

		
		
		// TODO Auto-generated method stub
	}
}
