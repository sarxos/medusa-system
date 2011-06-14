package com.sarxos.medusa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xinotes.iniparser.INIProperties;


/**
 * Medusa configuration class. Operates on medusa.ini file in data directory.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Configuration extends INIProperties {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Configuration.class.getSimpleName());

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

				LOG.debug("Checking configuration changes");

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
	private static Configuration instance = new Configuration();

	/**
	 * Path to the configuration file.
	 */
	private String path = null;

	/**
	 * Updater runnable.
	 */
	private ConfigurationUpdater updater = new ConfigurationUpdater();

	/**
	 * Private constructor - this is singleton class.
	 */
	private Configuration() {

		super();

		path = getHomePath();
		if (path != null) {
			path += "/";
		} else {
			path = "";
		}
		path += "data/medusa.ini";

		File ini = new File(path);
		loadFile(ini);
		updater.setTimestamp(ini.lastModified());

		Thread runner = new Thread(updater, "ConfigurationUpdater");
		runner.setDaemon(true);
		runner.start();

		LOG.info("Starting configuration updater");

	}

	/**
	 * Will load configuration from the input ini file.
	 * 
	 * @param ini - ini file to load
	 */
	private void loadFile(File ini) {
		try {
			load(new FileInputStream(ini));

			if (LOG.isInfoEnabled()) {
				LOG.info("Configuration loaded from " + ini.getPath());
			}

		} catch (FileNotFoundException e) {
			LOG.error("Configuration file '" + ini.getPath() + "' has not been found");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * @return Singleton instance for the Medusa Configuration
	 */
	public static Configuration getInstance() {
		return instance;
	}

	public String getProperty(String section, String name, String def) {
		String prop = super.getProperty(section, name);
		if (prop == null) {
			prop = def;
		}
		return prop;
	}

	protected String getHomePath() {
		// TODO why env variable returns null ???
		// path = System.getenv("MEDUSA_HOME");
		return "\\usr\\medusa";
	}

	public String getString(String section, String name) {
		return getString(section, name, null);
	}

	public String getString(String section, String name, String def) {
		return getProperty(section, name, def);
	}

	public boolean getBool(String section, String name) {
		return getBool(section, name, false);
	}

	public boolean getBool(String section, String name, boolean def) {
		String b = getProperty(section, name, Boolean.toString(def));
		return Boolean.parseBoolean(b);
	}

	public int getInt(String section, String name) {
		return getInt(section, name, 0);
	}

	public int getInt(String section, String name, int def) {
		String i = getProperty(section, name, Integer.toString(def));
		return Integer.parseInt(i);
	}

	public String getPath(String section, String name) {
		return getHomePath() + "/" + getString(section, name);
	}
}
