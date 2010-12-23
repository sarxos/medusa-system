package com.sarxos.gpwnotifier.db;

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

import com.sarxos.gpwnotifier.data.DataFileFormat;
import com.sarxos.gpwnotifier.data.QuotesReader;
import com.sarxos.gpwnotifier.data.QuotesReaderException;
import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.market.Paper;
import com.sarxos.gpwnotifier.market.Quote;
import com.sarxos.gpwnotifier.market.Symbol;


public class DBDAO {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String url = "jdbc:mysql://localhost:3306/gpw"; 

	private Connection con = null;
	
	
	public DBDAO() {
		try {
			con = DriverManager.getConnection(url, "root", "secret");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void ensureSymbolTableExists(Symbol symbol) throws SQLException {
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

	protected void ensureWalletTableExists() throws SQLException {
		Statement create = con.createStatement();
		create.execute(
				"CREATE TABLE IF NOT EXISTS wallet ( " +
				"    symbol VARCHAR(20) NOT NULL PRIMARY KEY, " +
				"    desired INT NOT NULL, " +
				"    quantity INT NOT NULL" +
				")"
		);
	}
	
	public boolean addQuotes(Symbol symbol, List<Quote> quotes) {
		try {
			
			ensureSymbolTableExists(symbol);
			
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
	
	public boolean addPaper(Paper p) {
		
		try {
			
			ensureWalletTableExists();
			
			PreparedStatement insert = con.prepareStatement(
					"INSERT INTO " +
					"    wallet " +
					"VALUES " +
					"    (?, ?, ?) " +
					"ON DUPLICATE KEY UPDATE " +
					"    desired = ?, quantity = ?"
			);
			
			insert.setString(1, p.getSymbol().toString());
			insert.setDouble(2, p.getDesiredQuantity());
			insert.setDouble(3, p.getQuantity());
			insert.setDouble(4, p.getDesiredQuantity());
			insert.setDouble(5, p.getQuantity());
			insert.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return true;
	}
	
	public List<Paper> getPapers() {

		Statement select = null;
		List<Paper> papers = new LinkedList<Paper>();
		
		try {

			ensureWalletTableExists();
			
			select = con.createStatement();
			ResultSet result = select.executeQuery("SELECT * FROM wallet");

			while (result.next()) {
				Symbol symbol = Symbol.valueOf(result.getString("symbol"));
				int desired = result.getInt("desired");
				int quantity = result.getInt("quantity");
				papers.add(new Paper(symbol, desired, quantity));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return papers;
	}
	
	public boolean importData(File f, DataFileFormat format, Symbol symbol) throws QuotesReaderException {

		List<Quote> data = null;

		switch (format) {
			case STOOQ:
				QuotesReader<Quote> reader = new StoqReader<Quote>(Quote.class);
				data = reader.read(f.toURI());
				break;
		}

		try {
			ensureSymbolTableExists(symbol);
			addQuotes(symbol, data);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public static void main(String[] args) throws QuotesReaderException {
		DBDAO dbdao = new DBDAO();
		
		dbdao.addPaper(new Paper(Symbol.KGH, 60, 10));
	}
}
