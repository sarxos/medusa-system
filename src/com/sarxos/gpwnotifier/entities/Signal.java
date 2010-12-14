package com.sarxos.gpwnotifier.entities;

import java.util.Date;



public class Signal {

	private SignalType type = null;
	
	private Date date = null;
	
	private Quote quote = null;
	
	private double level = 0;

	
	public double getLevel() {
		return level;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	public Signal() {
		super();
	}
	
	public Signal(Quote quote, SignalType type) {
		super();
		this.quote = quote;
		this.date = quote.getDate();
		this.type = type;
	}	
	
	public Signal(Date date, SignalType type) {
		super();
		this.date = date;
		this.type = type;
	}

	public Signal(Date date, SignalType type, Quote quote) {
		super();
		this.date = date;
		this.type = type;
		this.quote = quote;
	}

	public Signal(Date date, SignalType type, Quote quote, double level) {
		super();
		this.date = date;
		this.type = type;
		this.quote = quote;
		this.level = level;
	}
	
	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public SignalType getType() {
		return type;
	}

	public void setType(SignalType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	} 
}
