package com.sarxos.medusa.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.sarxos.medusa.data.DataFileFormat;
import com.sarxos.medusa.data.PersistanceProvider;
import com.sarxos.medusa.data.QuotesReader;
import com.sarxos.medusa.data.QuotesReaderException;
import com.sarxos.medusa.data.stoq.StoqReader;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.trader.Trader;


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
	
	private SQLFileReader sqlreader = new SQLFileReader();

	private static AtomicReference<DBDAO> instance = new AtomicReference<DBDAO>();
	
	
	private DBDAO() {
		try {
			con = DriverManager.getConnection(url, "root", "secret");
			// TODO iterate via directory and install all
			installProcedure("GetQuotes");
			installProcedure("AddPaper");
			installProcedure("UpdatePaper");
			installProcedure("GetPapers");
			installProcedure("RemovePaper");
			installProcedure("AddTrader");
			installProcedure("GetTrader");
			installProcedure("GetTraders");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return DBDAO static instance
	 */
	public static DBDAO getInstance() {
		instance.compareAndSet(null, new DBDAO());
		return instance.get();
	}
	
	protected void installProcedure(String name) throws IOException, SQLException {
		String sql = sqlreader.getSQL(name);
		Statement st = con.createStatement();
		st.execute("DROP PROCEDURE IF EXISTS " + name);
		st.execute(sql);
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

		PreparedStatement getQuoets = null;

		List<Quote> quotes = new LinkedList<Quote>();
		
		try {

			getQuoets = con.prepareStatement("CALL GetQuotes(?)");
			getQuoets.setString(1, symbol.toString());
			
			ResultSet result = getQuoets.executeQuery();

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
			
			PreparedStatement addPaper = con.prepareStatement("CALL AddPaper(?, ?, ?)");
			addPaper.setString(1, p.getSymbol().toString());
			addPaper.setDouble(2, p.getQuantity());
			addPaper.setDouble(3, p.getDesiredQuantity());
			addPaper.execute();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return false;
	}
	
	public boolean updatePaper(Paper p) {
		
		try {
			
			ensureWalletTableExists();
			
			PreparedStatement updateWallet = con.prepareStatement("CALL UpdatePaper(?, ?, ?)");
			
			updateWallet.setString(1, p.getSymbol().toString());
			updateWallet.setDouble(2, p.getQuantity());
			updateWallet.setDouble(3, p.getDesiredQuantity());
			updateWallet.execute();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return false;
	}	
	
	public boolean removePaper(Paper p) {
		
		try {
			
			ensureWalletTableExists();
			
			PreparedStatement updateWallet = con.prepareStatement("CALL RemovePaper(?, ?, ?)");
			updateWallet.setString(1, p.getSymbol().toString());
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return false;
	}	
	
	public List<Paper> getPapers() {

		Statement select = null;
		List<Paper> papers = new LinkedList<Paper>();
		
		try {

			ensureWalletTableExists();
			
			select = con.createStatement();
			ResultSet result = select.executeQuery("CALL GetPapers()");

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
	
	public boolean addTrader(Trader trader) {
		
		try {
			
			PreparedStatement addTrader = con.prepareStatement("CALL AddTrader(?, ?, ?, ?)");
			
			addTrader.setString(1, trader.getName());
			addTrader.setString(2, trader.getSymbol() == null ? null : trader.getSymbol().toString());
			addTrader.setString(3, trader.getGeneratorClassName());
			addTrader.setString(4, PersistanceProvider.marshalGenParams(trader.getGenerator()));
			addTrader.execute();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return false;
	}
	
	public Trader getTrader(String name) {
		
		try {
			PreparedStatement get = con.prepareStatement("CALL GetTrader(?)");
			get.setString(1, name);
			ResultSet rs = get.executeQuery();
			
			List<Trader> traders = resultSetToTraders(rs);
			if (traders.size() > 0) {
				return traders.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}

	public List<Trader> getTraders() {
		
		try {
			PreparedStatement get = con.prepareStatement("CALL GetTraders()");
			ResultSet rs = get.executeQuery();
			
			return resultSetToTraders(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<Trader> resultSetToTraders(ResultSet rs) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<Trader> traders = new LinkedList<Trader>();
		while (rs.next()) {
			String name = rs.getString("name");
			Symbol symbol = Symbol.valueOf(rs.getString("symbol"));
			Class<?> clazz = Class.forName(rs.getString("siggen"));
			Map<String, Object> params = PersistanceProvider.unmarshalGenParams(rs.getString("params"));
			SignalGenerator<Quote> siggen = (SignalGenerator<Quote>) clazz.newInstance();
			siggen.setParameters(params);
			traders.add(new Trader(name, siggen, symbol));
		}
		return traders;
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
		dbdao.updatePaper(new Paper(Symbol.KGH, 20, 10));
		
		System.out.println(dbdao.getQuotes(Symbol.KGH).size());
	}
}
