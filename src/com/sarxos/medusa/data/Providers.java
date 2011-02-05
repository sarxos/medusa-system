package com.sarxos.medusa.data;

import com.sarxos.medusa.data.bossa.BossaHDProvider;
import com.sarxos.medusa.data.interia.InteriaRTDProvider;


public class Providers {

	public static RealTimeDataProvider getDefaultRealTimeDataProvider() {
		return new InteriaRTDProvider();
	}
	
	public static HistoricalDataProvider getDefaultHistoricalDataProvider() {
		return new BossaHDProvider();
	}
}
