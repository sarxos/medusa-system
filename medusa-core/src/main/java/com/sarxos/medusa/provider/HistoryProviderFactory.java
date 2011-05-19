package com.sarxos.medusa.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.util.Configuration;


/**
 * Factory class for history data provider objects.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class HistoryProviderFactory {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Providers.class.getSimpleName());

	/**
	 * Static configuration instance.
	 */
	private static final Configuration CFG = Configuration.getInstance();

	/**
	 * Locally stored instance of history provider.
	 */
	private HistoryProvider provider = null;

	/**
	 * History data provider class name.
	 */
	private String name = null;

	/**
	 * @return Return instance of history data provider
	 */
	public HistoryProvider getProvider() {

		String tmp = CFG.getProperty("data", "history");

		if (tmp == null) {
			return null;
		}

		if (name == null || !name.equals(tmp)) {
			name = tmp;
			Class<?> clazz = null;
			try {
				clazz = Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			if (!HistoryProvider.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(
					"History provider clas have to be a subclass of " +
					HistoryProvider.class.getSimpleName());
			}
			Class<? extends HistoryProvider> pclazz = clazz.asSubclass(HistoryProvider.class);
			try {
				provider = pclazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("History provider has been created: " + provider.getClass().getSimpleName());
			}
		}

		return provider;
	}

}