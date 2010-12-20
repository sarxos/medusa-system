package com.mac.verec.models ;

import java.util.Date ;

/**
 * Encapsulates that date, open, high, low and close value for a given
 * instrument on that particular date.
 * @see Instrument
 */
public class Quote {

	/** The date corresponding to the OHLC fields below */
	public Date		date ;

	/** The O of OHLC */
	public float	open ;

	/** The H of OHLC */
	public float	high ;

	/** The L of OHLC */
	public float	low ;

	/** The C of OHLC */
	public float	close ;

	/** The Open Interest */
	public float	interest ;
	
	/** The volume */
	public float	volume ;
	
	/** Default constructor. */
	public
	Quote(	Date	date 
		,	float	open 
		,	float	high 
		,	float	low
		,	float	close 
		,	float	interest
		,	float	volume) {

		this.date = date ;
		this.open = open ;
		this.high = high ;
		this.low = low ;
		this.close = close ;
		this.interest = interest ;
		this.volume = volume ;
	}
	
	/** Copy constructor. */
	public Quote(
		Quote other) {

		this.date = other.date ;
		this.open = other.open ;
		this.high = other.high ;
		this.low = other.low ;
		this.close = other.close ;
		this.interest = other.interest ;
		this.volume = other.volume ;
	}
	
	public void
	reset() {
	}

	/** Just so that dumps are a bit more menaingful. */
	public String
	toString() {
		return 	"Date: " + date +
				", open:" + open + 
				", high:" + high + 
				", low:" + low + 
				", close:" + close + 
				", interest:" + interest + 
				", volume:" + volume ;
	}
}
