package com.sarxos.medusa.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.xinotes.iniparser.INIProperties;


/**
 * Medusa configuration class. Operates on medusa.ini file in data directory.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Configuration extends INIProperties {

	/**
	 * Configuration updater class.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class ConfigurationUpdater implements Runnable {

		/**
		 * Store last modified timestamp (when config file was modified last
		 * time).
		 */
		private long timestamp = 0;

		@Override
		public void run() {
			File ini = new File(path);
			long tmp = 0;
			while (true) {
				tmp = ini.lastModified();
				if (tmp != timestamp) {
					try {
						load(new FileInputStream(ini));
					} catch (Exception e) {
						e.printStackTrace();
					}
					timestamp = tmp;
				}
				try {
					Thread.sleep(2 * 60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * @return Return last modification timestamp
		 */
		public long getTimestamp() {
			return timestamp;
		}

		/**
		 * Set last modification timestamp
		 * 
		 * @param timestamp - new time to set
		 */
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
	}

	/**
	 * Singleton instance.
	 */
	private static AtomicReference<Configuration> instance = new AtomicReference<Configuration>();

	/**
	 * Path to the configuration file.
	 */
	private String path = "data/medusa.ini";

	/**
	 * Updater runnable.
	 */
	private ConfigurationUpdater updater = new ConfigurationUpdater();

	/**
	 * Private constructor - this is singleton class.
	 */
	private Configuration() {

		File ini = new File(path);
		loadFile(ini);
		updater.setTimestamp(ini.lastModified());

		Thread runner = new Thread(updater);
		runner.setDaemon(true);
		runner.start();
	}

	/**
	 * Will load configuration from the input ini file.
	 * 
	 * @param ini - ini file to load
	 */
	private void loadFile(File ini) {
		try {
			load(new FileInputStream(ini));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Singleton instance for the Medusa Configuration
	 */
	public static Configuration getInstance() {
		instance.compareAndSet(null, new Configuration());
		return instance.get();
	}
}
