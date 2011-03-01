package com.sarxos.medusa.comm;

import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.util.Configuration;


/**
 * Messaging policy is used to control flow of messages between Medusa and
 * player. Lets take a look on the example when Medusa discovers few BUY/SELL
 * signals few minutes after another. In this case it will send few acknowledge
 * messages what is undesirable (or desirable - it depends on the messaging
 * policy class implementation -everyone can create his own). For example
 * throttling policy will disallow to send new message if one message for given
 * paper has been already sent (per day quota).
 * 
 * @author Bartosz Firyn (SarXos)
 * @see MessagingPolicy#allows(Symbol)
 */
public abstract class MessagingPolicy {

	/**
	 * Static configuration instance.
	 */
	protected static final Configuration CFG = Configuration.getInstance();

	/**
	 * Policy class name.
	 */
	private static String name = null;

	/**
	 * Policy class instance.
	 */
	private static MessagingPolicy policy = null;

	/**
	 * All extending classes have to call this super constructor!
	 */
	public MessagingPolicy() {
		super();
	}

	/**
	 * @return Return current messaging policy.
	 */
	public static final MessagingPolicy getPolicy() {

		String tmp = CFG.getProperty("messaging", "policy");

		if (name == null || !name.equals(tmp)) {

			name = tmp;
			Class<?> clazz = null;
			try {
				clazz = Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}

			if (!MessagingPolicy.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(
					"Messaging policy have to be a subclass of " +
					MessagingPolicy.class.getSimpleName());
			}

			Class<? extends MessagingPolicy> pclazz = clazz.asSubclass(MessagingPolicy.class);
			try {
				policy = pclazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return policy;
	}

	/**
	 * Return true if messages sending for given symbol is allowed (at the
	 * moment of calling this method), false otherwise.
	 * 
	 * @param symbol - market stock symbol
	 * @return true or false, depending on internal implementation
	 */
	public abstract boolean allows(Symbol symbol);

	/**
	 * Tells messaging policy implementation that message for given symbol has
	 * been sent. Policy implementation can allow or disallow sending new
	 * messages in the configurable time interval. Also other type of policy can
	 * be supported.
	 * 
	 * @param symbol - symbol for which message has been sent
	 */
	public abstract void sent(Symbol symbol);
}
