package com.sarxos.gpwnotifier.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class SQLFileReader {

	// TODO make it configurable
	public static final String SQL_STORAGE = "db";
	
	public String getSQL(String name) throws IOException {
		
		File file = new File(SQL_STORAGE + "/" + name + ".sql");
		
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		
		while (br.ready()) {
			sb.append(br.readLine()).append(" \n");
		}
		
		return sb.toString();
	}
} 
