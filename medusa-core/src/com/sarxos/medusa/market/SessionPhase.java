package com.sarxos.medusa.market;


public enum SessionPhase {

	/**
	 * Session is closed - time between 16:30 and 08:00 next day.
	 */
	CLOSED,
	
	/**
	 * Phase before open (08:00 - 09:00 for stocks, 08:00 - 08:30 for
	 * futures and options).
	 */
	BEFORE_OPEN,
	
	/**
	 * Session phase (after open to 16:10 for all papers excluding 
	 * treasury bonds, for treasury bonds session ends at 16:20). 
	 */
	SESSION,
	
	/**
	 * Fixing phase (from 16:10 to 16:20 for all papers excluding 
	 * treasury bonds which fixing occurs between 16:20 and 16:30).
	 */
	FIXING,
	
	/**
	 * For all papers (excluding treasury bonds) trade in the fixed
	 * price (16:20 - 16:30). After this time stock market is closed. 
	 */
	PLAY_OFF;
}
