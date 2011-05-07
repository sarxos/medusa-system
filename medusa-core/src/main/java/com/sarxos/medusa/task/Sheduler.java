package com.sarxos.medusa.task;

import java.util.Date;
import java.util.Timer;

import com.sarxos.medusa.trader.PlannedTask;


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
