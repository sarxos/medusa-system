package com.sarxos.medusa.data;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.util.Configuration;


/**
 * Useful database methods. 
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DB {

	public static final String STORED_PROC_PATH = Configuration.getInstance().getProperty("core", "procedures");

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DB.class.getSimpleName());
	
	
	/**
	 * SQL file name filter.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected static class SQLFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith("sql");
		}
	}
	
	/**
	 * Install all stored procedures from the Medusa stored procedures
	 * directory.
	 * 
	 * @throws SQLException if stored procedures directory is empty or
	 * 			other reason
	 */
	public static void installProcedures(Connection conn) throws SQLException {
		
		File dir = new File(STORED_PROC_PATH);
		String[] paths = dir.list(new SQLFileFilter());

		if (paths.length > 0) {
			
			SQLFileReader reader = new SQLFileReader();
			String name = null;
			
			for (String path : paths) {
				name = path.substring(0, path.lastIndexOf(".sql"));
				try {
					installProcedure(reader, conn, name);
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		} else {
			throw new SQLException("Stored procedures directory is empty");
		}
	}
	
	/**
	 * Install single stored procedure.
	 * 
	 * @param reader - SQL file reader
	 * @param conn - connection object
	 * @param name - stored procedure name
	 * @throws IOException if reader cannot read SQL file
	 * @throws SQLException if something is wrong with SQL
	 */
	private static void installProcedure(SQLFileReader reader, Connection conn, String name) throws IOException, SQLException {

		String sql = reader.getSQL(name);
		Statement st = null;

		st = conn.createStatement();
		st.execute("DROP PROCEDURE IF EXISTS " + name);
		st.close();

		st = conn.createStatement();
		st.execute(sql);
		st.close();
	}
}
