package com.sarxos.gpwnotifier.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.market.Quote;
import com.sarxos.gpwnotifier.market.Symbol;


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
	
	protected void ensureTableExists(Symbol symbol) throws SQLException {
		Statement create = con.createStatement();
		create.execute(
				"CREATE TABLE IF NOT EXISTS " + symbol + " ( " +
				"    time DATE NOT NULL PRIMARY KEY, " +
				"    open FLOAT NOT NULL, " +
				"    high FLOAT NOT NULL, " +
				"    low FLOAT NOT NULL, " +
				"    close FLOAT NOT NULL, " +
				"    volume BIGINT NOT NULL " +
				")"
		);
	}
	
	public boolean addQuotes(Symbol symbol, List<Quote> quotes) {
		try {
			
			ensureTableExists(symbol);
			
			PreparedStatement insert = con.prepareStatement(
					"INSERT INTO " + symbol + " " +
					"VALUES (?, ?, ?, ?, ?, ?)"
			);
			
			for (Quote quote : quotes) {
				insert.setDate(1, new java.sql.Date(quote.getDate().getTime()));
				insert.setDouble(2, quote.getOpen());
				insert.setDouble(3, quote.getHigh());
				insert.setDouble(4, quote.getLow());
				insert.setDouble(5, quote.getClose());
				insert.setDouble(6, quote.getVolume());
				insert.execute();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Read all quotes for given symbol.
	 * 
	 * @param symbol - symbol to read
	 * @return Return list of all quotes for particular symbol
	 */
	public List<Quote> getQuotes(Symbol symbol) {

		Statement select = null;

		List<Quote> quotes = new LinkedList<Quote>();
		
		try {

			select = con.createStatement();
			ResultSet result = select.executeQuery(
					"SELECT * FROM " + symbol + " ORDER BY time"
			);

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
	
	public boolean importData(File f, DateFileFormat format, Symbol symbol) throws QuotesReaderException {

		List<Quote> data = null;

		switch (format) {
			case STOOQ:
				QuotesReader<Quote> reader = new StoqReader<Quote>(Quote.class);
				data = reader.read(f.toURI());
				break;
		}

		try {
			ensureTableExists(symbol);
			addQuotes(symbol, data);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public static void main(String[] args) throws QuotesReaderException {
		File f = new File("data/kgh_d.csv");
		QuotesDAO qdao = new QuotesDAO();
		qdao.importData(f, DateFileFormat.STOOQ, Symbol.KGH);		
	}
}
