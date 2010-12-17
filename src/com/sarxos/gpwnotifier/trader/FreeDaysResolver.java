package com.sarxos.gpwnotifier.trader;

import java.io.BufferedReader;
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


public class FreeDaysResolver {

	public static class FreeDaysUpdater extends Thread {

		public FreeDaysUpdater() {
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					// 1h delay
					Thread.sleep(1000 * 60 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FreeDaysResolver.getInstance().updateFileDefinition();
			}
		}
	}
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String FILE_LOCATION = "data/free.days";
	
	private List<Date> free = new ArrayList<Date>();

	private Calendar calendar = Calendar.getInstance(); 
	
	private static FreeDaysResolver instance = new FreeDaysResolver();
	
	private FreeDaysUpdater updater = new FreeDaysUpdater();
	
	public FreeDaysResolver() {
		updateFileDefinition();
		updater.start();
	}

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
	
	public static FreeDaysResolver getInstance() {
		return instance;
	}
	
	public boolean isFreeDay(String date) {
		try {
			return isFreeDay(DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			throw new RuntimeException("Wrong date format '" + date + "'", e);
		}
	}
	
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
		
		System.out.println(FreeDaysResolver.getInstance().isFreeDay("2011-08-12"));
		
	}
}
