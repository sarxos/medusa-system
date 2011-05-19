package com.sarxos.medusa.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarxos.medusa.util.Configuration;


/**
 * Factory class for real time provider objects.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class RealTimeProviderFactory {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RealTimeProviderFactory.class.getSimpleName());

	/**
	 * Static configuration instance.
	 */
	private static final Configuration CFG = Configuration.getInstance();

	/**
	 * Locally stored real time provider instance.
	 */
	private RealTimeProvider provider = null;

	/**
	 * Real time data provider class name.
	 */
	private String name = null;

	/**
	 * @return Return real time provider instance
	 */
	public RealTimeProvider getProvider() {

		String tmp = CFG.getProperty("data", "realtime");

		if (name == null || !name.equals(tmp)) {
			name = tmp;
			Class<?> clazz = null;
			try {
				clazz = Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			if (!RealTimeProvider.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(
						"Real time provider clas have to be a subclass of " +
						RealTimeProvider.class.getSimpleName());
			}
			Class<? extends RealTimeProvider> pclazz = clazz.asSubclass(RealTimeProvider.class);
			try {
				provider = pclazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Real time provider has been created: " + provider.getClass().getSimpleName());
			}
		}

		return provider;
	}
}