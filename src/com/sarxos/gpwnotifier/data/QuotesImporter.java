package com.sarxos.gpwnotifier.data;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.sarxos.gpwnotifier.data.stoq.StoqReader;
import com.sarxos.gpwnotifier.market.Quote;


public class QuotesImporter {

	public static void main(String[] args) throws QuotesReaderException {


		// TODO parametrize
		File f = new File("data/kgh_d.csv");

		// TODO parametrize
		QuotesImporter.importData(f, DateFileFormat.STOOQ, "KGH");


	}

	public static boolean importData(File f, DateFileFormat format, String symbol) throws QuotesReaderException {

		List<Quote> data = null;

		switch (format) {
			case STOOQ:
				QuotesReader<Quote> reader = new StoqReader<Quote>(Quote.class);
				data = reader.read(f.toURI());
				break;
		}

		// TODO XXX move to QuotesDAO
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String url = "jdbc:mysql://localhost:3306/gpw";

		Connection con = null;
		Statement create = null;
		PreparedStatement insert = null;

		try {
			con = DriverManager.getConnection(url, "root", "Ttxdtd7");

			create = con.createStatement();
			create.execute(
					"CREATE TABLE IF NOT EXISTS " + symbol + " ( " +
					"    time DATE, " +
					"    open FLOAT, " +
					"    high FLOAT, " +
					"    low FLOAT, " +
					"    close FLOAT, " +
					"    volume BIGINT " +
					")"
			);

			insert = con.prepareStatement(
					"INSERT INTO " + symbol + " " +
					"VALUES (?, ?, ?, ?, ?, ?)"
			);

			for (Quote quote : data) {
				insert.setDate(1, new Date(quote.getDate().getTime()));
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

}
