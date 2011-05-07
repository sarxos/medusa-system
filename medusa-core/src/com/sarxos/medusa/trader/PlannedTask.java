package com.sarxos.medusa.trader;

import java.util.Date;
import java.util.TimerTask;


public abstract class PlannedTask extends TimerTask {

	public static final long PERIOD_MINUTE = 1000 * 60; 
	
	public static final long PERIOD_HOUR = PERIOD_MINUTE * 60;

	public static final long PERIOD_DAY = PERIOD_HOUR * 24;
	
	private Date executionTime = null;
	
	private long executionPeriod = 0;

	
	public Date getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}

	public long getExecutionPeriod() {
		return executionPeriod;
	}

	public void setExecutionPeriod(long executionPeriod) {
		this.executionPeriod = executionPeriod;
	}
}
