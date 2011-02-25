package com.sarxos.medusa.data;

import com.sarxos.medusa.provider.HistoricalProvider;
import com.sarxos.medusa.provider.RealTimeProvider;
import com.sarxos.medusa.provider.history.BossaProvider;
import com.sarxos.medusa.provider.realtime.InteriaProvider;


public class Providers {

	public static RealTimeProvider getDefaultRealTimeDataProvider() {
		return new InteriaProvider();
	}
	
	public static HistoricalProvider getDefaultHistoricalDataProvider() {
		return new BossaProvider();
	}
}
