package com.sarxos.medusa.db;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Goal of this class is to run MySQL: service and ensure it is running. If
 * service is not running after 10s new <code>RuntimeException</code> is being
 * threw.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class MySQLRunner {

	/**
	 * Static instance - this is singleton.
	 */
	private static AtomicReference<MySQLRunner> instance = new AtomicReference<MySQLRunner>();

	private MySQLRunner() {
	}

	/**
	 * @return Will return static instance of this class.
	 */
	public static MySQLRunner getInstance() {
		if (instance.get() == null) {
			instance.compareAndSet(null, new MySQLRunner());
		}
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
	 * Run MySQL service and ensure it is running.
	 */
	public void runMySQL() {

		if (!isMySQLRunning()) {

			runMySQLOnce();

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

			System.out.println("Service MySQL is running");
		}
	}

	/**
	 * Run MySQL service.
	 */
	protected void runMySQLOnce() {

		ProcessBuilder pb = new ProcessBuilder();
		String os = System.getProperty("os.name");

		if ("Windows XP".equals(os)) {
			pb = pb.command("net", "start", "mysql");
		} else if ("Linux".equals(os)) {
			pb = pb.command("service", "mysql", "start");
		} else {
			throw new RuntimeException("Unsupported operating system '" + os + "'");
		}

		System.out.println("Starting MySQL service");

		try {
			pb.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
