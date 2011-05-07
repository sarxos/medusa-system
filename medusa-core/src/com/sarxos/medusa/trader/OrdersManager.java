package com.sarxos.medusa.trader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.sarxos.medusa.market.Account;
import com.sarxos.medusa.market.BidAsk;
import com.sarxos.medusa.market.Brokerage;
import com.sarxos.medusa.market.Order;
import com.sarxos.medusa.market.OrderStatus;
import com.sarxos.medusa.market.Symbol;
import com.sarxos.medusa.market.order.TrailingStop;


/**
 * Manage orders in brokerage
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class OrdersManager implements PriceListener {

	/**
	 * Threads factory.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	private class ManagerThreadsFactory implements ThreadFactory {

		/**
		 * Thread number.
		 */
		private int number = 0;

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "OrdersManagerThread-" + (number++));
			return t;
		}
	}

	/**
	 * List of orders in brokerage
	 */
	private final List<Order> orders = new LinkedList<Order>();

	/**
	 * Executor service
	 */
	private ExecutorService executor = Executors.newCachedThreadPool(new ManagerThreadsFactory());

	/**
	 * This runnable will update orders.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	private class UpdateOrders implements Runnable {

		PriceEvent pe = null;

		public UpdateOrders(PriceEvent pe) {
			this.pe = pe;
		}

		@Override
		public void run() {

			Symbol es = pe.getQuote().getSymbol();

			if (es == null) {
				throw new RuntimeException("Price event symbol cannot be null");
			}

			Iterator<Order> oi = orders.iterator();
			while (oi.hasNext()) {

				Order order = oi.next();

				// omit if not active
				if (order.getStatus() != OrderStatus.ACTIVE) {
					continue;
				}

				Symbol os = order.getPaper().getSymbol();

				if (os == null) {
					throw new RuntimeException("Order symbol cannot be null");
				} else if (os == es) {
					if (order instanceof TrailingStop) {
						evaluateTrailingStop((TrailingStop) order, pe);
					}
				}
			}
		}
	}

	@Override
	public void priceChange(PriceEvent pe) {
		executor.execute(new UpdateOrders(pe));
	}

	/**
	 * Evaluate new trailing stop price limit and update order if it valid for
	 * new conditions.
	 * 
	 * @param order - trailing stop order
	 * @param pe - price change event
	 */
	private void evaluateTrailingStop(TrailingStop order, PriceEvent pe) {

		checkSymbols(order, pe);

		double price = 0;
		double limit = order.getActivationLimit();
		double threshold = order.getThreshold();
		double update = 0;

		BidAsk ba = pe.getQuote().getBidAsk();

		switch (order.getDirection()) {
			case SELL:
				price = ba.getAsk();
				update = price - price * threshold;
				if (update > limit) {
					order.setActivationLimit(update);
					updateOrder(order);
				}
				break;
			case BUY:
				price = ba.getBid();
				update = price + price * threshold;
				if (update < limit) {
					order.setActivationLimit(update);
					updateOrder(order);
				}
				break;
		}
	}

	/**
	 * Validate symbols against equality. Symbols for order and price event
	 * shall be the same.
	 * 
	 * @param order - order to check
	 * @param pe - price event to check
	 */
	private void checkSymbols(TrailingStop order, PriceEvent pe) {
		Symbol os = order.getPaper().getSymbol();
		Symbol es = pe.getQuote().getSymbol();
		if (os != null || es != null || os != es) {
			throw new RuntimeException(
					"Order symbol '" + os + "' does not match price " +
					"event symbol '" + es + "'");
		}
	}

	/**
	 * Update order. This method will send request to brokerage.
	 * 
	 * @param order - order to update
	 * @return true if order has been updated, false otherwise
	 */
	public boolean updateOrder(Order order) {
		Brokerage brokerage = Brokerage.getDefault();
		Account account = brokerage.getAccount(order);
		if (account != null) {
			return account.updateOrder(order);
		} else {
			return false;
		}
	}
}
