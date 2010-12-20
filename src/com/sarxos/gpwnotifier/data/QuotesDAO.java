package com.sarxos.gpwnotifier.data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sarxos.gpwnotifier.market.Quote;


public class QuotesDAO {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String url = "jdbc:mysql://localhost:3306/gpw"; 

	private Connection con = null;
	
	
	public QuotesDAO() {
		try {
			con = DriverManager.getConnection(url, "root", "Ttxdtd7");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read all quotes for given symbol.
	 * 
	 * @param symbol - symbol to read
	 * @return Return list of all quotes for particular symbol
	 */
	public List<Quote> getQuotes(String symbol) {

		Statement select = null;

		List<Quote> quotes = new LinkedList<Quote>();
		
		try {

			select = con.createStatement();
			ResultSet result = select.executeQuery("SELECT * FROM " + symbol);

			while (result.next()) {
				Date date = new Date(result.getDate("time").getTime());
				float open = result.getFloat("open");
				float high = result.getFloat("high");
				float low = result.getFloat("low");
				float close = result.getFloat("close");
				long volume = result.getLong("volume");
				quotes.add(new Quote(date, open, high, low, close, volume));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return quotes;
	}
}
