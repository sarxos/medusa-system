package com.sarxos.medusa.market;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is used to check if given day is a free day.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Calendarium {

	/**
	 * This class is used only inside {@link Calendarium} class code.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected static class FreeDaysUpdater extends Thread {

		private long modified = 0;

		public FreeDaysUpdater() {
			setDaemon(true);
		}

		@Override
		public void run() {
			long modified = 0;
			while (true) {
				try {
					// 10 minute delay
					Thread.sleep(1000 * 60 * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				modified = new File(FILE_LOCATION).lastModified();
				if (this.modified != modified) {
					Calendarium.getInstance().updateFileDefinition();
					this.modified = modified;
				}
			}
		}
	}

	/**
	 * Date format used to store free day dates.
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Free days file location.
	 */
	public static final String FILE_LOCATION = "data/free.days";

	/**
	 * List of free dates.
	 */
	private List<Date> free = new ArrayList<Date>();

	/**
	 * Calendar object used to compare dates.
	 */
	private Calendar calendar = Calendar.getInstance();

	/**
	 * Singleton instance.
	 */
	private static Calendarium instance = new Calendarium();

	/**
	 * Free days file updater.
	 */
	private FreeDaysUpdater updater = new FreeDaysUpdater();

	/**
	 * Private constructor.
	 */
	private Calendarium() {
		updateFileDefinition();
		updater.start();
	}

	/**
	 * Update file with free dates.
	 * 
	 * @throws RuntimeException when data format in file is incorrect or cannot
	 *             read file
	 */
	protected void updateFileDefinition() {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream("data/free.days");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);

		String line = null;

		List<Date> tmp = new LinkedList<Date>();

		try {
			while (br.ready()) {
				line = br.readLine();
				line = line.trim();
				if (line.length() > 0) {
					try {
						tmp.add(DATE_FORMAT.parse(line));
					} catch (ParseException e) {
						throw new RuntimeException("Wrong date format '" + line + "'", e);
					}
				}
			}
			synchronized (this) {
				free.clear();
				free.addAll(tmp);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot read file " + FILE_LOCATION, e);
		}
	}

	/**
	 * @return Singleton instance.
	 */
	public static Calendarium getInstance() {
		return instance;
	}

	/**
	 * Check whether or not given date string is free day.
	 * 
	 * @param date - date string to check (yyyy-MM-dd)
	 * @return true if days is free, false otherwise
	 * @throws RuntimeException if data format is incorrect
	 */
	public boolean isFreeDay(String date) {
		try {
			return isFreeDay(DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			throw new RuntimeException("Wrong date format '" + date + "'", e);
		}
	}

	/**
	 * Check whether or not given day is a working day (simply check if given
	 * day is not a free day).
	 * 
	 * @param date - date to check (format yyyy-MM-dd)
	 * @return true if day is working, false otherwise
	 * @throws RuntimeException if data format is incorrect
	 */
	public boolean isWorkingDay(String date) {
		return !isFreeDay(date);
	}

	/**
	 * Check whether or not given date string is free day.
	 * 
	 * @param date - date to check
	 * @return true if days is free, false otherwise
	 */
	public synchronized boolean isFreeDay(Date date) {

		calendar.setTime(date);

		int dow = calendar.get(Calendar.DAY_OF_WEEK);
		switch (dow) {
			case Calendar.SUNDAY:
			case Calendar.SATURDAY:
				return true;
		}

		int ad, am, ay, bd, bm, by;

		ad = calendar.get(Calendar.DAY_OF_MONTH);
		am = calendar.get(Calendar.MONTH);
		ay = calendar.get(Calendar.YEAR);

		for (Date d : free) {
			calendar.setTime(d);
			bd = calendar.get(Calendar.DAY_OF_MONTH);
			bm = calendar.get(Calendar.MONTH);
			by = calendar.get(Calendar.YEAR);

			if (ad == bd && am == bm && ay == by) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check whether or not given day is a working day (simply check if given
	 * day is not a free day).
	 * 
	 * @param date - date to check
	 * @return true if day is working, false otherwise
	 */
	public boolean isWorkingDay(Date date) {
		return !isFreeDay(date);
	}

	/**
	 * Return session phase in the given time.
	 * 
	 * @param date - time to check
	 * @param paper - paper to check phase
	 * @return Phase of the session.
	 */
	public SessionPhase getSessionPhase(Date date, Paper paper) {

		if (date == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}
		if (paper == null) {
			throw new IllegalArgumentException("Paper cannot be null");
		}

		SecuritiesGroup group = paper.getGroup();

		synchronized (this) {

			calendar.setTime(date);

			Date before_open = null;
			Date open = null;
			Date fixing = null;
			Date play_off = null;
			Date close = null;

			calendar.set(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			before_open = calendar.getTime();

			switch (group) {
				case FUTURES_INDEXES:
				case FUTURES_QUOTES:
				case FUTURES_CURRENCY:
				case OPTIONS_INDEXES:
				case OPTIONS_MINIWIG:
					calendar.set(Calendar.MINUTE, 30);
					open = calendar.getTime();
					break;

				default:
					calendar.set(Calendar.HOUR_OF_DAY, 9);
					open = calendar.getTime();
					break;
			}

			calendar.set(Calendar.HOUR_OF_DAY, 17);
			calendar.set(Calendar.MINUTE, 20);

			fixing = calendar.getTime();

			calendar.set(Calendar.MINUTE, 30);
			play_off = calendar.getTime();

			calendar.set(Calendar.MINUTE, 35);
			close = calendar.getTime();

			calendar.setTime(date);

			long now = calendar.getTimeInMillis();

			if (now < before_open.getTime()) {
				return SessionPhase.CLOSED;
			} else if (before_open.getTime() < now && now < open.getTime()) {
				return SessionPhase.BEFORE_OPEN;
			} else if (open.getTime() < now && now < fixing.getTime()) {
				return SessionPhase.SESSION;
			} else if (fixing.getTime() < now && now < play_off.getTime()) {
				return SessionPhase.FIXING;
			} else if (play_off.getTime() < 0 && now < close.getTime()) {
				return SessionPhase.PLAY_OFF;
			} else if (now > close.getTime()) {
				return SessionPhase.CLOSED;
			}
		}

		return SessionPhase.CLOSED;
	}

	/**
	 * Check on the base of current date if market is open
	 * 
	 * @return true if market is open, false otherwise
	 */
	public boolean isMarketOpen() {
		return isMarketOpen(new Date());
	}

	/**
	 * Check on the base of given date if market is open
	 * 
	 * @param date date to check
	 * @return true if market is open, false otherwise
	 */
	public boolean isMarketOpen(Date date) {

		if (date == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}

		long open = 0;
		long close = 0;
		long now = 0;

		Date orig = null;

		synchronized (this) {

			orig = calendar.getTime();

			calendar.setTime(date);

			calendar.set(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			open = calendar.getTimeInMillis();

			calendar.set(Calendar.HOUR_OF_DAY, 17);
			calendar.set(Calendar.MINUTE, 35);

			close = calendar.getTimeInMillis();

			calendar.setTime(date);

			now = calendar.getTimeInMillis();

			calendar.setTime(orig);
		}

		if (open < now && now < close) {
			return true;
		}

		return false;
	}

	/**
	 * Tells whether or not session is in progress.
	 * 
	 * @param date - time to check
	 * @param paper - paper to check
	 * @return true / false
	 */
	public boolean isSessionInProgress(Date date, Paper paper) {
		SessionPhase phase = getSessionPhase(date, paper);
		switch (phase) {
			case BEFORE_OPEN:
			case SESSION:
			case PLAY_OFF:
				return true;

			case FIXING:
			case CLOSED:
			default:
				return false;
		}
	}

	/**
	 * Return next working day date.
	 * 
	 * @param date - date to from find next working day
	 * @return Next working day date
	 */
	public synchronized Date getNextWorkingDay(Date date) {
		do {
			calendar.setTime(date);
			calendar.add(Calendar.DATE, +1);
			date = calendar.getTime();
		} while (!isWorkingDay(date));

		return date;
	}

	/**
	 * Return previous working day date.
	 * 
	 * @param date - date to from find previous working day
	 * @return Previous working day date
	 */
	public synchronized Date getPreviousWorkingDay(Date date) {
		do {
			calendar.setTime(date);
			calendar.add(Calendar.DATE, -1);
			date = calendar.getTime();
		} while (!isWorkingDay(date));

		return date;
	}
}
