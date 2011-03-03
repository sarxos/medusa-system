package com.sarxos.medusa.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple expire set implementation.
 * 
 * @param <T> - generic type argument for expire set
 * @author Bartosz Firyn (SarXos)
 */
public class ExpireSet<T> extends LinkedHashSet<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ExpireSet.class);

	/**
	 * Set cleaner. This runnable find expired elements and remove them from the
	 * set.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class Cleaner implements Runnable {

		/**
		 * Mutex for set iterations.
		 */
		private ExpireSet<T> mutex = null;

		/**
		 * Create new cleaner.
		 * 
		 * @param mutex - mutex for set iterations
		 */
		public Cleaner(ExpireSet<T> mutex) {
			this.mutex = mutex;
		}

		@Override
		public void run() {

			List<T> remove = new LinkedList<T>();

			while (true) {

				try {
					Thread.sleep(cleanPeriod);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				long now = System.nanoTime();
				long diff = now - expiration * (long) 1E+6;

				Set<Entry<T, Long>> entries = mapping.entrySet();
				Iterator<Entry<T, Long>> ei = entries.iterator();
				Entry<T, Long> entry = null;

				synchronized (mutex) {
					while (ei.hasNext()) {
						entry = ei.next();
						if (entry.getValue().longValue() < diff) {
							ei.remove();
							remove.add(entry.getKey());
							LOG.info("Expired element '" + entry.getKey() + "' has been removed");
						}
					}
				}

				for (T element : remove) {
					remove(element);
				}

				remove.clear();
			}
		}
	}

	/**
	 * Elements expiration time.
	 */
	private long expiration = 2 * 60 * 1000; // 2 minutes

	/**
	 * Cleaning period.
	 */
	private long cleanPeriod = 0;

	/**
	 * Object - timestamp mapping
	 */
	private Map<T, Long> mapping = new HashMap<T, Long>();

	/**
	 * Set cleaner.
	 */
	private Cleaner cleaner = new Cleaner(this);

	/**
	 * CRunner thread for set cleaner.
	 */
	private Thread runner = null;

	/**
	 * Create expire set with default expiration time set to 2 minutes.
	 */
	public ExpireSet() {
		this(2, TimeUnit.MINUTES);
	}

	/**
	 * Create expire set with given expiration time. Do not forget to pass
	 * correct time unit.
	 * 
	 * @param expiration - expiration time
	 * @param unit - time unit
	 */
	public ExpireSet(long expiration, TimeUnit unit) {
		super();
		this.setExpiration(expiration, unit);

	}

	/**
	 * Create expire set with given expiration time. Do not forget to pass
	 * correct time unit.
	 * 
	 * @param expiration - expiration time
	 * @param period - cleaning period
	 * @param unit - time unit to use
	 */
	public ExpireSet(long expiration, long period, TimeUnit unit) {
		super();
		this.setExpiration(expiration, unit);
		this.setCleaninigPeriod(period, unit);
	}

	/**
	 * @return Runner thread for cleaning runnable
	 */
	private Thread createRunner() {
		Thread runner = new Thread(cleaner, "ExpireSet[" + hashCode() + "]Cleaner");
		runner.setDaemon(true);
		return runner;
	}

	@Override
	public synchronized boolean add(T e) {
		if (runner == null) {
			this.runner = createRunner();
			this.runner.start();
		}
		boolean added = super.add(e);
		mapping.put(e, Long.valueOf(System.nanoTime()));
		return added;
	}

	@Override
	public synchronized boolean remove(Object o) {
		boolean removed = super.remove(o);
		if (removed) {
			mapping.remove(o);
		}
		return removed;
	}

	@Override
	public synchronized boolean contains(Object o) {
		long diff = System.nanoTime() - expiration * (long) 1E+6;
		Long l = mapping.get(o);
		if (l != null && l.longValue() < diff) {
			remove(o);
			mapping.remove(o);
			return false;
		}
		return super.contains(o);
	}

	/**
	 * @return Return expiration time (<b>always</b> in seconds)
	 */
	public long getExpiration() {
		return expiration;
	}

	/**
	 * Set new expiration time in seconds.
	 * 
	 * @param expiration - new expiration time to set (in seconds)
	 */
	public void setExpiration(long expiration) {
		setExpiration(expiration, TimeUnit.SECONDS);
	}

	/**
	 * Set new expiration time. Element will expire and will be removed after
	 * this time.
	 * 
	 * @param expiration - new expiration time to set
	 * @param unit - time unit
	 */
	public void setExpiration(long expiration, TimeUnit unit) {
		switch (unit) {
			case DAYS:
				expiration *= 24;
			case HOURS:
				expiration *= 60;
			case MINUTES:
				expiration *= 60;
			case SECONDS:
				expiration *= 1000;
			case MILLISECONDS:
				break;
			case NANOSECONDS:
				expiration /= 1000;
			case MICROSECONDS:
				expiration /= 1000;
		}
		this.expiration = expiration;
	}

	/**
	 * @return Return cleaning period (<b>always</b> in seconds)
	 */
	public long getCleaninigPeriod() {
		return cleanPeriod;
	}

	/**
	 * Set new clean period
	 * 
	 * @param period - new cleaning period to set
	 * @param unit - cleaning period time unit
	 */
	protected void setCleaninigPeriod(long period, TimeUnit unit) {
		switch (unit) {
			case DAYS:
				period *= 24;
			case HOURS:
				period *= 60;
			case MINUTES:
				period *= 60;
			case SECONDS:
				period *= 1000;
			case MILLISECONDS:
				break;
			case NANOSECONDS:
				period /= 1000;
			case MICROSECONDS:
				period /= 1000;
		}
		this.cleanPeriod = period;
	}
}
