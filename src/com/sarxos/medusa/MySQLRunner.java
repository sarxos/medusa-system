package com.sarxos.medusa;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;


public class MySQLRunner {

	private static AtomicReference<MySQLRunner> instance = new AtomicReference<MySQLRunner>();
	
	
	private MySQLRunner() {
	}
	
	protected static MySQLRunner getInstance() {
		instance.compareAndSet(null, new MySQLRunner());
		return instance.get();
	}
	
	/**
	 * Check if MySQL service is running.
	 * 
	 * @return true if it is running, false otherwise
	 */
	public boolean isMySQLRunning() {
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", 3306);
		} catch (IOException e) {
			return false;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return socket != null;
	}
	
	/**
	 * Run MySQL service.
	 */
	public void runMySQL() {
		
		if (!isMySQLRunning()) {

			ProcessBuilder pb = new ProcessBuilder();
			String os = System.getProperty("os.name");
			
			if ("Windows XP".equals(os)) {
				pb = pb.command("net", "start", "mysql");
			} else if ("Linux".equals(os)) {
				pb = pb.command("service", "mysql", "start");
			} else {
				throw new RuntimeException("Unsupported Operating System '" + os + "'");
			}
			
			try {
				pb.start();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			int i = 0;
			while (!isMySQLRunning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (i++ > 10) {
					throw new RuntimeException("MySQL service is not running");
				}
			}
			
		}
	}
}
