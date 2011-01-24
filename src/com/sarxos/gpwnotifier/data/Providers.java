package com.sarxos.gpwnotifier.data;

import com.sarxos.gpwnotifier.data.bossa.BossaHDProvider;
import com.sarxos.gpwnotifier.data.interia.InteriaRTDProvider;


public class Providers {

	public static RealTimeDataProvider getDefaultRealTimeDataProvider() {
		return new InteriaRTDProvider();
	}
	
	public static HistoricalDataProvider getDefaultHistoricalDataProvider() {
		return new BossaHDProvider();
	}
}
