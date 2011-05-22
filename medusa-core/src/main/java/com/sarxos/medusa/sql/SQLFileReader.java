package com.sarxos.medusa.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sarxos.medusa.util.Configuration;


/**
 * Read SQL files with stored procedures and return given procedure definition.
 * It is used to install newest procedure in the DB.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class SQLFileReader {

	/**
	 * Stored procedures storage directory - configurable.
	 */
	public static final String STORAGE = Configuration.getInstance().getProperty("core", "procedures");

	/**
	 * Return SQL for given stored procedure.
	 * 
	 * @param name - stored procedure name
	 * @return Return SQL string
	 * @throws IOException if file with stored procedure does not exist
	 * @throws RuntimeException in case when existing file does not contain
	 *             correct procedure
	 */
	public String getSQL(String name) throws IOException {

		File file = new File(STORAGE + "/" + name + ".sql");

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();

		while (br.ready()) {
			sb.append(br.readLine()).append(" \n");
		}

		String procedure = sb.toString();

		if (procedure.indexOf("CREATE PROCEDURE " + name) == -1) {
			throw new RuntimeException("Incorrect procedure in file " + file.getPath());
		}

		return sb.toString();
	}
}
