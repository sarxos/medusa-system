package com.sarxos.medusa.provider;

import com.sarxos.medusa.util.Configuration;


/**
 * Utility class used to obtain most current instances of history and real time
 * data providers. As you know each of the providers implementing classes can be
 * changed in the Medusa configuration, so different kind of data provider can
 * be loaded in the runtime. This class make it simpler - just call one of the
 * history- or realtime-corresponding getter to obtain the most actual instance
 * of given provider implementation.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Providers {

	/**
	 * Static configuration instance.
	 */
	private static final Configuration CFG = Configuration.getInstance();

	/**
	 * Factory class for real time provider objects.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static class RealTimeProviderFactory {

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
			}

			return provider;
		}
	}

	/**
	 * Factory class for history data provider objects.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	public static class HistoryProviderFactory {

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
			}

			return provider;
		}

	}

	/**
	 * Static instance of real time data provider factory.
	 */
	private static RealTimeProviderFactory realTimeFactory = new RealTimeProviderFactory();

	/**
	 * Static instance of history data provider factory.
	 */
	private static HistoryProviderFactory historyFactory = new HistoryProviderFactory();

	/**
	 * @return Return most current real time data provider instance
	 */
	public static RealTimeProvider getRealTimeProvider() {
		return realTimeFactory.getProvider();
	}

	/**
	 * @return Return most current history data provider instance
	 */
	public static HistoryProvider getHistoryProvider() {
		return historyFactory.getProvider();
	}
}
