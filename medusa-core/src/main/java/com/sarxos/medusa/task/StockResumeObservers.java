package com.sarxos.medusa.task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sarxos.medusa.trader.PlannedTask;


/**
 * Resume observers before session.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class StockResumeObservers extends PlannedTask {

	public StockResumeObservers() {
		
		Date now = new Date();
		Date execution = null;
		
		GregorianCalendar calendar = new GregorianCalendar();
		
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
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
		// TODO Auto-generated method stub
	}
}
