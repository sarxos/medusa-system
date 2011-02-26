package com.sarxos.medusa.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
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

import com.sarxos.medusa.data.persistence.PersistenceException;
import com.sarxos.medusa.data.persistence.PersistenceProvider;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Position;
import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.SignalGenerator;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.trader.Trader;
import com.sarxos.medusa.util.Configuration;
import com.sarxos.medusa.util.StoqReader;


public class DBDAO implements PersistenceProvider {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static Configuration cfg = Configuration.getInstance();

	private static String url = null;
	static {
		String host = cfg.getProperty("database", "host");
		String port = cfg.getProperty("database", "port");
		String name = cfg.getProperty("database", "name");
		url = "jdbc:mysql://" + host + ":" + port + "/" + name;
	}

	private Connection con = null;

	private SQLFileReader sqlreader = new SQLFileReader();

	private static AtomicReference<DBDAO> instance = new AtomicReference<DBDAO>();

	private DBDAO() throws DBDAOException {

		String usr = cfg.getProperty("database", "user");
		String pwd = cfg.getProperty("database", "password");

		try {

			con = DriverManager.getConnection(url, usr, pwd);

			// TODO iterate via directory and install all
			installProcedure("GetQuotes");
			installProcedure("AddPaper");
			installProcedure("UpdatePaper");
			installProcedure("GetPapers");
			installProcedure("RemovePaper");
			installProcedure("AddTrader");
			installProcedure("GetTrader");
			installProcedure("RemoveTrader");
			installProcedure("GetTraders");
		} catch (Exception e) {
			throw new DBDAOException(e);
		}
	}

	/**
	 * @return DBDAO static instance
	 */
	public static DBDAO getInstance() {
		try {
			if (instance.get() == null) {
				instance.compareAndSet(null, new DBDAO());
			}
		} catch (DBDAOException e) {
			e.printStackTrace();
		}
		return instance.get();
	}

	protected void installProcedure(String name) throws IOException, SQLException {

		String sql = sqlreader.getSQL(name);
		Statement st = null;

		st = con.createStatement();
		st.execute("DROP PROCEDURE IF EXISTS " + name);
		st.close();

		st = con.createStatement();
		st.execute(sql);
		st.close();
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
		create.close();
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
		create.close();
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

			insert.close();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
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

			Quote q = null, p = null;

			while (result.next()) {

				p = q;

				Date date = new Date(result.getDate("time").getTime());
				float open = result.getFloat("open");
				float high = result.getFloat("high");
				float low = result.getFloat("low");
				float close = result.getFloat("close");
				long volume = result.getLong("volume");

				q = new Quote(date, open, high, low, close, volume);

				if (p != null) {
					q.setPrev(p);
					p.setNext(q);
				}

				quotes.add(q);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return quotes;
	}

	public boolean addPaper(Paper p) {

		PreparedStatement add = null;
		try {
			ensureWalletTableExists();

			add = con.prepareStatement("CALL AddPaper(?, ?, ?)");
			add.setString(1, p.getSymbol().toString());
			add.setDouble(2, p.getQuantity());
			add.setDouble(3, p.getDesiredQuantity());
			add.execute();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (add != null) {
				try {
					add.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public boolean updatePaper(Paper p) {

		try {

			ensureWalletTableExists();

			PreparedStatement update = con.prepareStatement("CALL UpdatePaper(?, ?, ?)");

			update.setString(1, p.getSymbol().toString());
			update.setDouble(2, p.getQuantity());
			update.setDouble(3, p.getDesiredQuantity());
			update.execute();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean removePaper(Paper p) {

		PreparedStatement update = null;
		try {
			ensureWalletTableExists();

			update = con.prepareStatement("CALL RemovePaper(?, ?, ?)");
			update.setString(1, p.getSymbol().toString());
			update.execute();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (update != null) {
				try {
					update.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
		} finally {
			if (select != null) {
				try {
					select.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return papers;
	}

	/**
	 * Add trader to the DB.
	 * 
	 * @param trader - trader to add
	 * @return true if trader has been added, false otherwise
	 * @throws DBDAOException
	 */
	public boolean addTrader(Trader trader) throws DBDAOException {

		PreparedStatement add = null;
		try {

			add = con.prepareStatement("CALL AddTrader(?, ?, ?, ?, ?, ?)");
			add.setString(1, trader.getName());
			add.setString(2, trader.getSymbol() == null ? null : trader.getSymbol().toString());
			add.setInt(3, trader.getPosition() == Position.SHORT ? 0 : 1);
			add.setString(4, trader.getGeneratorClassName());
			add.setString(5, trader.getClass().getName());
			add.setString(6, Marshaller.marshalGenParams(trader.getGenerator()));
			add.execute();

			return true;

		} catch (SQLException e) {
			throw new DBDAOException(e);
		} finally {
			if (add != null) {
				try {
					add.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Update trader in the DB.
	 * 
	 * @param trader - trader to update
	 * @return true if trader has been updated, false otherwise
	 * @throws DBDAOException
	 */
	public boolean updateTrader(Trader trader) throws DBDAOException {
		return addTrader(trader);
	}

	/**
	 * Read given trader from the DB.
	 * 
	 * @param name - trader's name to read
	 * @return Will return trader or null if something is wrong
	 * @throws DBDAOException
	 */
	public Trader getTrader(String name) throws DBDAOException {

		PreparedStatement get = null;
		try {
			get = con.prepareStatement("CALL GetTrader(?)");
			get.setString(1, name);

			ResultSet rs = get.executeQuery();

			List<Trader> traders = resultSetToTraders(rs);
			if (traders.size() > 0) {
				return traders.get(0);
			} else {
				return null;
			}

		} catch (Exception e) {
			throw new DBDAOException(e);
		} finally {
			if (get != null) {
				try {
					get.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return Return all traders
	 * @throws DBDAOException
	 */
	public List<Trader> getTraders() throws DBDAOException {

		PreparedStatement get = null;
		try {
			get = con.prepareStatement("CALL GetTraders()");

			ResultSet rs = get.executeQuery();

			return resultSetToTraders(rs);

		} catch (Exception e) {
			throw new DBDAOException(e);
		} finally {
			if (get != null) {
				try {
					get.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Remove trader from the DB.
	 * 
	 * @param name - trader's name to remove
	 * @return true in case of successful operation, false otherwise
	 * @throws DBDAOException
	 */
	public boolean removeTrader(String name) throws DBDAOException {

		PreparedStatement get = null;
		try {
			get = con.prepareStatement("CALL RemoveTrader(?)");
			get.setString(1, name);
			get.execute();

			return true;
		} catch (Exception e) {
			throw new DBDAOException(e);
		} finally {
			if (get != null) {
				try {
					get.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<Trader> resultSetToTraders(ResultSet rs) throws DBDAOException {

		Class<?> clazz = null;
		List<Trader> traders = new LinkedList<Trader>();

		try {
			while (rs.next()) {

				String name = rs.getString("name");
				Symbol symbol = Symbol.valueOf(rs.getString("symbol"));

				clazz = Class.forName(rs.getString("siggen"));
				Map<String, String> params = Marshaller.unmarshalGenParams(rs.getString("params"));
				SignalGenerator<Quote> siggen = (SignalGenerator<Quote>) clazz.newInstance();
				siggen.setParameters(params);

				clazz = Class.forName(rs.getString("class"));
				Constructor<?> cnstr = clazz.getConstructor(String.class, SignalGenerator.class, Symbol.class);
				Trader t = (Trader) cnstr.newInstance(name, siggen, symbol);

				t.setPosition(rs.getInt("position") == 0 ? Position.SHORT : Position.LONG);

				traders.add(t);
			}
		} catch (Exception e) {
			throw new DBDAOException(e);
		}
		return traders;
	}

	public boolean importData(File f, DataFileFormat format, Symbol symbol) throws QuotesReaderException {

		List<Quote> data = null;

		switch (format) {
			case STOOQ:
				QuotesRemoteReader<Quote> reader = new StoqReader<Quote>(Quote.class);
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

	// ////////////// NEW API /////////////////

	@Override
	public boolean saveTrader(Trader trader) throws PersistenceException {
		try {
			return addTrader(trader);
		} catch (DBDAOException e) {
			throw new PersistenceException(e);
		}
	}

}
