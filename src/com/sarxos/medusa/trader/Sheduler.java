package com.sarxos.medusa.trader;

import java.util.Date;
import java.util.Timer;

import com.sarxos.medusa.task.ReconcileQuotesDataTask;
import com.sarxos.medusa.task.StockPauseObservers;
import com.sarxos.medusa.task.StockPauseObservers2;
import com.sarxos.medusa.task.StockResumeObservers;
import com.sarxos.medusa.task.StockResumeObservers2;


public class Sheduler extends Timer {

	public Sheduler() {
		this.schedule(new ReconcileQuotesDataTask());
		this.schedule(new StockPauseObservers());
		this.schedule(new StockPauseObservers2());
		this.schedule(new StockResumeObservers());
		this.schedule(new StockResumeObservers2());
	}
	
	public void schedule(PlannedTask task) {
		Date execution = task.getExecutionTime();
		long period = task.getExecutionPeriod();
		this.scheduleAtFixedRate(task, execution, period);
	}
}
