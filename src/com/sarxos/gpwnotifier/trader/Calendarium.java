package com.sarxos.gpwnotifier.trader;

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
	 * This class is used only inside {@link Calendarium} class
	 * code. 
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
					// 1 minute delay
					Thread.sleep(1000 * 60);
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
	 * @throws RuntimeException when data format in file is incorrect or cannot read file
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
				line = br. readLine();
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
	 * Check whether or not given date string is free day.
	 *   
	 * @param date - date to check (yyyy-MM-dd)
	 * @return true if days is free, false otherwise
	 * @throws RuntimeException if data format is incorrect 
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
	
	public static void main(String[] args) {
		
		System.out.println(Calendarium.getInstance().isFreeDay("2011-08-13"));
		
	}
}
