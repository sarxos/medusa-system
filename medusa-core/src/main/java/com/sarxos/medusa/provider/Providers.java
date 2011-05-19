package com.sarxos.medusa.provider;



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
	 * Static instance of real time data provider factory.
	 */
	private static RealTimeProviderFactory rtf = new RealTimeProviderFactory();

	/**
	 * Static instance of history data provider factory.
	 */
	private static HistoryProviderFactory hf = new HistoryProviderFactory();

	/**
	 * @return Return most current real time data provider instance
	 */
	public static RealTimeProvider getRealTimeProvider() {
		return rtf.getProvider();
	}

	/**
	 * @return Return most current history data provider instance
	 */
	public static HistoryProvider getHistoryProvider() {
		return hf.getProvider();
	}
}
