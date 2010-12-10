package com.sarxos.gpwnotifier.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sarxos.gpwnotifier.data.stoq.StoqColumn;


/**
 * Quote class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Quote {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private Date date = null;
	
	@StoqColumn("Date")
	private String datestring = null;

	@StoqColumn("Open")
	private double open = 0;
	
	@StoqColumn("High")
	private double high = 0;
	
	@StoqColumn("Low")
	private double low = 0;
	
	@StoqColumn("Close")
	private double close = 0;
	
	@StoqColumn("Volume")
	private long volume = 0;

	private Quote next = null;
	private Quote prev = null;
	
	public Quote next() {
		return next;
	}

	public void setNext(Quote next) {
		this.next = next;
	}

	public Quote prev() {
		return prev;
	}

	public void setPrev(Quote prev) {
		this.prev = prev;
	}

	/**
	 * This constructor exists only for reflection purpose. 
	 */
	public Quote() {
	}
	
	/**
	 * Create quote instance with all necessary parameters. 
	 * 
	 * @param datestring - quote date as {@link String} object (format is yyyy-mm-dd)
	 * @param open - opening price
	 * @param high - highest price
	 * @param low - lowest price
	 * @param close - closing price
	 * @param volume - whole day volume
	 */
	public Quote(String datestring, double open, double high, double low, double close, long volume) {
		super();
		this.datestring = datestring;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	/**
	 * @return Quote date as {@link String} instance.
	 */
	public String getDateString() {
		return datestring;
	}

	/**
	 * Set quote date from {@link String}
	 * 
	 * @param datestring - new date to set.
	 */
	public void setDateString(String datestring) {
		this.datestring = datestring;
		try {
			this.date = DATE_FORMAT.parse(datestring);
			String format = DATE_FORMAT.format(this.date);
			if (!format.equals(datestring)) {
				System.err.println("sth wrong");
			}
		} catch (ParseException e) {
			throw new RuntimeException("Cannot parse date '" + datestring + "'", e);
		}
	}

	/**
	 * @return Quote date as {@link Date} object.
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Set quote date from {@link Date} object.
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return Opening price.
	 */
	public double getOpen() {
		return open;
	}

	/**
	 * Set opening price.
	 * 
	 * @param open - opening price
	 */
	public void setOpen(double open) {
		this.open = open;
	}

	/**
	 * @return Highest price for particular date.
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * Set highest price for particular date.
	 * 
	 * @param high - highest price.
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * @return Lowest price for particular date.
	 */
	public double getLow() {
		return low;
	}

	/**
	 * Set lowest price for particular date.
	 * 
	 * @param low - lowest price.
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 * @return Closing price for particular date. 
	 */
	public double getClose() {
		return close;
	}

	/**
	 * Set closing price for particular date.
	 * 
	 * @param close - closing price.
	 */
	public void setClose(double close) {
		this.close = close;
	}

	/**
	 * @return Volume for particular date.
	 */
	public long getVolume() {
		return volume;
	}

	/**
	 * Set volume.
	 * 
	 * @param volume - volume to set.
	 */
	public void setVolume(long volume) {
		this.volume = volume;
	}
	
	@Override
	public String toString() {
		return datestring + " " + open + " " + close;
	}
	
	public static void main(String[] args) throws ParseException {
		System.out.println(DATE_FORMAT.parse("2010-11-10"));
	}
}
