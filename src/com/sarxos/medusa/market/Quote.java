package com.sarxos.medusa.market;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sarxos.medusa.util.StoqColumn;


/**
 * Quote class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Quote implements Cloneable {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private Symbol symbol = null;

	private Date date = null;

	@StoqColumn("Date")
	private String datestring = null;

	@StoqColumn("Open")
	private double open = -1;

	@StoqColumn("High")
	private double high = -1;

	@StoqColumn("Low")
	private double low = -1;

	@StoqColumn("Close")
	private double close = -1;

	@StoqColumn("Volume")
	private long volume = -1;

	/**
	 * Next quote - can be null.
	 */
	private Quote next = null;

	/**
	 * Previous quote - can be null.
	 */
	private Quote prev = null;

	/**
	 * Bid / ask pair.
	 */
	private BidAsk bidAsk = null;

	/**
	 * This constructor exists only for reflection purpose.
	 */
	public Quote() {
	}

	/**
	 * Create quote instance with all necessary parameters.
	 * 
	 * @param date - quote date (format is yyyy-mm-dd)
	 * @param open - opening price
	 * @param high - highest price
	 * @param low - lowest price
	 * @param close - closing price
	 * @param volume - whole day volume
	 */
	public Quote(String date, double open, double high, double low, double close, long volume) {
		this(null, date, open, high, low, close, volume);
	}

	/**
	 * Create quote instance with all necessary parameters.
	 * 
	 * @param datestring - quote date (format is yyyy-mm-dd)
	 * @param open - opening price
	 * @param high - highest price
	 * @param low - lowest price
	 * @param close - closing price
	 * @param volume - whole day volume
	 */
	public Quote(Date date, double open, double high, double low, double close, long volume) {
		this(null, DATE_FORMAT.format(date), open, high, low, close, volume);
	}

	/**
	 * Create quote instance with all necessary parameters.
	 * 
	 * @param symbol - quote symbol
	 * @param date - quote date (format is yyyy-mm-dd)
	 * @param open - opening price
	 * @param high - highest price
	 * @param low - lowest price
	 * @param close - closing price
	 * @param volume - whole day volume
	 */
	public Quote(Symbol symbol, String date, double open, double high, double low, double close, long volume) {
		super();
		this.setDateString(date);
		this.setOpen(open);
		this.setHigh(high);
		this.setLow(low);
		this.setClose(close);
		this.setVolume(volume);
		this.symbol = symbol;
	}

	/**
	 * Create quote instance with all necessary parameters.
	 * 
	 * @param symbol - quote symbol
	 * @param date - quote date (format is yyyy-mm-dd)
	 * @param open - opening price
	 * @param high - highest price
	 * @param low - lowest price
	 * @param close - closing price
	 * @param volume - whole day volume
	 */
	public Quote(Symbol symbol, Date date, double open, double high, double low, double close, long volume) {
		super();
		this.setDate(date);
		this.setOpen(open);
		this.setHigh(high);
		this.setLow(low);
		this.setClose(close);
		this.setVolume(volume);
		this.symbol = symbol;
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
		if (datestring != null) {
			if (datestring.length() == 0) {
				throw new IllegalArgumentException("Date string for quote cannot be empty");
			}
			try {
				this.date = DATE_FORMAT.parse(datestring);
				String format = DATE_FORMAT.format(this.date);
				if (!format.equals(datestring)) {
					throw new RuntimeException(
						"Date re-conversion does not match correct format: " +
						"'" + format + "' != '" + datestring + "'");
				}
			} catch (ParseException e) {
				throw new RuntimeException("Cannot parse date '" + datestring + "'", e);
			}
		}
		this.datestring = datestring;
	}

	/**
	 * @return Quote date as {@link Date} object.
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Set quote date from {@link Date} object.
	 * 
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
		if (datestring == null) {
			datestring = DATE_FORMAT.format(date);
		}
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

		StringBuffer sb = new StringBuffer(256);
		sb.append(getClass().getSimpleName());
		sb.append("[").append(getDateString()).append("]");
		sb.append('[');
		sb.append("O:").append(String.format("%.2f", open).replaceAll(",", "."));
		sb.append(" H:").append(String.format("%.2f", high).replaceAll(",", "."));
		sb.append(" L:").append(String.format("%.2f", low).replaceAll(",", "."));
		sb.append(" C:").append(String.format("%.2f", close).replaceAll(",", "."));

		if (bidAsk != null) {
			sb.append(" B|A:").append(bidAsk);
		}

		sb.append("]");

		return sb.toString();
	}

	/**
	 * @return the bidAsk
	 */
	public BidAsk getBidAsk() {
		return bidAsk;
	}

	/**
	 * @param bidAsk the bidAsk to set
	 */
	public void setBidAsk(BidAsk bidAsk) {
		this.bidAsk = bidAsk;
	}

	/**
	 * Copy quote attributes. Do not copy previous and next quote references.
	 * 
	 * @param q - quote to copy attributes from
	 */
	public void copyFrom(Quote q) {
		this.setBidAsk(q.getBidAsk());
		this.setDate(q.getDate());
		this.setOpen(q.getOpen());
		this.setHigh(q.getHigh());
		this.setLow(q.getLow());
		this.setClose(q.getClose());
		this.setVolume(q.getVolume());
	}

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
	 * @return the symbol
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public Quote clone() throws CloneNotSupportedException {
		return (Quote) super.clone();
	}
}
