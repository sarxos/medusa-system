package com.sarxos.medusa.market;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Abstract class for signal generators. All common methods is here.
 * 
 * @param <T> - generic parameter - shall extends {@link Quote}
 * @author Bartosz Firyn (SarXos)
 */
public abstract class AbstractGenerator<T extends Quote> implements SignalGenerator<T> {

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SignalGenerator) {

			if (getClass() != obj.getClass()) {
				return false;
			}

			SignalGenerator<? extends Quote> sg = (SignalGenerator<?>) obj;
			Map<String, String> sgparams = sg.getParameters();

			if (sgparams == null) {
				return this.getParameters() == null;
			}

			Map<String, String> params = Collections.unmodifiableMap(this.getParameters());
			Set<Entry<String, String>> entries = params.entrySet();
			Iterator<Entry<String, String>> ei = entries.iterator();
			Entry<String, String> en = null;

			String v, k;

			while (ei.hasNext()) {

				en = ei.next();
				k = en.getKey();
				v = en.getValue();

				if (!v.equals(sgparams.get(k))) {
					return false;
				}
			}

			return true;

		} else {
			return false;
		}
	}

}
