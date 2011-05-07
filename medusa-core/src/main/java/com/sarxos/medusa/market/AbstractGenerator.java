package com.sarxos.medusa.market;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract class for signal generators. All common methods is here.
 * 
 * @param <T> - generic parameter - shall extends {@link Quote}
 * @author Bartosz Firyn (SarXos)
 */
public abstract class AbstractGenerator<T extends Quote> implements SignalGenerator<T> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractGenerator.class.getSimpleName());

	/**
	 * Shall resultant signal handle internal calculation values in map?
	 */
	private boolean outputting = false;

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

	/**
	 * @return Return true if resultant signal handle internal calculation
	 *         values in map, false otherwise.
	 */
	public boolean isOutputting() {
		return outputting;
	}

	/**
	 * Shall resultant signal handle internal calculation values in map?
	 * 
	 * @param outputting
	 */
	public void setOutputting(boolean outputting) {
		this.outputting = outputting;
		if (LOG.isDebugEnabled()) {
			LOG.debug(
				"Signal generator " + getClass().getSimpleName() + " " +
				"will create output content");
		}
	}
}
